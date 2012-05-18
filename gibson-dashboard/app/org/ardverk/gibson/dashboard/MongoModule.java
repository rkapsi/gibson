package org.ardverk.gibson.dashboard;

import java.io.IOException;

import javax.inject.Singleton;

import org.ardverk.gibson.DatastoreFactory;
import org.ardverk.gibson.Gibson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Configuration;

import com.google.code.morphia.Datastore;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

class MongoModule extends AbstractModule {
  
  private static final Logger LOG = LoggerFactory.getLogger(MongoModule.class);
  
  private static final String URI_KEY = "gibson.mongo.uri";
  
  private static final String DATABASE_KEY = "gibson.mongo.database";
  
  @Override
  protected void configure() {
  }
  
  @Provides @Singleton
  Datastore getDatastore(Configuration configuration) throws IOException {
    
    MongoURI uri = parseURI(configuration.getString(URI_KEY), Gibson.ENDPOINT);
    String database = parseDatabase(configuration.getString(DATABASE_KEY), Gibson.DATABASE);
    
    if (LOG.isInfoEnabled()) {
      LOG.info("Connecting: uri=" + uri + ", database=" + database);
    }
    
    Mongo mongo = new Mongo(uri);
    DatastoreFactory factory = new DatastoreFactory(mongo);
    return factory.createDatastore(database);
  }
  
  private static MongoURI parseURI(String uri, MongoURI defaultValue) {
    if (uri != null) {
      return new MongoURI(uri);
    }
    
    return defaultValue;
  }
  
  private static String parseDatabase(String database, String defaultValue) {
    if (database != null) {
      return database;
    }
    
    return defaultValue;
  }
}
