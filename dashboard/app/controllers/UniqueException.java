package controllers;


import org.ardverk.logging.GibsonEvent;

import static play.data.validation.Constraints.Required;


public class UniqueException {
  @Required
  public int count;
  @Required
  public GibsonEvent event;


  public UniqueException(int count, GibsonEvent event) {
    this.count = count;
    this.event = event;
  }
}
