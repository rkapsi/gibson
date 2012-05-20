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

package org.ardverk.gibson;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.converters.DefaultConverters;
import com.google.code.morphia.mapping.Mapper;
import com.mongodb.Mongo;

/**
 * A simple factory to create {@link Datastore}s.
 */
public class DatastoreFactory {

  private final Mongo mongo;

  public DatastoreFactory(Mongo mongo) {
    this.mongo = mongo;
  }

  public Morphia createMorphia() {
    Morphia morphia = new Morphia();

    Mapper mapper = morphia.getMapper();
    DefaultConverters converters = mapper.getConverters();
    converters.addConverter(StackTraceElementConverter.class);

    morphia.mapPackageFromClass(Event.class);

    return morphia;
  }

  public Datastore createDatastore(String database) {
    return createDatastore(createMorphia(), database);
  }

  public Datastore createDatastore(Morphia morphia, String database) {
    Datastore ds = morphia.createDatastore(mongo, database);

    ds.ensureIndexes();
    ds.ensureCaps();

    return ds;
  }
}
