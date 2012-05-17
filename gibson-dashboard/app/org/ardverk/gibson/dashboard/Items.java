package org.ardverk.gibson.dashboard;


abstract class Items {

  public boolean isEmpty() {
    return size() == 0;
  }
  
  public abstract int size();
}
