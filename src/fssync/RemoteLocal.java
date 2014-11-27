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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Repository in the same directory to local data.
 *
 * @version 1.0
 * @since 27-Nov-2014
 * @author deme
 */
public class RemoteLocal {

  File root;
  long lastSynchronization;

  /**
   *
   * @param root Root of repository
   * @param lastSync Last time which synchronizations was made.
   */
  public RemoteLocal(File root, long lastSync) {
    this.root = root;
    lastSynchronization = lastSync;
  }

  void addFile(List<Remote> l, File root, String path) {
    R fls = new R(root, path);
    l.add(fls);
    if (fls.file.isDirectory()) {
      for (String str : fls.file.list()) {
        addFile(l, root, new File(new File(path), str).toString());
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
    public Remote make(String path) {
      return new R(root, path);
    }

    @Override
    public String getPath() {
      return path;

    }

    @Override
    public boolean exists(String path) {
      return file.exists();
    }

    @Override
    public long lastSync() {
      return lastSynchronization;
    }

    @Override
    public OutputStream outputStream() throws Exception {
      return new FileOutputStream(file);
    }

    @Override
    public void mkdir() throws Exception {
      file.mkdir();
    }

    @Override
    public String delete() {
      file.delete();
      return "";
    }
  }
}
