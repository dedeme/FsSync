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
import java.util.Map;

/**
 *
 * @version 1.0
 * @since 27-Nov-2014
 * @author deme
 */
public class ServerSync {

  public static void sync(File f) throws FsSyncException {

    String serverName = f.getName();
    long lastSync = f.lastModified();

    Map<String, String> mp = Io.readMap(f);
    String type = mp.get("type");
    if (type == null)
      throw new FsSyncException(String.format(
        "Server '%s': Parameter 'type' is missing",
        serverName
      ));

    String localRoot = mp.get("local-root");
    if (localRoot == null)
      throw new FsSyncException(String.format(
        "Server '%s': Parameter 'local-root' is missing",
        serverName
      ));

    String remoteRoot = mp.get("remote-root");
    if (remoteRoot == null)
      throw new FsSyncException(String.format(
        "Server '%s': Parameter 'remote-root' is missing",
        serverName
      ));

    File fLocalRoot = new File(localRoot);
    if (!fLocalRoot.isDirectory())
      throw new FsSyncException(String.format(
        "Server '%s': Parameter 'local-root' is not a directory",
        serverName
      ));

    LocalSys localSys = new LocalSys(fLocalRoot);

    switch (type) {
      case "local":
        local(serverName, localSys, remoteRoot, lastSync);
        break;
      default:
        throw new FsSyncException(String.format(
          "Server '%s': Value '%s' for parameter 'type' is not valid",
          serverName,
          type
        ));
    }

    Io.write(f, Io.read(f));
  }

  static void local (
    String serverName, LocalSys localSys, String remoteRoot, long lastSync
  ) throws FsSyncException {
    File fRemoteRoot = new File(remoteRoot);
    if (!fRemoteRoot.getParentFile().isDirectory())
      throw new FsSyncException(String.format(
        "Server '%s': Parent directory of 'server-root' does not exists",
        serverName
      ));

    RemoteLocal r = new RemoteLocal(fRemoteRoot, lastSync);

    Local.sync(localSys.list(), r.list());
  }

}
