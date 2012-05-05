package controllers;


import static play.data.validation.Constraints.Required;


public class UniqueException {
  @Required
  public int count;
  @Required
  public Throwable throwable;


  public UniqueException(int count, Throwable throwable) {
    this.count = count;
    this.throwable = throwable;
  }
}
