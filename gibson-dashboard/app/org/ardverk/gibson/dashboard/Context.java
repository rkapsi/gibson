/*
 * Copyright 2012 Will Benedict, Felix Berger and Roger Kapsi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.ardverk.gibson.dashboard;

import java.util.concurrent.atomic.AtomicReference;

import play.Application;
import play.Configuration;
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
    AbstractModule play = new AbstractModule() {
      @Override
      protected void configure() {
        bind(Application.class).toInstance(application);
        bind(Configuration.class).toInstance(application.configuration());
      }
    };
    
    return Guice.createInjector(play, new MongoModule());
  }
}
