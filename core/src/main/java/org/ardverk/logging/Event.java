package org.ardverk.logging;

import java.util.UUID;

import com.basho.riak.client.convert.RiakKey;

public class Event {

  public static Event valueOf() {
    Event value = new Event();
    value.setKey(UUID.randomUUID().toString());
    value.setCreationTime(System.currentTimeMillis());
    return value;
  }
  
  @RiakKey
  private String key;
  
  private long creationTime;
  
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public long getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(long creationTime) {
    this.creationTime = creationTime;
  }
}
