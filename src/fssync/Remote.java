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

import java.io.File;
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
   * @param isDirectory 'true' if 'path' is a directory
   * @param path Path of new Remote
   * @return A new Local
   * @throws fssync.FsSyncException
   */
  public Remote make(boolean isDirectory, String path) throws FsSyncException;

  /**
   * Returns the server name.
   *
   * @return Server name
   */
  public String getServerName();

  /**
   * File Path
   *
   * @return File Path
   */
  public String getPath();

  /**
   * Return 'true' if path exists
   *
   * @return
   * @throws fssync.FsSyncException
   */
  public boolean exists() throws FsSyncException;

  /**
   * Indicates if this is a directory
   *
   * @return 'true', if this is a directory. 'false' otherwise
   * @throws fssync.FsSyncException
   */
  public boolean isDirectory() throws FsSyncException;

  /**
   * Time of last synchronization
   *
   * @return Time of last synchronization
   */
  public long lastSync();

  /**
   * Output stream bound to this (not ftp Remote)
   *
   * @return Output stream bound to this or null (ftp)
   * @throws fssync.FsSyncException
   */
  public OutputStream outputStream() throws FsSyncException;

  /**
   * Upload a file (ftp Remote)
   *
   * @param f File to upload
   * @return true (ftp) if this function is available.
   * @throws fssync.FsSyncException
   */
  public boolean update (File f) throws FsSyncException;

  /**
   * Creates a directory
   *
   * @throws fssync.FsSyncException
   */
  public void mkdir() throws FsSyncException;

  /**
   * Deletes this
   */
  public void delete();

}
