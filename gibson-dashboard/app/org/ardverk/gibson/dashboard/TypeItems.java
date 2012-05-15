package org.ardverk.gibson.dashboard;

import java.util.List;

import play.data.validation.Constraints.Required;

public class TypeItems {

  @Required
  public final List<? extends TypeItem> elements;
  
  @Required
  public final long count;

  public TypeItems(List<? extends TypeItem> elements, long count) {
    this.elements = elements;
    this.count = count;
  }
  
  public boolean isEmpty() {
    return size() == 0;
  }
  
  public int size() {
    return elements != null ? elements.size() : 0;
  }
}
