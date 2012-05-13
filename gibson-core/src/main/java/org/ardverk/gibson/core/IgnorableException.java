package org.ardverk.gibson.core;


/**
 * We log it but don't create {@link Condition}s or rather {@link Event}s out of it.
 */
class IgnorableException extends IllegalStateException {
  
  private static final long serialVersionUID = -3215691645479141943L;

  public IgnorableException(String message, Throwable cause) {
    super(message, cause);
  }
}