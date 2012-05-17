package org.ardverk.gibson.dashboard;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ardverk.gibson.Condition;
import org.ardverk.gibson.Event;

import play.data.validation.Constraints.Required;

public class EventItem implements Countable {
  
  @Required
  public final Event event;
  
  @Required
  public final String exception;
  
  @Required
  public final long count;
  
  public EventItem(Event event) {
    this(event, 1L);
  }
  
  public EventItem(Event event, long count) {
    this.event = event;
    this.count = count;
    
    Condition condition = event.getCondition();
    this.exception = condition.getMessage();
  }
  
  @Override
  public long getCount() {
    return count;
  }
  
  public List<String> getKeywords(final int limit) {
    List<String> keywords = event.getKeywords();
    
    if (limit >= 1 && limit < keywords.size()) {
      keywords = keywords.subList(0, limit);
    }
    
    return keywords;
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).toString();
  }
}
