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

import org.ardverk.gibson.dashboard.EventItem;
import org.ardverk.gibson.dashboard.EventService;
import org.ardverk.gibson.dashboard.Note;
import org.ardverk.gibson.dashboard.NoteService;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.event;

public class Event extends Controller {
  
  private static final int KEYWORD_LIMIT = 100;
  
  public static Result show(String typeName, String signature) {
    EventService events = injector().getInstance(EventService.class);
    NoteService notes = injector().getInstance(NoteService.class);
    
    EventItem item = events.getEvent(typeName, signature);
    Note note = notes.find(signature);
    
    if (item == null) {
      return notFound(typeName + ", " + signature);
    }
    
    return ok(event.render(item, note, KEYWORD_LIMIT));
  }
  
  public static Result delete(String typeName, String signature) {
    return null;
  }
}
