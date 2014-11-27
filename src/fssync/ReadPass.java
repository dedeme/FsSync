/*
 * Copyright 15-nov-2014 ºDeme
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Reader for passwords.
 *
 * @version 1.0
 * @since 15-Nov-2014
 * @author deme
 */
public class ReadPass {

  /**
   * Reads a password.
   *
   * @param message Prompt for input
   * @return A password or if user canceled input an empty string.
   */
  public static String show(
    String message
  ) {
    StringBuilder r = new StringBuilder();

    JDialog dg = new JDialog((Frame) null, "FsSync", true);

    JPanel pn1 = new JPanel(new BorderLayout());
    pn1.setBorder(new EmptyBorder(8, 8, 8, 8));

    JPanel pn2 = new JPanel();
    JLabel lb = new JLabel(message);
    lb.setFont(lb.getFont().deriveFont(Font.PLAIN));
    JPasswordField pass = new JPasswordField();
    pass.setPreferredSize(new Dimension(
      250,
      pass.getPreferredSize().height
    ));
    pn2.add(lb);
    pn2.add(pass);

    JPanel pn31 = new JPanel(new BorderLayout());
    JPanel pn32 = new JPanel();
    JButton cancel = new JButton("Cancel");
    cancel.setFont(cancel.getFont().deriveFont(Font.PLAIN));
    cancel.setPreferredSize(new Dimension(
      100,
      cancel.getPreferredSize().height
    ));
    cancel.addActionListener((e) -> {
      r.delete(0, r.length());
      for (int i = 0; i < pass.getPassword().length; ++i) {
        pass.getPassword()[i] = 0;
      }
      dg.setVisible(false);
    });
    cancel.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        r.delete(0, r.length());
        for (int i = 0; i < pass.getPassword().length; ++i) {
          pass.getPassword()[i] = 0;
        }
        dg.setVisible(false);
      }
    });
    final JButton accept = new JButton("Accept");
    accept.setFont(accept.getFont().deriveFont(Font.PLAIN));
    accept.setPreferredSize(new Dimension(
      100,
      accept.getPreferredSize().height
    ));
    accept.addActionListener((e) -> {
      r.append(new String(pass.getPassword()));
      for (int i = 0; i < pass.getPassword().length; ++i) {
        pass.getPassword()[i] = 0;
      }
      dg.setVisible(false);
    });
    accept.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        r.append(new String(pass.getPassword()));
        for (int i = 0; i < pass.getPassword().length; ++i) {
          pass.getPassword()[i] = 0;
        }
        dg.setVisible(false);
      }
    });
    pass.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10) {
          accept.requestFocus();
        }
      }
    });

    pn32.add(cancel);
    pn32.add(accept);
    pn31.add(pn32, BorderLayout.EAST);

    pn1.add(pn2, BorderLayout.NORTH);
    pn1.add(pn31, BorderLayout.SOUTH);
    dg.add(pn1);
    dg.pack();
    dg.setLocationRelativeTo(null);
    dg.setResizable(false);
    pass.requestFocus();
    dg.setVisible(true);
    dg.dispose();
    return r.toString();
  }
}
