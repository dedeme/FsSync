/*
 * Copyright 22-nov-2014 ºDeme
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

/**
 * Program exception
 *
 * @version 1.0
 * @since 22-Nov-2014
 * @author deme
 */
public class FsSyncException extends Exception {

  public FsSyncException(String message) {
    super(message);
  }

  /**
   * Shows exception message on console
   */
  public void show() {
    System.out.println(
      "\n"
      + "FsSync exception:\n"
      + getMessage());
  }
}
