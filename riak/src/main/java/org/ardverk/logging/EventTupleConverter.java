package org.ardverk.logging;

import org.codehaus.jackson.map.ObjectMapper;

class EventTupleConverter extends JsonConverter<EventTuple> {

  public EventTupleConverter(String bucket) {
    super(EventTuple.class, bucket);
  }

  public EventTupleConverter(ObjectMapper mapper, String bucket) {
    super(mapper, EventTuple.class, bucket);
  }
}
