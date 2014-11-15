/*
 * Copyright 10-nov-2014 ºDeme
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

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import jcifs.smb.*;

/**
 *
 * @version 1.0
 * @since 10-Nov-2014
 * @author deme
 */
public class FsSync {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    String pss = ReadPass.show("Samba password:");

    jcifs.Config.setProperty("jcifs.netbios.wins", "192.168.1.2");
    NtlmPasswordAuthentication auth =
      new NtlmPasswordAuthentication(null, "deme", pss);
    SmbFileInputStream in
      = new SmbFileInputStream(
        new SmbFile("smb://server/home/www/dedeme.css", auth));
    byte[] b = new byte[8192];
    int n;
    while ((n = in.read(b)) > 0) {
      System.out.write(b, 0, n);
    }
    System.out.println("here");
  }

}
