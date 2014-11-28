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
 * <p>
 * Interface for synchronizing local data.</p>
 *
 * @version 1.0
 * @since 13-Nov-2014
 * @author deme
 */
public interface Local {

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

  static String copy(Local s, Remote t) {
    String r = "";
    try {
      if (s.isDirectory()) {
        if (t.exists() && !t.isDirectory()) {
          t.delete();
        }
        if (!t.exists()) {
          t.mkdir();
        }
      } else {
        if (t.exists()) {
          t.delete();
        }
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
    Local fsource, Iterable<Remote> fs
  ) {
    try {
      for (Remote ft : fs) {
        if (fsource.getPath().equals(ft.getPath())) {
          if ((fsource.isDirectory() && !ft.isDirectory())
            || (!fsource.isDirectory() && ft.isDirectory())) {
            return null;
          }
          if (fsource.lastModified() > ft.lastSync()) {
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
    Remote ft, Iterable<Local> fs
  ) {
    try {
      String r = null;

      for (Local fsource : fs) {
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
    List<Local> source, List<Remote> target
  ) {
    try {
      Remote ft = target.get(0);
      return Algor.sync(
        source,
        target,
        (fs, i) -> {
          return mustCopy(fs, i);
        },
        (fs) -> {
          String r;
          try {
            r = copy(fs, ft.make(fs.isDirectory(), fs.getPath()));
          } catch (FsSyncException ex) {
            r = ex.getMessage();
          }
          if (r.equals("")) {
            System.out.println(String.format(
                "Copied '%s'", fs.getPath()));
          }
          return r;
        },
        (fr, i) -> {
          return mustDelete(fr, i);
        },
        (fr) -> {
          fr.delete();
          System.out.println(String.format(
              "Deleted '%s'", fr.getPath()));
          return "";
        }
      );

    } catch (Exception e) {
      return e.getMessage();
    }
  }
}