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

import java.util.Set;

import org.ardverk.gibson.Condition;
import org.ardverk.gibson.Event;
import org.ardverk.gibson.Level;

import play.data.validation.Constraints.Required;

public class SearchItem extends Item {
  
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
  
  public SearchItem(Set<? extends String> keywords, Event event, long count) {
    super(count);
    
    this.condition = event.getCondition();
    this.typeName = condition.getTypeName();
    
    this.signature = event.getSignature();
    
    this.logger = event.getLogger();
    this.level = event.getLevel();
    this.message = event.getMessage();
    this.keywords = keywords;
  }
}
