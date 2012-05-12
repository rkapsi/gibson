package org.ardverk.gibson.dashboard;

import org.apache.commons.lang.builder.ToStringBuilder;

import play.data.validation.Constraints.Required;

public class TypeItem implements Countable {

  @Required
  public final String typeName;
  
  @Required
  public final long count;
  
  public TypeItem(String typeName, long count) {
    this.typeName = typeName;
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
