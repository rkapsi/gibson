package org.ardverk.gibson;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.converters.DefaultConverters;
import com.google.code.morphia.mapping.Mapper;
import com.mongodb.Mongo;

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
