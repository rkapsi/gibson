package controllers;


import static play.data.validation.Constraints.Required;


public class ExceptionSummary {
  @Required
  public int count;
  @Required
  public String className;


  public ExceptionSummary(int count, String className) {
    this.count = count;
    this.className = className;
  }
}