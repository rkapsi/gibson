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

import java.util.List;

import play.data.validation.Constraints.Required;

public class EventItems extends Items {
  
  @Required
  public final String typeName;
  
  @Required
  public final List<? extends EventItem> elements;
  
  @Required
  public final long count;

  public EventItems(String typeName, List<? extends EventItem> elements, long count) {
    this.typeName = typeName;
    this.elements = elements;
    this.count = count;
  }
  
  @Override
  public int size() {
    return elements != null ? elements.size() : 0;
  }
}
