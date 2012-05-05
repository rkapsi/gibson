package controllers;


import static play.data.validation.Constraints.Required;


public class ExceptionSummary {
  @Required
  public int count;
  @Required
  public String name;


  public ExceptionSummary(int count, String name) {
    this.count = count;
    this.name = name;
  }
}
