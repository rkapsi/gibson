package org.ardverk.gibson.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ardverk.gibson.core.Event;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

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
  public List<Event> getEvents(String typeName) {
    
    BasicDBObject query = new BasicDBObject();
    query.put("condition.typeName", typeName);
    
    @SuppressWarnings("unchecked")
    List<String> signatures = events().distinct("signature", query);
    if (signatures == null || signatures.isEmpty()) {
      return Collections.emptyList();
    }
    
    List<Event> dst = new ArrayList<Event>(signatures.size());
    
    // Find one (!) Event for each distinct (!) signature
    for (String signature : signatures) {
      Event event = getEvent(typeName, signature);
      if (event != null) {
        dst.add(event);
      }
    }
    
    return dst;
  }
  
  /**
   * Returns an {@link Event} of the given type that has the given signature.
   */
  public Event getEvent(String typeName, String signature) {
    return createQuery()
        .filter("condition.typeName = ", typeName)
        .filter("signature = ", signature).get();
  }
  
  /**
   * Returns the number of occurrences of the given {@link Event}.
   */
  public long getEventCount(Event event) {
    return getEventCount(event.getSignature());
  }
  
  /**
   * @see EventDAO#getEventCount(Event)
   */
  private long getEventCount(String signature) {
    return createQuery().filter("signature = ", signature).countAll();
  }
}
