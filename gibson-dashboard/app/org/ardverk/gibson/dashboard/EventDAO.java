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

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.apache.commons.lang3.tuple.Pair;
import org.ardverk.gibson.Event;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Singleton
class EventDAO extends BasicDAO<Event, Event> {

  @Inject
  public EventDAO(Datastore ds) {
    super(Event.class, ds);
  }
  
  /**
   * Returns the {@link Event}s {@link DBCollection}.
   */
  private DBCollection events() {
    DB db = ds.getDB();
    return db.getCollection(Event.COLLECTION);
  }
  
  /**
   * Clears all {@link Event}s.
   */
  public void clear() {
    events().drop();
  }
  
  /**
   * Deletes all {@link Event}s with the given signature
   */
  public void delete(String signature) {
    deleteByQuery(createQuery().filter("signature = ", signature));
  }
  
  /**
   * Returns the type names of all {@link Event}.
   */
  @SuppressWarnings("unchecked")
  public List<String> getTypeNames() {
    return (List<String>)events().distinct("condition.typeName");
  }
  
  /**
   * Returns the number of occurrences of the given {@link Event} type.
   */
  public long getTypeNameCount(String typeName) {
    return createQuery().filter("condition.typeName = ", typeName).countAll();
  }
  
  /**
   * Returns all {@link Event}s of the given type.
   */
  @SuppressWarnings("unchecked")
  public List<String> getEvents(String typeName) {
    BasicDBObject query = new BasicDBObject();
    query.put("condition.typeName", typeName);
    return events().distinct("signature", query);
  }
  
  /**
   * Returns an {@link Event} for the given signature.
   */
  public Event getEvent(String signature) {
    return getEvent(null, signature);
  }
  
  /**
   * Returns an {@link Event} of the given type that has the given signature.
   */
  public Event getEvent(String typeName, String signature) {
    Query<Event> query = createQuery();
    if (typeName != null) {
      query.filter("condition.typeName = ", typeName);
    }
    
    return query.filter("signature = ", signature).get();
  }
  
  /**
   * @see EventDAO#getEventCount(Event)
   */
  public long getEventCount(String signature) {
    return createQuery().filter("signature = ", signature).countAll();
  }

  public Date getTypeNameFirstOccurrence(String typeName) {
    return getTypeNameCreationTime(typeName, true);
  }

  public Date getTypeNameLastOccurrence(String typeName) {
    return getTypeNameCreationTime(typeName, false);
  }

  public Date getEventFirstOccurrence(String signature) {
    return getEventCreationTime(signature, false);
  }

  public Date getEventLastOccurrence(String signature) {
    return getEventCreationTime(signature, true);
  }

  private Date getEventCreationTime(String signature, boolean descending) {
    return getEventCreationTimeForQuery(createQuery().filter("signature =", signature), descending);
  }

  private Date getTypeNameCreationTime(String typeName, boolean descending) {
    return getEventCreationTimeForQuery(createQuery().filter("condition.typeName =", typeName), descending);
  }

  private Date getEventCreationTimeForQuery(Query<Event> query, boolean descending) {
    String order = descending ? "-creationTime" : "creationTime";
    return query.order(order).limit(1).get().getCreationTime();
  }

  /**
   * Returns a list of distinct (!) signatures of all events that have these keywords.
   */
  @SuppressWarnings("unchecked")
  public List<String> search(Collection<? extends String> keywords) {
    BasicDBObject query = new BasicDBObject();
    query.put("keywords", new BasicDBObject("$in", keywords));
    return events().distinct("signature", query);
  }
  
  /**
   * Returns all distinct (!) keywords under the given signature.
   */
  @SuppressWarnings("unchecked")
  public SortedSet<String> getKeywords(String signature) {
    BasicDBObject query = new BasicDBObject();
    query.put("signature", signature);
    
    return new TreeSet<String>(events().distinct("keywords", query));
  }
  
  /**
   * Returns all distinct (!) hostname(s) under the given signature.
   */
  @SuppressWarnings("unchecked")
  public SortedSet<String> getHostnames(String signature) {
    BasicDBObject query = new BasicDBObject();
    query.put("signature", signature);
    
    return new TreeSet<String>(events().distinct("hostname", query));
  }
}
