package org.ardverk.gibson.dashboard;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ardverk.gibson.Condition;
import org.ardverk.gibson.Event;
import org.ardverk.gibson.Level;

import play.data.validation.Constraints.Required;

public class EventItem implements Countable {
  
  @Required
  public final String signature;
  
  @Required
  public final String logger;
  
  @Required
  public final Level level;
  
  @Required
  public final String message;
  
  @Required
  public final String exception;
  
  @Required
  public final long count;
  
  public EventItem(Event event, long count) {
    this.signature = event.getSignature();
    
    this.logger = event.getLogger();
    this.level = event.getLevel();
    this.message = event.getMessage();
    this.count = count;
    
    Condition condition = event.getCondition();
    this.exception = condition.getMessage();
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
