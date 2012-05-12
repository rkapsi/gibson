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
public class EventDAO extends BasicDAO<Event, Event> {

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
   * 
   */
  @SuppressWarnings("unchecked")
  public List<String> getTypeNames() {
    return (List<String>)events().distinct("condition.typeName");
  }
  
  /**
   * 
   */
  public long getTypeNameCount(String typeName) {
    return createQuery().filter("condition.typeName = ", typeName).countAll();
  }
  
  /**
   * 
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
   * 
   */
  public Event getEvent(String typeName, String signature) {
    return createQuery().filter("signature = ", signature).get();
  }
  
  /**
   * 
   */
  public long getEventCount(Event event) {
    return getEventCount(event.getSignature());
  }
  
  /**
   * 
   */
  public long getEventCount(String signature) {
    return createQuery().filter("signature = ", signature).countAll();
  }
}
