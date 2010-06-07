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
package org.jclouds.rest.internal;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

/**
 * @author Adrian Cole
 */
public class RestContextImpl<S, A> implements RestContext<S, A> {

   @Resource
   private Logger logger = Logger.NULL;
   private final A asyncApi;
   private final S syncApi;
   private final Closer closer;
   private final URI endPoint;
   private final String account;

   @Inject
   public RestContextImpl(Closer closer, A asyncApi, S syncApi, URI endPoint, String account) {
      this.asyncApi = asyncApi;
      this.syncApi = syncApi;
      this.closer = closer;
      this.endPoint = endPoint;
      this.account = account;
   }

   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

   public String getAccount() {
      return account;
   }

   public A getAsyncApi() {
      return asyncApi;
   }

   public S getApi() {
      return syncApi;
   }

   public URI getEndPoint() {
      return endPoint;
   }

}
