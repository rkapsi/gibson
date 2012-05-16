package org.ardverk.gibson.appender;

interface Console {

  public boolean isInfoEnabled();

  public boolean isErrorEnabled();

  public void info(String message);

  public void error(String message);

  public void error(String message, Throwable t);

}