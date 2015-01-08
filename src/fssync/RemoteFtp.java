/*
 * Copyright 29-nov-2014 ºDeme
 *
 * This file is part of 'fssync'.
 *
 * 'fssync' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * 'fssync' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with 'fssync'.  If not, see <http://www.gnu.org/licenses/>.
 */
package fssync;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;
import java.io.File;

/**
 *
 * @version 1.0
 * @since 29-Nov-2014
 * @author deme
 */
public class RemoteFtp {

  int repeatLimit = 10;

  FTPClient client;
  String root;
  String[] ignore;
  long lastSynchronization;

  String serverName;
  String machine;
  String port;
  String account;
  String user;
  String passKey;

  /**
   *
   * @param serverName
   * @param root
   * @param ignore
   * @param lastSync
   * @param machine
   * @param port
   * @param user
   * @param passKey
   * @param account
   * @throws FsSyncException
   */
  public RemoteFtp(
    String serverName, String root, String[] ignore, long lastSync,
    String machine, String port, String user, String passKey, String account
  ) throws FsSyncException {
    this.serverName = serverName;
    this.root = root;
    this.ignore = ignore;
    lastSynchronization = lastSync;

    this.machine = machine;
    this.port = port;
    this.account = account;
    this.user = user;
    this.passKey = passKey;

    try {
      connect();
    } catch (Exception ex) {
      throw new FsSyncException(ex.getMessage());
    }
  }

  final void op0(Supplier<String> s) throws FsSyncException {
    int count = 0;
    String err = "";

    while (count < repeatLimit) {
      err = s.get();
      if (err == null) {
        return;
      }
      disconnect();
      connect();
      ++count;
    }
    throw new FsSyncException(err);
  }

  final <T> T op(Supplier<T> s, String message) throws FsSyncException {
    int count = 0;

    while (count < repeatLimit) {
      T r = s.get();
      if (r != null) {
        return r;
      }
      disconnect();
      connect();
      ++count;
    }
    throw new FsSyncException(message);
  }

  final void connect() throws FsSyncException {
    try {
      client = new FTPClient();

      if (port != null) {
        client.connect(machine, new Integer(port));
      } else {
        client.connect(machine);
      }

      if (account != null) {
        client.login(user, FsSync.passwords.get(passKey), account);
      } else {
        client.login(user, FsSync.passwords.get(passKey));
      }

      client.setPassive(true);
      client.setType(FTPClient.TYPE_BINARY);
    } catch (Exception ex) {
      throw new FsSyncException(ex.getMessage());
    }
  }

  public void disconnect() {
    try {
      if (client.isConnected()) {
        client.disconnect(true);
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  FTPFile[] list(String path) throws FsSyncException {
    return op(() -> {
      try {
        return client.list(path);
      } catch (Exception ex) {
        return null;
      }
    }, "Error reading '" + path + "'");
  }

  void addFile(
    List<Remote> l, FTPFile file, String path
  ) throws FsSyncException {
    try {
      R fls = new R(file, path);
      l.add(fls);
      if (fls.isDirectory()) {
        for (FTPFile f : list(fls.absolutePath)) {
          String str = f.getName();
          if (str.equals(".") || str.equals("..")) {
            // continue;
          } else if (Arrays.stream(ignore).anyMatch(i -> {
            return i.equals(
              path.equals("") ? str : path + "/" + str);
          })) {
            // continue
          } else {
            if (path.equals("")) {
              addFile(l, f, str);
            } else {
              addFile(l, f, path + "/" + str);
            }
          }
        }
      }
    } catch (Exception ex) {
      throw new FsSyncException(ex.getMessage());
    }
  }

  /**
   * Returns an ascendent list (first files) prepared to delete operations.
   *
   * @return Ascendent list of target directory
   * @throws fssync.FsSyncException
   */
  public List<Remote> list() throws FsSyncException {
    ArrayList<Remote> r = new ArrayList<>();
    addFile(r, null, "");
    Collections.reverse(r);
    return r;
  }

  class R implements Remote {

    String path;
    FTPFile file;
    String absolutePath;
    boolean isDirectory;

    public R(FTPFile file, String path) {
      this.path = path;
      this.file = file;
      isDirectory = true;
      if (file != null) {
        isDirectory = file.getType() == FTPFile.TYPE_DIRECTORY;
      }
      if (path.equals("")) {
        absolutePath = root;
      } else {
        absolutePath = root + (root.endsWith("/") ? "" : "/") + path;
      }
    }

    @Override
    public Remote make(
      boolean isDirectory, String path
    ) throws FsSyncException {
      R r = new R(null, path);
      r.isDirectory = isDirectory;
      return r;
    }

    @Override
    public String getServerName() {
      return serverName;
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public boolean exists() throws FsSyncException {
      FTPFile[] fs = list(absolutePath);
      return fs != null && fs.length > 0;
    }

    @Override
    public boolean isDirectory() throws FsSyncException {
      return isDirectory;
    }

    @Override
    public long lastSync() {
      return lastSynchronization;
    }

    @Override
    public boolean update(File f) throws FsSyncException {
      try {
        client.changeDirectory(
          absolutePath.substring(0, absolutePath.lastIndexOf("/")));
        client.upload(f);
        return true;
      } catch (Exception ex) {
        throw new FsSyncException(ex.getMessage());
      }
    }

    @Override
    public OutputStream outputStream() throws FsSyncException {
      return null;
    }

    @Override
    public void mkdir() throws FsSyncException {
      op0(() -> {
        try {
          client.createDirectory(absolutePath);
          return null;
        } catch (Exception ex) {
          return ex.getMessage();
        }
      });
    }

    void delete(R f) throws FsSyncException {
      try {
        if (f.isDirectory()) {
          for (FTPFile ff : list(f.absolutePath)) {
            String fl = ff.getName();
            if (fl.equals(".") || fl.equals("..")) {
              // continue;
            } else if (f.path.equals("")) {
              delete(new R(ff, fl));
            } else {
              delete(new R(ff, f.path + "/" + fl));
            }
          }
          client.deleteDirectory(f.absolutePath);
        } else {
          client.deleteFile(f.absolutePath);
        }
      } catch (Exception ex) {
        throw new FsSyncException(ex.getMessage());
      }
    }

    @Override
    public void delete() {
      try {
        delete(this);
      } catch (Exception ex) {
        System.out.println(path + "\n" + ex.getMessage());
      }
    }
  }
}
