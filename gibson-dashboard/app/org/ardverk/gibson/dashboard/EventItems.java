package org.ardverk.gibson.dashboard;

import java.util.List;

import play.data.validation.Constraints.Required;

public class EventItems extends Items {
  
  @Required
  public final String typeName;
  
  @Required
  public final List<? extends EventItem> elements;
  
  @Required
  public final long count;

  public EventItems(String typeName, List<? extends EventItem> elements, long count) {
    this.typeName = typeName;
    this.elements = elements;
    this.count = count;
  }
  
  @Override
  public int size() {
    return elements != null ? elements.size() : 0;
  }
}
