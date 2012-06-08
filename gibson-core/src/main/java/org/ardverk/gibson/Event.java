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

package org.ardverk.gibson;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;

/**
 * An {@link Event} is simply a logging event that gets written into the database.
 */
@Entity(Event.COLLECTION)
@Indexes({
  @Index("logger"),
  @Index("marker"),
  @Index("level"),
  @Index("message"),
  @Index("signature"),
  @Index("keywords"),
  @Index("condition.typeName")
})
public class Event {
  
  public static final String COLLECTION = "Events";
  
  @Id
  private ObjectId id;
  
  private Date creationTime;
  
  private String logger;
  
  private String marker;
  
  private Level level;
  
  private String thread;
  
  private String message;
  
  private Condition condition;
  
  private List<StackTraceElement> callerData;
  
  private Map<String, String> mdc;
  
  private String signature;
  
  private List<String> keywords;
  
  private String hostname;
  
  public ObjectId getId() {
    return id;
  }
  
  public void setId(ObjectId id) {
    this.id = id;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public String getLogger() {
    return logger;
  }

  public void setLogger(String logger) {
    this.logger = logger;
  }

  public String getMarker() {
    return marker;
  }

  public void setMarker(String marker) {
    this.marker = marker;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }
  
  public String getThread() {
    return thread;
  }

  public void setThread(String thread) {
    this.thread = thread;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Condition getCondition() {
    return condition;
  }

  public void setCondition(Condition condition) {
    this.condition = condition;
  }
  
  public List<StackTraceElement> getCallerData() {
    return callerData;
  }

  public void setCallerData(List<StackTraceElement> callerData) {
    this.callerData = callerData;
  }

  public Map<String, String> getMdc() {
    return mdc;
  }

  public void setMdc(Map<String, String> mdc) {
    this.mdc = mdc;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
  
  public List<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }
  
  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).toString();
  }
}
