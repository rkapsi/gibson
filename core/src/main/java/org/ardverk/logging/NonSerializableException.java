package org.ardverk.logging;

class NonSerializableException extends IllegalStateException {
  
  private static final long serialVersionUID = 5248569070496124230L;

  public NonSerializableException(String message, Throwable cause) {
    super(message, cause);
  }
}