package org.ardverk.gibson.dashboard;

import java.io.IOException;

import javax.inject.Singleton;

import org.ardverk.gibson.core.DatastoreFactory;

import com.google.code.morphia.Datastore;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.Mongo;

class MongoModule extends AbstractModule {
  
  @Override
  protected void configure() {
  }
  
  @Provides @Singleton
  Datastore getDatastore() throws IOException {
    Mongo mongo = new Mongo(DatastoreFactory.ENDPOINT);
    DatastoreFactory factory = new DatastoreFactory(mongo);
    return factory.createDatastore(DatastoreFactory.DATABASE);
  }
}
