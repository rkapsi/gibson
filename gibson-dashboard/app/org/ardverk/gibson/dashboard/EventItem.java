package org.ardverk.gibson.dashboard;

import java.util.List;

import org.ardverk.gibson.Condition;
import org.ardverk.gibson.Event;

import play.data.validation.Constraints.Required;

public class EventItem extends Item {
  
  @Required
  public final Event event;
  
  @Required
  public final String exception;
  
  public EventItem(Event event, long count) {
    super(count);
    
    this.event = event;
    
    Condition condition = event.getCondition();
    this.exception = condition.getMessage();
  }
  
  public List<String> getKeywords(final int limit) {
    List<String> keywords = event.getKeywords();
    
    if (limit >= 1 && limit < keywords.size()) {
      keywords = keywords.subList(0, limit);
    }
    
    return keywords;
  }
}
