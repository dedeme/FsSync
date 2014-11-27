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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for reading and writing text files ('UTF-8' format).
 *
 * @version 1.0
 * @since 27-Nov-2014
 * @author deme
 */
public class Io {

  /**
   * Reads a UTF-8 text file in an only string.
   *
   * @param f
   * @return
   */
  public static String read(InputStream f) {
    try {
      StringBuilder sb = new StringBuilder();
      try (BufferedReader rd = new BufferedReader(
        new InputStreamReader(f, "UTF-8")
      )) {
        String l = rd.readLine();
        while (l != null) {
          sb.append(l).append("\n");
          l = rd.readLine();
        }
        return sb.toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return "???";
    }
  }

  /**
   * Reads a UTF-8 text file in an only string.
   *
   * @param f
   * @return
   */
  public static String read(File f) {
    try {
      return read(new FileInputStream(f));
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
      return "???";
    }
  }

  /**
   * Reads a UTF-8 text file in an only string.
   *
   * @param resource
   * @return
   */
  public static String read(String resource) {
    try {
      return read(
        ClassLoader.getSystemResourceAsStream("resources/" + resource));
    } catch (Exception ex) {
      ex.printStackTrace();
      return "???";
    }
  }

  /**
   * Reads a server configuration file.
   * @param f File to read
   * @return key-values of configuration such as 'path', 'lastSync' ...
   */
  public static Map<String, String> readMap(File f) {
    HashMap<String, String> r = new HashMap<>();

    for (String l : Io.read(f).split("\n")) {
      l = l.trim();
      if (l.equals("") || l.charAt(0) == '#')
        continue;

      int ix = l.indexOf("=");
      if (ix > 0) {
        String key = l.substring(0, ix).trim();
        if (!key.equals("")) {
          String value = l.substring(ix + 1).trim();
          if (!value.equals(""))
            r.put(key, value);
        }
      }
    }

    return r;
  }

  /**
   * Writes 's' in 'f'. 'f' is overwritten and automatically closed.
   *
   * @param f
   * @param s
   */
  public static void write(File f, String s) {
    try {
      try (PrintWriter pw = new PrintWriter(f, "UTF-8")) {
        pw.print(s);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static boolean wrongName(String n) {
    byte[] bs = n.getBytes();
    for (int i= 0; i < bs.length; ++i)
      if (bs[i] == 63)
        return true;
    return false;
  }
}
