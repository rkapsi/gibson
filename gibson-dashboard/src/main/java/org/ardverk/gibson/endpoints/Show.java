package org.ardverk.gibson.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class Show {

  @GET
  @Path("/")
  public void index() {
    
  }
  
  @GET
  @Path("/event/{type}")
  public void events() {
    
  }
  
  @GET
  @Path("/event/{type}/{signature}")
  public void event() {
    
  }
}
