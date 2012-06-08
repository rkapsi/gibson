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

import java.util.Collections;
import java.util.List;

import org.ardverk.gibson.Condition;
import org.ardverk.gibson.Event;

import play.data.validation.Constraints.Required;

public class EventItem extends Item {
  
  @Required
  public final Event event;
  
  public final List<String> hostnames;
  
  @Required
  public final String exception;
  
  public EventItem(Event event, long count) {
    this(event, wrap(event), count);
  }
  
  public EventItem(Event event, List<String> hostnames, long count) {
    super(count);
    
    this.event = event;
    this.hostnames = hostnames;
    
    Condition condition = event.getCondition();
    this.exception = condition.getMessage();
  }
  
  public List<String> getKeywords(int limit) {
    List<String> keywords = event.getKeywords();
    
    if (limit >= 1 && limit < keywords.size()) {
      keywords = keywords.subList(0, limit);
    }
    
    return keywords;
  }
  
  public List<String> getHostnames() {
    return hostnames;
  }
  
  private static List<String> wrap(Event event) {
    String host = event.getHostname();
    if (host == null) {
      return Collections.emptyList();
    }
    
    return Collections.singletonList(host);
  }
}
