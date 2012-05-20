/*
 * Copyright 2012 Will Benedict, Felix Berger and Roger Kapsi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

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
