package org.ardverk.gibson.dashboard;

import org.ardverk.gibson.Event;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultEventService.class)
public interface EventService {
  
  public void drop();
  
  public TypeItems getTypeItems();
  
  public EventItems getEventItems(String typeName);
  
  public EventItem getEvent(String typeName, String signature);
  
  public long getEventCount(Event event);
  
  public SearchItems query(String query);
}