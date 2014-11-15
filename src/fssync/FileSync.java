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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>Interface for synchronizing two file systems.</p>
 * <p>Examples:</p>
 * <h3>Local directories</h3>
 * <pre>
 * String result = FileSync.sync(
 *   FLocalSync.sourceList(new File("/deme/www")),
 *   FLocalSync.targetList(new File("/deme/wwwxx"))
 * );
 * System.out.println(result);</pre>
 *
 * @version 1.0
 * @since 13-Nov-2014
 * @author deme
 */
public interface FileSync {

  /**
   * Creates a FileSync from a path. This function is used to copy
   *
   * @param path Path of new FileSync
   * @return A new FileSync
   */
  public FileSync make(String path);

  /**
   * File Path
   *
   * @return File Path
   */
  public String getPath();

  /**
   * Time of last modification
   *
   * @return Time of last modification
   */
  public long lastModified();

  /**
   * Indicates if this is a directory
   *
   * @return 'true', if this is a directory. 'false' otherwise
   */
  public boolean isDirectory();

  /**
   * Input stream bound to this
   *
   * @return Input stream bound to this
   * @throws java.lang.Exception
   */
  public InputStream inputStream() throws Exception;

  /**
   * Output stream bound to this
   *
   * @return Output stream bound to this
   * @throws java.lang.Exception
   */
  public OutputStream outputStream() throws Exception;

  /**
   * Creates a directory
   *
   * @throws Exception
   */
  public void mkdir() throws Exception;

  /**
   * Deletes this
   *
   * @return If operation succeeded an empty string, else an error message.
   */
  public String delete();

  static String copy(FileSync s, FileSync t) {
    String r = "";
    try {
      if (s.isDirectory()) {
        t.mkdir();
      } else {
        InputStream is = s.inputStream();
        OutputStream os = t.outputStream();
        try {
          byte[] buffer = new byte[1024]; // Adjust if you want
          int bytesRead;
          while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
          }
        } catch (Exception ex) {
          r = ex.getMessage();
        } finally {
          is.close();
          os.close();
        }
      }
    } catch (Exception ex) {
      r = ex.getMessage();
    }
    return r;
  }

  static String mustCopy(
    FileSync fsource, Iterable<FileSync> fs
  ) {
    try {
      for (FileSync ft : fs) {
        if (fsource.getPath().equals(ft.getPath())) {
          if (fsource.lastModified() > ft.lastModified()) {
            return null;
          }
          return "";
        }
      }

      return null;
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  static String mustDelete(
    FileSync ft, Iterable<FileSync> fs
  ) {
    try {
      String r = null;

      for (FileSync fsource : fs) {
        if (fsource.getPath().equals(ft.getPath())) {
          return "";
        }
      }

      return r;
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  public static String sync(
    List<FileSync> source, List<FileSync> target
  ) {
    try {
      FileSync ft = target.get(0);
      return Algor.sync(
        source,
        target,
        (f, i) -> {
          return mustCopy(f, i);
        },
        (f) -> {
          return copy(f, ft.make(f.getPath()));
        },
        (f, i) -> {
          return mustDelete(f, i);
        },
        (f) -> {
          return f.delete();
        }
      );

    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
