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

import java.io.File;

/**
 * Program first time initialization
 * @version 1.0
 * @since 16-Nov-2014
 * @author deme
 */
public class FirstTime {
  /**
   * Initializes program when is running at first time.
   */
  public static void run() {

    File servers = new File(FsSync.root, "servers");
    if (!servers.isDirectory())
      servers.mkdir();
  }

}
