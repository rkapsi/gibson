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

package org.ardverk.gibson.transport;

import java.io.Closeable;
import java.io.IOException;

import org.ardverk.gibson.Event;

/**
 * The {@link Transport} interface defines a simple mechanism to send message(s)
 * to a remote host.
 */
public interface Transport extends Closeable {

  /**
   * Returns {@code true} if the {@link Transport} is connected.
   */
  public boolean isConnected();

  /**
   * Connects the {@link Transport} to some (possibly remote) endpoint.
   */
  public void connect() throws IOException;

  /**
   * Sends the given message.
   */
  public void send(Event event);
}
