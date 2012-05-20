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

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.mongodb.MongoURI;

/**
 * This class contains some commonly used constants.
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
