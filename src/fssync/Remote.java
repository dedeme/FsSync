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

import java.io.OutputStream;

/**
 * <p>
 * Interface for synchronizing remote data.</p>
 *
 * @version 1.0
 * @since 27-Nov-2014
 * @author deme
 */
public interface Remote {

  /**
   * Creates a Remote from a path. This function is used to copy
   *
   * @param path Path of new Remote
   * @return A new Local
   */
  public Remote make(String path);

  /**
   * File Path
   *
   * @return File Path
   */
  public String getPath();

  /**
   * Return 'true' if path exists
   *
   * @param path
   * @return
   */
  public boolean exists(String path);

  /**
   * Time of last synchronization
   *
   * @return Time of last synchronization
   */
  public long lastSync();

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

}
