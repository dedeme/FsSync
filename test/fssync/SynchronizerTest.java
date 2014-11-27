/*
 * Copyright 10-nov-2014 ºDeme
 *
 * This file is part of 'ftpsync'.
 *
 * 'ftpsync' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * 'ftpsync' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with 'ftpsync'.  If not, see <http://www.gnu.org/licenses/>.
 */
package fssync;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deme
 */
public class SynchronizerTest {

  Function<CharPos, String> makeCopy(ArrayList<CharPos> t) {
    return (CharPos s) -> {
      int pos = s.getPos();
      for (int i = t.size(); i <= pos; ++i) {
        t.add(new CharPos(' ', i));
      }
      t.set(s.getPos(), s);
      return "";
    };
  }

  Function<CharPos, String> makeDelete(ArrayList<CharPos> ts) {
    return (CharPos t) -> {
      ts.set(t.getPos(), null);
      return "";
    };
  }

  @Test
  public void SynchronizerTest() {
    BiFunction<CharPos, Iterable<CharPos>, String> mustCopy
      = (CharPos ch, Iterable<CharPos> it) -> {
        int count = 0;
        char chc = ch.getCh();
        int chp = ch.getPos();
        if (Character.isDigit(chc)) {
          return String.format("Character in position %d is a digit (%c)",
            chp, chc);
        }
        for (CharPos c : it) {
          if (chp == count) {
            char cc = c.getCh();
            if (chc != cc) {
              return null;
            }
          }
          ++count;
        }
        if (chp >= count) {
          return null;
        }
        return "";
      };

    BiFunction<CharPos, Iterable<CharPos>, String> mustDelete
      = (CharPos ch, Iterable<CharPos> it) -> {
        int count = 0;
        for (CharPos c : it) {
          ++count;
        }
        if (ch.getPos() >= count) {
          return null;
        }
        return "";
      };

    Function<String, ArrayList<CharPos>> toList
      = (String s) -> {
        ArrayList<CharPos> r = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
          r.add(new CharPos(s.charAt(i), i));
        }
        return r;
      };

    Function<ArrayList<CharPos>, String> toString
      = (ArrayList<CharPos> a) -> {
        StringBuilder s = new StringBuilder();
        a.stream().forEach((chp) -> {
          if (chp != null) {
            s.append(chp.getCh());
          }
        });
        return s.toString();
      };

    ArrayList<CharPos> s0 = toList.apply("");
    ArrayList<CharPos> t0 = toList.apply("");
    assertEquals("",
      Algor.sync(
        s0, t0,
        mustCopy, makeCopy(t0),
        mustDelete, makeDelete(t0)
      ));
    assertTrue(toString.apply(s0).equals(toString.apply(t0)));

    s0 = toList.apply("AbC");
    t0 = toList.apply("");
    assertEquals("",
      Algor.sync(
        s0, t0,
        mustCopy, makeCopy(t0),
        mustDelete, makeDelete(t0)
      ));
    assertTrue(toString.apply(s0).equals(toString.apply(t0)));

    s0 = toList.apply("");
    t0 = toList.apply("AbC");
    assertEquals("",
      Algor.sync(
        s0, t0,
        mustCopy, makeCopy(t0),
        mustDelete, makeDelete(t0)
      ));
    assertTrue(toString.apply(s0).equals(toString.apply(t0)));

    s0 = toList.apply("ABC");
    t0 = toList.apply("AbC");
    assertEquals("",
      Algor.sync(
        s0, t0,
        mustCopy, makeCopy(t0),
        mustDelete, makeDelete(t0)
      ));
    assertTrue(toString.apply(s0).equals(toString.apply(t0)));
    assertTrue("ABC".equals(toString.apply(t0)));

    s0 = toList.apply("ABC");
    t0 = toList.apply("sAbCmjr");
    assertEquals("",
      Algor.sync(
        s0, t0,
        mustCopy, makeCopy(t0),
        mustDelete, makeDelete(t0)
      ));
    assertTrue(toString.apply(s0).equals(toString.apply(t0)));
    assertTrue("ABC".equals(toString.apply(t0)));

    s0 = toList.apply("A0C");
    t0 = toList.apply("sAbCmjr");
    assertEquals("Character in position 1 is a digit (0)",
      Algor.sync(
        s0, t0,
        mustCopy, makeCopy(t0),
        mustDelete, makeDelete(t0)
      ));
    assertTrue("A0C".equals(toString.apply(s0)));
    assertTrue("AAbCmjr".equals(toString.apply(t0)));
  }

  @Test
  public void SynchronizerTest2() {
    assertEquals(3, 2 + 1);
  }

}

class CharPos {

  char ch;
  int pos;

  public CharPos(char ch, int pos) {
    this.ch = ch;
    this.pos = pos;
  }

  public char getCh() {
    return ch;
  }

  public int getPos() {
    return pos;
  }
}
