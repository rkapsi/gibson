package org.ardverk.gibson.dashboard;

import java.util.Comparator;

interface Countable {

  /**
   * A {@link Comparator} that sorts objects that implement the {@link Countable} interface in descending order.
   */
  public static final Comparator<Countable> DESCENDING = new Comparator<Countable>() {
    @Override
    public int compare(Countable o1, Countable o2) {
      long diff = o2.getCount() - o1.getCount();
      
      if (diff < 0L) {
        return -1;
      } else if (diff > 0L) {
        return 1;
      }
      return 0;
    }
  };
  
  /**
   * Returns the object's count.
   */
  public long getCount();
}
