/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.http;

import java.util.concurrent.SynchronousQueue;

import java.util.concurrent.Future;

/**
 * Used for passing objects for response processing
 * 
 * @author Adrian Cole
 */
public class HttpCommandRendezvous<T> {

   private final HttpCommand command;
   @SuppressWarnings("unchecked")
   private final SynchronousQueue rendezvous;
   private final Future<T> future;

   @SuppressWarnings("unchecked")
   public HttpCommandRendezvous(HttpCommand command, SynchronousQueue rendezvous,
            Future<T> future) {
      this.command = command;
      this.rendezvous = rendezvous;
      this.future = future;
   }

   @SuppressWarnings("unchecked")
   public void setResponse(HttpResponse response) throws InterruptedException {
      this.rendezvous.put(response);
   }

   @SuppressWarnings("unchecked")
   public void setException(Exception exception) throws InterruptedException {
      this.rendezvous.put(exception);
   }

   public void cancel() {
      getFuture().cancel(true);
   }

   public HttpCommand getCommand() {
      return command;
   }

   public Future<T> getFuture() {
      return future;
   }

}
