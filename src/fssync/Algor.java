/*
 * Copyright 11-nov-2014 ºDeme
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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @version 1.0
 * @since 11-Nov-2014
 * @author deme
 */
public class Algor {
  static <S, T> String operate (
    List<S> source,
    List<T> target,
    BiFunction<S, Iterable<T>, String> mustOperate,
    Function<S, String> operation
  ){
    String r = "";
    for (S s : source) {
      String must = mustOperate.apply(s, target);
      if (must == null)
        r = operation.apply(s);
      else
        r = must;
      if (!r.equals(""))
        break;
    }
    return r;
  }

  /**
   * Synchronizes elements of source with elements of target.
   * @param <S> Type of source element (original)
   * @param <T> Type of target element (copy)
   * @param source List of elements S
   * @param target List of elements T
   * @param mustCopy Indicates if a copy has to be made. Returns:
   * <dl>
   * <dt>null</dt><dd>Copy has to be made.</dd>
   * <dt>Empty string</dt><dd>Copy has not to be made.</dd>
   * <dt>message</dt><dd>An error has happened.</dd>
   * </dl>
   * @param copy Operation to actualize an element which is obsolete.
   * @param mustDelete Indicates if a deletion has to be made. Returns:
   * <dl>
   * <dt>null</dt><dd>Deletion has to be made.</dd>
   * <dt>Empty string</dt><dd>Deletion has not to be made.</dd>
   * <dt>message</dt><dd>An error has happened.</dd>
   * </dl>
   * @param delete Operation to delete an element in copy which is not in
   *   origen.
   * @return An empty string if synchronization was successful or a message
   *   if an error happened.
   */
  public static <S, T> String sync(
    List<S> source,
    List<T> target,
    BiFunction<S, Iterable<T>, String> mustCopy,
    Function<S, String> copy,
    BiFunction<T, Iterable<S>, String> mustDelete,
    Function<T, String> delete
  ) {
    String r = operate(source, target, mustCopy, copy);
    if (r.equals("")) {
      r = operate(target, source, mustDelete, delete);
    }
    return r;
  }
}
