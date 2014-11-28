/*
 * Copyright 28-nov-2014 ºDeme
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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 *
 * @version 1.0
 * @since 28-Nov-2014
 * @author deme
 */
public class RemoteSmb {

  SmbFile root;
  String[] ignore;
  long lastSynchronization;

  public RemoteSmb(
    String root, String[] ignore, long lastSync,
    String machine, String domain, String user, String passKey
  ) throws FsSyncException {
    this.ignore = ignore;
    lastSynchronization = lastSync;

    jcifs.Config.setProperty("jcifs.netbios.wins", machine);
    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
      domain, user, FsSync.passwords.get(passKey));
    try {
      this.root = new SmbFile("smb://" + machine + root + "/", auth);
    } catch (MalformedURLException ex) {
      throw new FsSyncException(ex.getMessage());
    }
  }

  void addFile(
    List<Remote> l, SmbFile root, String path
  ) throws FsSyncException {
    try {
      R fls = new R(root, path);
      l.add(fls);
      if (fls.isDirectory()) {
        for (String str : fls.file.list()) {
          if (Arrays.stream(ignore).anyMatch(i -> {
            return i.equals(
              path.equals("") ? str : path + "/" + str);
          })) {
            // continue
          } else {
            if (path.equals("")) {
              addFile(l, root, str);
            } else {
              addFile(l, root, path + "/" + str);
            }
          }
        }
      }
    } catch (SmbException ex) {
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
    addFile(r, root, "");
    Collections.reverse(r);
    return r;
  }

  class R implements Remote {

    String path;
    SmbFile file;
    boolean isDirectory;

    public R(SmbFile root, String path) throws FsSyncException {
      this.path = path;
      if (path.equals("")) {
        file = root;
        isDirectory = true;
      } else {
        try {
          file = new SmbFile(root, path);
          isDirectory = file.isDirectory();
          if (isDirectory) {
            file = new SmbFile(root, path + "/");
          }
        } catch (Exception ex) {
          throw new FsSyncException(ex.getMessage());
        }
      }
    }

    public R(
      SmbFile root, String path, boolean isDirectory
    ) throws FsSyncException {
      this.path = path;
      try {
        if (isDirectory) {
          file = new SmbFile(root, path);
          if (!file.isDirectory()) {
            this.isDirectory = false;
          } else {
            file = new SmbFile(root, path + "/");
            this.isDirectory = true;
          }
        } else {
          file = new SmbFile(root, path + "/");
          if (file.isDirectory()) {
            this.isDirectory = true;
          } else {
            file = new SmbFile(root, path);
            this.isDirectory = false;
          }
        }
      } catch (Exception ex) {
        throw new FsSyncException(ex.getMessage());
      }
    }

    @Override
    public Remote make(
      boolean isDirectory, String path
    ) throws FsSyncException {
      return new R(root, path, isDirectory);
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public boolean exists() throws FsSyncException {
      try {
        return file.exists();
      } catch (SmbException ex) {
        throw new FsSyncException(file + "\n" + ex.getMessage());
      }
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
    public OutputStream outputStream() throws FsSyncException {
      try {
        return new SmbFileOutputStream(file);
      } catch (Exception ex) {
        throw new FsSyncException(ex.getMessage());
      }
    }

    @Override
    public void mkdir() throws FsSyncException {
      try {
        file.mkdir();
      } catch (SmbException ex) {
        throw new FsSyncException(ex.getMessage());
      }
    }

    void delete(SmbFile f) throws FsSyncException {
      try {
        if (f.isDirectory()) {
          for (SmbFile fl : f.listFiles()) {
            delete(fl);
          }
        }
        f.delete();
      } catch (SmbException ex) {
        throw new FsSyncException(ex.getMessage());
      }
    }

    @Override
    public void delete() {
      try {
        file.delete();
      } catch (SmbException ex) {
        System.out.println(file.getPath() + "\n" +  ex.getMessage());
      }
    }
  }

}
