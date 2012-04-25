package org.ardverk.logging;

/**
 * We log it but don't create {@link GibsonThrowable}s or rather {@link GibsonEvent}s out of it.
 */
class IgnorableException extends IllegalStateException {
  
  private static final long serialVersionUID = 5248569070496124230L;

  public IgnorableException(String message, Throwable cause) {
    super(message, cause);
  }
}