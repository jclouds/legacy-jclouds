/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http.pool;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import org.jclouds.http.HttpCommandRendezvous;

/**
 * Associates a command with an open connection to a service.
 * 
 * @author Adrian Cole
 */
public abstract class HttpCommandConnectionHandle<C> {
   protected final BlockingQueue<C> available;
   protected final Semaphore maxConnections;
   protected final Semaphore completed;
   protected final URI endPoint;
   protected C conn;
   protected HttpCommandRendezvous<?> command;

   public HttpCommandConnectionHandle(Semaphore maxConnections, BlockingQueue<C> available,
            URI endPoint, HttpCommandRendezvous<?> command, C conn) throws InterruptedException {
      this.available = available;
      this.maxConnections = maxConnections;
      this.completed = new Semaphore(1);
      this.endPoint = endPoint;
      this.command = command;
      this.conn = conn;
      completed.acquire();
   }

   public HttpCommandRendezvous<?> getCommandRendezvous() {
      return command;
   }

   public abstract void startConnection();

   public boolean isCompleted() {
      return (completed.availablePermits() == 1);
   }

   public void release() throws InterruptedException {
      if (isCompleted() || alreadyReleased()) {
         return;
      }
      available.put(conn);
      conn = null;
      command = null;
      completed.release();
   }

   private boolean alreadyReleased() {
      return conn == null;
   }

   public void cancel() throws IOException {
      if (isCompleted()) {
         return;
      }
      if (conn != null) {
         try {
            shutdownConnection();
         } finally {
            conn = null;
            command = null;
            maxConnections.release();
         }
      }
      completed.release();
   }

   public abstract void shutdownConnection() throws IOException;

   public void waitFor() throws InterruptedException {
      completed.acquire();
      completed.release();
   }
}
