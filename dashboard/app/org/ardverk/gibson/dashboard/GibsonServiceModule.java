package org.ardverk.gibson.dashboard;

import com.google.inject.AbstractModule;

import controllers.GibsonService;
import controllers.MockService;

public class GibsonServiceModule extends AbstractModule {
  
  @Override
  protected void configure() {
    bind(GibsonService.class).toInstance(new MockService());
  }
}
