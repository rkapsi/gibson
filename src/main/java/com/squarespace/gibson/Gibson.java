/*
 * Copyright 2012-2014 Will Benedict, Felix Berger, Roger Kapsi, Doug
 * Jones and Squarespace Inc.
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

package com.squarespace.gibson;

import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * This class contains some commonly used constants.
 */
public class Gibson {

  /**
   * The name of the Gibson {@link MDC} signature.
   */
  public static final String SIGNATURE = "Gibson.SIGNATURE";
  
  /**
   * All logging events produced by Gibson itself must use this {@link Marker} to prevent recursions.
   */
  static final Marker MARKER = MarkerFactory.getMarker(Gibson.class.getName() + ".GIBSON");
  
  private Gibson() {}
}
