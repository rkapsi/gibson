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

package controllers;

import static org.ardverk.gibson.dashboard.Context.injector;

import org.ardverk.gibson.dashboard.EventItems;
import org.ardverk.gibson.dashboard.EventService;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.events;

public class Events extends Controller {
  
  public static Result show(String typeName) {
    EventService service = injector().getInstance(EventService.class);
    EventItems items = service.getEventItems(typeName);
    
    if (items == null || items.isEmpty()) {
      return notFound(typeName);
    }
    
    return ok(events.render(items));
  }
}
