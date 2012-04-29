package org.ardverk.logging;

import com.basho.riak.client.convert.ConversionException;

class GibsonEventConversionException extends ConversionException {
  
  private static final long serialVersionUID = 6511401050532258413L;

  public GibsonEventConversionException(String message, Throwable cause) {
    super(message);
    initCause(cause);
  }
}
