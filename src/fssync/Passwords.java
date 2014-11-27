/*
 * Copyright 16-nov-2014 ºDeme
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

import java.util.HashMap;


/**
 * Utility for reading and saving passwords
 *
 * @version 1.0
 * @since 16-Nov-2014
 * @author deme
 */
public class Passwords {
  HashMap<String, String> passwords;

  public Passwords() {
    passwords = new HashMap<>();
  }

  /**
   * Reads a password for replace key in a 'Remote'
   *
   * @param key Password which is in a 'Remote'
   * @return Password entered by user
   */
  public String get (String key) {
    if (!passwords.keySet().contains(key)) {
      String r = ReadPass.show("Password for " + key + ":");
      passwords.put(key, r);
      return r;
    }
    return passwords.get(key);
  };
}
