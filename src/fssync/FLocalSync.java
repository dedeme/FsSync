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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @version 1.0
 * @since 13-Nov-2014
 * @author deme
 */
public class FLocalSync implements FileSync {

  File root;
  String path;
  File file;

  public FLocalSync(File root, String path) {
    this.root = root;
    this.path = path;
    file = new File(root, path);
  }

  /**
   * Creates a FileSync from a path. This function is used to
   * copy
   * @param path Path of new FileSync
   * @return A new FileSync
   */
  @Override
  public FileSync make (String path) {
    return new FLocalSync(root, path);
  }

  public File getRoot() {
    return root;
  }

  /**
   * File Path
   *
   * @return File Path
   */
  @Override
  public String getPath() {
    return path;
  }

  /**
   * Time of last modification
   *
   * @return Time of last modification
   */
  @Override
  public long lastModified() {
    return file.lastModified();
  }

  /**
   * Indicates if this is a directory
   *
   * @return 'true', if this is a directory. 'false' otherwise
   */
  @Override
  public boolean isDirectory() {
    return file.isDirectory();
  }

  /**
   * Input stream bound to this
   *
   * @return Input stream bound to this
   * @throws java.io.FileNotFoundException
   */
  @Override
  public InputStream inputStream() throws FileNotFoundException {
    return new FileInputStream(file);
  }

  /**
   * Output stream bound to this
   *
   * @return Output stream bound to this
   * @throws java.io.FileNotFoundException
   */
  @Override
  public OutputStream outputStream() throws FileNotFoundException {
    return new FileOutputStream(file);
  }

  /**
   * Creates a directory
   *
   * @throws Exception
   */
  @Override
  public void mkdir() throws Exception {
    file.mkdir();
  }

  /**
   * Deletes this
   *
   * @return If operation succeeded an empty string, else an error message.
   */
  @Override
  public String delete() {
    file.delete();
    return "";
  }

  static void addFile(List<FileSync> l, File root, String path) {
    FLocalSync fls = new FLocalSync(root, path);
    l.add(fls);
    if (fls.file.isDirectory()) {
      for (String str : fls.file.list()) {
        addFile(l, root, new File(new File(path), str).toString());
      }
    }
  }

  /**
   * Returns a descendent list (first directories) prepared to copy operations
   * from a source directory to a target directory.
   * @param root Root source directory
   * @return Descendent list of source directory
   */
  public static List<FileSync> sourceList(File root) {
    ArrayList<FileSync> r = new ArrayList<>();
    addFile(r, root, "");
    return r;
  }

  /**
   * Returns an ascendent list (first files) prepared to delete operations in
   * a target directory
   * @param root Root target directory
   * @return Ascendent list of target directory
   */
  public static List<FileSync> targetList(File root) {
    ArrayList<FileSync> r = new ArrayList<>();
    addFile(r, root, "");
    Collections.reverse(r);
    return r;
  }
}
