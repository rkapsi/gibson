package org.ardverk.gibson.dashboard;

import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ardverk.gibson.Condition;
import org.ardverk.gibson.Event;
import org.ardverk.gibson.Level;

import play.data.validation.Constraints.Required;

public class SearchItem implements Countable {
  
  @Required
  public final String typeName;
  
  @Required
  public final String signature;
  
  @Required
  public final String logger;
  
  @Required
  public final Level level;
  
  @Required
  public final String message;
  
  @Required
  public final Condition condition;
  
  @Required
  public final Set<? extends String> keywords;
  
  @Required
  public final long count;
  
  public SearchItem(Set<? extends String> keywords, Event event, long count) {
    this.condition = event.getCondition();
    this.typeName = condition.getTypeName();
    
    this.signature = event.getSignature();
    
    this.logger = event.getLogger();
    this.level = event.getLevel();
    this.message = event.getMessage();
    this.keywords = keywords;
    
    this.count = count;
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
