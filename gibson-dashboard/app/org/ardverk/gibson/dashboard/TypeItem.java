package org.ardverk.gibson.dashboard;

import play.data.validation.Constraints.Required;

public class TypeItem extends Item {

  @Required
  public final String typeName;
  
  public TypeItem(String typeName, long count) {
    super(count);
    this.typeName = typeName;
  }
}
