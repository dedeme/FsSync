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

import java.io.File;
import java.io.IOException;

/**
 * Main class
 *
 * @version 1.0
 * @since 10-Nov-2014
 * @author deme
 */
public class FsSync {

  /**
   * Version number.
   */
  public static String version = "1.0.0";
  /**
   * User directory for program
   */
  public static File root = new File(
    new File(
      new File(System.getProperty("user.home")),
      ".dmJavaApp"),
    "fssync");
  /**
   * Configuration FileMap
   */
  public static FileMap conf = new FileMap(new File(FsSync.root, "conf.txt"),
    new String[]{
      "version=" + FsSync.version,
      "editor=gedit $"
    });

  public static Passwords passwords = new Passwords();

  static void showHelp() {
    System.out.println(Io.read("help.txt"));
  }

  static void showList() {
    System.out.println("Server list:");
    String[] servers = new File(root, "servers").list();
    if (servers.length == 0) {
      System.out.println("Without servers");
    } else {
      for (String s : servers) {
        System.out.println(s);
      }
    }
    System.out.println();
  }

  static void optionEditor(String[] args) throws FsSyncException {
    if (args.length == 1) {
      System.out.println("Current editor:\n" + conf.get("editor"));
    } else if (args.length == 2) {
      conf.put("editor", args[1]);
      System.out.println(String.format(
        "New editor is '%s':", args[1]));
      System.out.println();
    } else {
      throw new FsSyncException(
        "'editor' only admits one o zero arguments"
      );

    }
  }

  static void optionNewLocal(String args[]) throws FsSyncException {
    if (args.length == 2) {
      File target = new File(new File(root, "servers"), args[1]);
      if (target.isFile()) {
        throw new FsSyncException(String.format(
          "'new-local': Server '%s' already exists",
          args[1]
        ));
      }
      Io.write(target, Io.read("localTemplate.txt"));
      String[] command = conf.get("editor").split("\\s");
      for (int i = 0; i < command.length; ++i) {
        if (command[i].equals("$")) {
          command[i] = target.toString();
        }
      }
      try {
        Runtime.getRuntime().exec(command);
      } catch (IOException ex) {
        throw new FsSyncException(
          "It is not posible to launch the editor."
        );
      }
    } else {
      throw new FsSyncException(
        "'new-local' only admits one and only one argument"
      );
    }
  }

  static void optionNewSmb(String args[]) throws FsSyncException {
    if (args.length == 2) {
      File target = new File(new File(root, "servers"), args[1]);
      if (target.isFile()) {
        throw new FsSyncException(String.format(
          "'new-smb': Server '%s' already exists",
          args[1]
        ));
      }
      Io.write(target, Io.read("smbTemplate.txt"));
      String[] command = conf.get("editor").split("\\s");
      for (int i = 0; i < command.length; ++i) {
        if (command[i].equals("$")) {
          command[i] = target.toString();
        }
      }
      try {
        Runtime.getRuntime().exec(command);
      } catch (IOException ex) {
        throw new FsSyncException(
          "It is not posible to launch the editor."
        );
      }
    } else {
      throw new FsSyncException(
        "'new-smb' only admits one and only one argument"
      );
    }
  }

  static void optionNewFtp(String args[]) throws FsSyncException {
    if (args.length == 2) {
      File target = new File(new File(root, "servers"), args[1]);
      if (target.isFile()) {
        throw new FsSyncException(String.format(
          "'new-ftp': Server '%s' already exists",
          args[1]
        ));
      }
      Io.write(target, Io.read("ftpTemplate.txt"));
      String[] command = conf.get("editor").split("\\s");
      for (int i = 0; i < command.length; ++i) {
        if (command[i].equals("$")) {
          command[i] = target.toString();
        }
      }
      try {
        Runtime.getRuntime().exec(command);
      } catch (IOException ex) {
        throw new FsSyncException(
          "It is not posible to launch the editor."
        );
      }
    } else {
      throw new FsSyncException(
        "'new-ftp' only admits one and only one argument"
      );
    }
  }

  static void optionEdit(String args[]) throws FsSyncException {
    if (args.length == 2) {
      File target = new File(new File(root, "servers"), args[1]);
      if (!target.isFile()) {
        throw new FsSyncException(String.format(
          "'edit': Server '%s' does not exists",
          args[1]
        ));
      }
      String[] command = conf.get("editor").split("\\s");
      for (int i = 0; i < command.length; ++i) {
        if (command[i].equals("$")) {
          command[i] = target.toString();
        }
      }
      try {
        Runtime.getRuntime().exec(command);
      } catch (IOException ex) {
        throw new FsSyncException(
          "It is not posible to launch the editor."
        );
      }
    } else {
      throw new FsSyncException(
        "'edit' only admits one and only one argument"
      );
    }
  }

  static void optionDelete(String args[]) throws FsSyncException {
    if (args.length == 2) {
      File target = new File(new File(root, "servers"), args[1]);
      if (!target.isFile()) {
        throw new FsSyncException(String.format(
          "'delete': Server '%s' does not exists",
          args[1]
        ));
      }
      target.delete();
      System.out.println(String.format(
        "Server '%s' deleted:", args[1]));
      System.out.println();
    } else {
      throw new FsSyncException(
        "'delete' only admits one and only one argument"
      );
    }
  }

  static void optionShow(String args[]) throws FsSyncException {
    if (args.length == 2) {
      File target = new File(new File(root, "servers"), args[1]);
      if (!target.isFile()) {
        throw new FsSyncException(String.format(
          "'show': Server '%s' does not exists",
          args[1]
        ));
      }
      System.out.println(String.format(
        "Showing server '%s':\n", args[1]));
      System.out.println(Io.read(target));
      System.out.println();
    } else {
      throw new FsSyncException(
        "'show' only admits one and only one argument"
      );
    }
  }

  static void optionSync(String args[]) throws FsSyncException {
    if (args.length == 2) {
      File target = new File(new File(root, "servers"), args[1]);
      if (!target.isFile()) {
        throw new FsSyncException(String.format(
          "'sync': Server '%s' does not exists",
          args[1]
        ));
      }
      ServerSync.sync(target);
      System.out.println(String.format(
        "Server '%s' synchronized", args[1]));
      System.out.println();
    } else {
      throw new FsSyncException(
        "'sync' only admits one and only one argument"
      );
    }
  }

  /**
   * Entry point
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    FirstTime.run();
    try {
      if (args.length == 0) {
        showHelp();
        return;
      }
      switch (args[0]) {
        case "help":
          showHelp();
          break;
        case "list":
          showList();
          break;
        case "editor":
          optionEditor(args);
          break;
        case "new-local":
          optionNewLocal(args);
          break;
        case "new-smb":
          optionNewSmb(args);
          break;
        case "new-ftp":
          optionNewFtp(args);
          break;
        case "edit":
          optionEdit(args);
          break;
        case "delete":
          optionDelete(args);
          break;
        case "show":
          optionShow(args);
          break;
        case "sync":
          optionSync(args);
          break;
        default:
          throw new FsSyncException(String.format(
            "'%s': option unknown",
            args[0]
          ));
      }
    } catch (FsSyncException e) {
      e.show();
      System.out.println("\nFor help use:\nFsSync help\n");
    }
  }

}
