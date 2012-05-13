package org.ardverk.gibson.dashboard;

import java.util.concurrent.atomic.AtomicReference;

import play.Application;
import play.GlobalSettings;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Context extends GlobalSettings {

  private static final AtomicReference<Injector> INJECTOR = new AtomicReference<Injector>();
  
  public static Injector injector() {
    Injector injector = INJECTOR.get();
    if (injector == null) {
      throw new IllegalStateException();
    }
    return injector;
  }
  
  @Override
  public void onStart(Application application) {
    INJECTOR.set(createInjector(application));
    super.onStart(application);
  }
  
  private static Injector createInjector(final Application application) {
    AbstractModule main = new AbstractModule() {
      @Override
      protected void configure() {
        bind(Application.class).toInstance(application);
      }
    };
    
    return Guice.createInjector(main, new MongoModule());
  }
}
