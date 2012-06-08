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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ardverk.gibson.Event;
import org.ardverk.gibson.EventUtils;

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
    List<String> signatures = eventDAO.getEvents(typeName);
    List<EventItem> dst = new ArrayList<EventItem>(signatures.size());
    
    long total = 0L;
    for (String signature : signatures) {
      Event event = eventDAO.getEvent(typeName, signature);
      long count = eventDAO.getEventCount(signature);
      
      dst.add(new EventItem(event, count));
      total += count;
    }
    
    Collections.sort(dst, Countable.DESCENDING);
    return new EventItems(typeName, dst, total);
  }
  
  @Override
  public EventItem getEvent(String typeName, String signature) {
    Event event = eventDAO.getEvent(typeName, signature);
    if (event == null) {
      return null;
    }
    
    // Get all distinct (!) keywords and hostnames under that signature.
    event.setKeywords(new ArrayList<String>(eventDAO.getKeywords(signature)));
    Set<String> hostnames = eventDAO.getHostnames(signature);
    long count = eventDAO.getEventCount(signature);
    
    return new EventItem(event, new ArrayList<String>(hostnames), count);
  }

  @Override
  public long getEventCount(Event event) {
    return eventDAO.getEventCount(event.getSignature());
  }

  @Override
  public SearchItems query(String query) {
    Set<String> keywords = EventUtils.keywords(query);
    if (keywords == null || keywords.isEmpty()) {
      return SearchItems.notFound(query);
    }
    
    List<String> signatures = eventDAO.search(keywords);
    if (signatures == null || signatures.isEmpty()) {
      return SearchItems.notFound(query);
    }
    
    List<SearchItem> dst = new ArrayList<SearchItem>(signatures.size());
    
    long total = 0L;
    for (String signature : signatures) {
      Event event = eventDAO.getEvent(signature);
      long count = eventDAO.getEventCount(signature);
      
      // Get all distinct (!) keywords under that signature.
      Set<String> everything = eventDAO.getKeywords(signature);
      everything.retainAll(keywords);
      
      dst.add(new SearchItem(everything, event, count));
      total += count;
    }
    
    Collections.sort(dst, Countable.DESCENDING);
    return new SearchItems(query, keywords, dst, total);
  }

  @Override
  public void delete(String signature) {
    eventDAO.delete(signature);
  }
}
