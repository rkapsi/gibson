package org.ardverk.gibson.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ardverk.gibson.core.Event;
import org.ardverk.gibson.core.EventUtils;

@Singleton
class DefaultEventService implements EventService {
  
  @Inject
  private EventDAO eventDAO;
  
  @Override
  public void drop() {
    eventDAO.clear();
  }

  @Override
  public TypeItems getTypeItems() {
    List<String> typeNames = eventDAO.getTypeNames();
    List<TypeItem> dst = new ArrayList<TypeItem>(typeNames.size());
    
    long total = 0L;
    for (String typeName : typeNames) {
      long count = eventDAO.getTypeNameCount(typeName);
      dst.add(new TypeItem(typeName, count));
      total += count;
    }
    
    Collections.sort(dst, Countable.DESCENDING);
    return new TypeItems(dst, total);
  }

  @Override
  public EventItems getEventItems(String typeName) {
    List<Event> events = eventDAO.getEvents(typeName);
    List<EventItem> dst = new ArrayList<EventItem>(events.size());
    
    long total = 0L;
    for (Event event : events) {
      long count = eventDAO.getEventCount(event);
      
      // Get all distinct (!) keywords under that signature.
      event.setKeywords(new ArrayList<String>(eventDAO.getKeywords(event)));
      
      dst.add(new EventItem(event, count));
      total += count;
    }
    
    Collections.sort(dst, Countable.DESCENDING);
    return new EventItems(typeName, dst, total);
  }
  
  @Override
  public Event getEvent(String typeName, String signature) {
    Event event = eventDAO.getEvent(typeName, signature);
    
    if (event != null) {
      // Get all distinct (!) keywords under that signature.
      event.setKeywords(new ArrayList<String>(eventDAO.getKeywords(signature)));
    }
    
    return event;
  }

  @Override
  public long getEventCount(Event event) {
    return eventDAO.getEventCount(event);
  }

  @Override
  public SearchItems query(String query) {
    SortedSet<String> keywords = EventUtils.keywords(query);
    if (keywords == null || keywords.isEmpty()) {
      return SearchItems.EMPTY;
    }
    
    List<Event> events = eventDAO.search(keywords);
    
    List<SearchItem> dst = new ArrayList<SearchItem>(events.size());
    
    long total = 0L;
    for (Event event : events) {
      long count = eventDAO.getEventCount(event);
      
      // Get all distinct (!) keywords under that signature.
      SortedSet<String> everything = eventDAO.getKeywords(event);
      everything.retainAll(keywords);
      event.setKeywords(new ArrayList<String>(everything));
      
      dst.add(new SearchItem(event, count));
      total += count;
    }
    
    Collections.sort(dst, Countable.DESCENDING);
    return new SearchItems(query, keywords, dst, total);
  }
}
