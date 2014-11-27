/*
 * Copyright 07-abr-2014 ºDeme
 *
 * This file is part of 'dmLang-8.1.0'.
 *
 * 'dmLang-8.1.0' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * 'dmLang-8.1.0' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with 'dmLang-8.1.0'.  If not, see <http://www.gnu.org/licenses/>.
 */
package fssync;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * <p>
 * Class for reading and writing configuration files.</p>
 * <p>
 * Its working is similar to a HasMap.</p>
 * <p>
 * When the constructor is invoked for first time a text file is automatically
 * created with default values.</p>
 *
 * @version 1.0
 * @since 27-Nov-2014
 * @author deme
 */
public class FileMap implements AutoCloseable {

  File f;
  HashMap<String, String> mp;

  /**
   *
   * @param f Text file
   * @param defaultValues Values of 'f' when it is created for first time.
   */
  public FileMap(File f, String[] defaultValues) {
    this.f = f;
    mp = new HashMap<>();
    for (String s : (f.isFile()) ? Io.read(f).split("\n") : defaultValues) {
      int ix = s.indexOf("=");
      if (ix > 0) {
        String key = s.substring(0, ix).trim();
        if (!key.equals("")) {
          mp.put(key, s.substring(ix + 1).trim());
        }
      }
    }
    if (!f.isFile()) {
      if (!f.getParentFile().isDirectory()) {
        f.getParentFile().mkdirs();
      }
      wr();
    }
  }

  /**
   * Constructor without default values
   *
   * @param f
   */
  public FileMap(File f) {
    this(f, new String[]{});
  }

  /**
   *
   * @param f Text file
   * @param defaultValues Values of 'f' when it is created for first time.
   */
  public FileMap(File f, HashMap<String, String> defaultValues) {
    this.f = f;
    mp = defaultValues;
  }

  final void wr() {
    StringBuilder sb = new StringBuilder();
    mp.keySet().stream().forEach((k) -> {
      sb.append(k).append("=").append(mp.get(k)).append("\n");
    });
    Io.write(f, sb.toString());
  }

  /**
   * Adds a pair key - value or overwrites an existent one.
   *
   * @param key
   * @param value
   */
  public void put(String key, String value) {
    if (key.contains("=")) {
      throw new IllegalArgumentException("key must not contain '='");
    }
    mp.put(key, value);
    wr();
  }

  /**
   * Remove 'key'
   *
   * @param key
   */
  public void remove(String key) {
    mp.remove(key);
    wr();
  }

  /**
   * Returns the key set
   *
   * @return
   */
  public Set<String> keySet() {
    return mp.keySet();
  }

  /**
   * Returns the values
   * @return
   */
  public Collection<String> values() {
    return mp.values();
  }

  /**
   * Returns the value of 'key'
   * @param key
   * @return
   */
  public String get(String key) {
    return mp.get(key);
  }

  /**
   * <p>Method of convenience to Write data in file.</p>
   * <p>After invoking this function you can go on using 'this'.</p>
   */
  @Override
  public void close() {
    wr();
  }

  /**
   * <p>Returns a copy of 'this' which changes are not saved in file until you
   * invoke 'close()'.</p>
   * <p>This method is intended to reduce the number of writing operations on
   * disk.</p>
   * @return
   */
  public FileMap fast() {
    return new FileMap(f, mp) {
      @Override
      public void put(String key, String value) {
        if (key.contains("=")) {
          throw new IllegalArgumentException("key must not contain '='");
        }
        mp.put(key, value);
      }

      @Override
      public void remove(String key) {
        mp.remove(key);
      }
    };
  }
}
