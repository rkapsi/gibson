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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.ardverk.gibson.Event;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@Singleton
class NoteDAO extends BasicDAO<Note, Note> {

  @Inject
  public NoteDAO(Datastore ds) {
    super(Note.class, ds);
  }
  
  /**
   * Returns the {@link Event}s {@link DBCollection}.
   */
  private DBCollection events() {
    DB db = ds.getDB();
    return db.getCollection(Note.COLLECTION);
  }
  
  /**
   * Clears all {@link Note}s.
   */
  public void clear() {
    events().drop();
  }
  
  /**
   * Deletes all {@link Note}s with the given signature
   */
  public void delete(String signature) {
    deleteByQuery(createQuery().filter("signature = ", signature));
  }
  
  /**
   * 
   */
  public Note find(String signature) {
    return createQuery().filter("signature = ", signature).get();
  }
  
  
}
