package org.ardverk.gibson.dashboard;

import org.ardverk.gibson.dashboard.riak.RiakGibsonService;

import com.google.inject.AbstractModule;

import controllers.GibsonService;

public class GibsonServiceModule extends AbstractModule {
  
  @Override
  protected void configure() {
    bind(GibsonService.class).to(RiakGibsonService.class);
  }
}
