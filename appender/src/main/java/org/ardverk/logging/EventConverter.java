package org.ardverk.logging;

import org.codehaus.jackson.map.ObjectMapper;

class EventConverter extends JsonConverter<Event> {

  public EventConverter(String bucket) {
    super(Event.class, bucket);
  }

  public EventConverter(ObjectMapper mapper, String bucket) {
    super(mapper, Event.class, bucket);
  }
}
