/*
 * Copyright 13-nov-2014 ºDeme
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Source directory for synchronization.
 *
 * @version 1.0
 * @since 13-Nov-2014
 * @author deme
 */
public class LocalSys {

  File root;

  /**
   *
   * @param root Root directory to synchronize
   */
  public LocalSys(File root) {
    this.root = root;
  }

  void addFile(List<Local> l, File root, String path) throws FsSyncException {
    L fls = new L(root, path);
    l.add(fls);
    if (fls.isDirectory()) {
      for (String str : fls.file.list()) {
        if (Io.wrongName(str)) {
          System.out.println(String.format(
            "Warning:\n"
            + "File '%s' in directory '%s' has non-ASCII characters\n"
            + "and can not be syncrhonized",
            str,
            fls.file.toString()
          ));
        } else {
          addFile(l, root, new File(new File(path), str).toString());
        }
      }
    }
  }

  /**
   * Returns a descendent list (first directories) prepared to copy operations.
   *
   * @return Descendent list of source directory
   * @throws fssync.FsSyncException
   */
  public List<Local> list() throws FsSyncException {
    ArrayList<Local> r = new ArrayList<>();
    addFile(r, root, "");
    return r;
  }

  class L implements Local {

    String path;
    File file;

    public L(File root, String path) {
      this.path = path;
      file = new File(root, path);
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public long lastModified() {
      return file.lastModified();
    }

    @Override
    public boolean isDirectory() {
      return file.isDirectory();
    }

    @Override
    public InputStream inputStream() throws Exception {
      return new FileInputStream(file);
    }
  }

}
