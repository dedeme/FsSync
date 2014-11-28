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
import java.util.Arrays;
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

    String[] ignoreArr = new String[]{};
    String ignore = mp.get("ignore");
    if (ignore != null)
      ignoreArr = Arrays.stream(ignore.split(";")).map(i->{
        return i.trim();
      }).filter(i ->{
        return !i.equals("");
      }).toArray(String[]::new);

    LocalSys localSys = new LocalSys(fLocalRoot, ignoreArr);

    switch (type) {
      case "local":
        local(serverName, localSys, ignoreArr, remoteRoot, lastSync);
        break;
      case "smb":
        smb(serverName, localSys, ignoreArr, remoteRoot, lastSync, mp);
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
    String serverName, LocalSys localSys, String[] ignore,
    String remoteRoot, long lastSync
  ) throws FsSyncException {
    File fRemoteRoot = new File(remoteRoot);
    if (!fRemoteRoot.getParentFile().isDirectory())
      throw new FsSyncException(String.format(
        "Server '%s': Parent directory of 'server-root' does not exists",
        serverName
      ));

    RemoteLocal r = new RemoteLocal(fRemoteRoot, ignore, lastSync);

    String err = Local.sync(localSys.list(), r.list());
    if (!err.equals("")) {
      throw new FsSyncException(err);
    }
  }

  static void smb (
    String serverName, LocalSys localSys, String[] ignore,
    String remoteRoot, long lastSync, Map<String, String> mp
  ) throws FsSyncException {
    String machine = mp.get("machine");
    if (machine == null)
      throw new FsSyncException(String.format(
        "Server '%s': Parameter 'machine' is missing",
        serverName
      ));

    String domain = mp.get("domain");

    String user = mp.get("user");
    if (user == null)
      throw new FsSyncException(String.format(
        "Server '%s': Parameter 'user' is missing",
        serverName
      ));

    String passKey = mp.get("passKey");
    if (passKey == null)
      throw new FsSyncException(String.format(
        "Server '%s': Parameter 'passKey' is missing",
        serverName
      ));

    RemoteSmb r = new RemoteSmb(
      remoteRoot, ignore, lastSync,
      machine, domain, user, passKey
    );
    
    String err = Local.sync(localSys.list(), r.list());
    if (!err.equals("")) {
      throw new FsSyncException(err);
    }
  }
}
