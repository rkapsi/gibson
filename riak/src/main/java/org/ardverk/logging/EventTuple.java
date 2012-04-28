package org.ardverk.logging;

import com.basho.riak.client.convert.RiakKey;


public class EventTuple {

  @RiakKey
  private String key;
  
  private GibsonEvent event;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public GibsonEvent getEvent() {
    return event;
  }

  public void setEvent(GibsonEvent event) {
    this.event = event;
  }
  
  @Override
  public String toString() {
    return "key=" + key + ", event=" + event;
  }
}
