package org.ardverk.gibson.dashboard;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.ardverk.gibson.core.Condition;
import org.ardverk.gibson.core.Event;
import org.ardverk.gibson.core.Level;

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
  public final List<String> keywords;
  
  @Required
  public final long count;
  
  public SearchItem(Event event, long count) {
    this.condition = event.getCondition();
    this.typeName = condition.getTypeName();
    
    this.signature = event.getSignature();
    
    this.logger = event.getLogger();
    this.level = event.getLevel();
    this.message = event.getMessage();
    this.keywords = event.getKeywords();
    
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
