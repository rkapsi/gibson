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
  
  @Override
  protected void configure() {
  }
  
  @Provides @Singleton
  Datastore getDatastore(Configuration configuration) throws IOException {
    
    MongoURI uri = parseURI(configuration.getString(URI_KEY), Gibson.URI);
    
    String database = uri.getDatabase();
    if (database == null) {
      throw new IOException("Database missing: " + uri);
    }
    
    if (LOG.isInfoEnabled()) {
      LOG.info("Connecting: uri=" + uri);
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
}
