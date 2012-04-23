package org.ardverk.logging;

import org.codehaus.jackson.map.ObjectMapper;

class GibsonEventConverter extends JsonConverter<GibsonEvent> {

  public GibsonEventConverter(String bucket) {
    super(GibsonEvent.class, bucket);
  }

  public GibsonEventConverter(ObjectMapper mapper, String bucket) {
    super(mapper, GibsonEvent.class, bucket);
  }
}
