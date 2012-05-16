package org.ardverk.gibson;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.mongodb.MongoURI;

/**
 * 
 */
public class Gibson {

  /**
   * All logging events produced by Gibson itself must use this {@link Marker} to prevent recursions.
   */
  public static final Marker MARKER = MarkerFactory.getMarker(Gibson.class.getName() + ".GIBSON");
  
  /**
   * The default {@link MongoURI}.
   */
  public static final MongoURI ENDPOINT = new MongoURI("mongodb://localhost");
  
  /**
   * The default name of the MongoDB database.
   */
  public static final String DATABASE = "Gibson";
  
  private Gibson() {}
}
