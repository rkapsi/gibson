package org.ardverk.gibson.dashboard;

import org.apache.commons.lang.builder.ToStringBuilder;

import play.data.validation.Constraints.Required;

abstract class Item implements Countable {

  @Required
  public final long count;

  public Item(long count) {
    this.count = count;
  }
  
  @Override
  public long getCount() {
    return count;
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).toString();
  }
}
