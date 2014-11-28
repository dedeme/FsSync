/*
 * Copyright 27-nov-2014 ºDeme
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Repository in the same directory to local data.
 *
 * @version 1.0
 * @since 27-Nov-2014
 * @author deme
 */
public class RemoteLocal {

  File root;
  String[] ignore;
  long lastSynchronization;

  /**
   *
   * @param root Root of repository
   * @param ignore Paths which will be ignored.
   * @param lastSync Last time which synchronizations was made.
   */
  public RemoteLocal(File root, String[] ignore, long lastSync) {
    this.root = root;
    this.ignore = ignore;
    lastSynchronization = lastSync;
  }

  void addFile(List<Remote> l, File root, String path) {
    R fls = new R(root, path);
    l.add(fls);
    if (fls.file.isDirectory()) {
      for (String str : fls.file.list()) {
        if (Arrays.stream(ignore).anyMatch(i -> {
          return i.equals(
            path.equals("") ? str : path + File.separator + str);
        })) {
          // continue
        } else {
          if (path.equals("")) {
            addFile(l, root, str);
          } else {
            addFile(l, root, path + File.separator + str);
          }
        }
      }
    }
  }

  /**
   * Returns an ascendent list (first files) prepared to delete operations.
   *
   * @return Ascendent list of target directory
   */
  public List<Remote> list() {
    ArrayList<Remote> r = new ArrayList<>();
    addFile(r, root, "");
    Collections.reverse(r);
    return r;
  }

  class R implements Remote {
    String path;
    File file;

    public R(File root, String path) {
      this.path = path;
      file = new File(root, path);
    }

    @Override
    public Remote make(boolean isDirectory, String path) {
      return new R(root, path);
    }

    @Override
    public String getPath() {
      return path;

    }

    @Override
    public boolean exists() {
      return file.exists();
    }

    @Override
    public boolean isDirectory() {
      return file.isDirectory();
    }

    @Override
    public long lastSync() {
      return lastSynchronization;
    }

    @Override
    public OutputStream outputStream() throws FsSyncException {
      try {
        return new FileOutputStream(file);
      } catch (FileNotFoundException ex) {
        throw new FsSyncException(ex.getMessage());
      }
    }

    @Override
    public void mkdir() throws FsSyncException {
      file.mkdir();
    }

    void delete (File f) {
      if (f.isDirectory()){
        Arrays.stream(f.listFiles()).forEach(fl -> delete(fl));
      }
      f.delete();
    }

    @Override
    public void delete() {
      delete(file);
    }
  }
}
