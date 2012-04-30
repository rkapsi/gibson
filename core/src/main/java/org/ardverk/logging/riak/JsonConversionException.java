package org.ardverk.logging.riak;

import com.basho.riak.client.convert.ConversionException;

class JsonConversionException extends ConversionException {
  
  private static final long serialVersionUID = 6511401050532258413L;

  public JsonConversionException(String message, Throwable cause) {
    super(message);
    initCause(cause);
  }
}
