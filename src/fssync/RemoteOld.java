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

import jcifs.smb.NtlmPasswordAuthentication;

/**
 *
 * @version 1.0
 * @since 16-Nov-2014
 * @author deme
 */
public class RemoteOld {

  public static enum Type {

    LOCAL, SAMBA, FTP
  }

  Type type;
  String machine;
  String user;
  String password;
  long lastTime;

  NtlmPasswordAuthentication auth;

  public RemoteOld(
    Type type, String machine, String user, String password, long lastTime
  ) {
    this.type = type;
    this.machine = machine;
    this.user = user;
    this.password = password;
    this.lastTime = lastTime;
  }

  public Type getType() {
    return type;
  }

  public String getMachine() {
    return machine;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public long getLastTime() {
    return lastTime;
  }

  public void connect(Passwords passwords) {
    if (type == Type.SAMBA) {
      String pss = passwords.get(password);
      jcifs.Config.setProperty("jcifs.netbios.wins", machine);
      auth = new NtlmPasswordAuthentication(null, user, pss);
    }
  }

  public String serialize() {
    return type.toString() + "\u0001"
      + machine + "\u0001"
      + user + "\u0001"
      + password + "\u0001"
      + String.valueOf(lastTime);
  }

  public static RemoteOld restore(String serial) {
    String[] vs = serial.split("\u0001");
    return new RemoteOld(
      vs[0].equals("LOCAL") ? Type.LOCAL
        : vs[0].equals("SAMBA") ? Type.SAMBA : Type.FTP,
      vs[1],
      vs[2],
      vs[3],
      new Long(vs[4])
    );
  }
}
