/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.atmos.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.net.URI;

import org.jclouds.Fallback;
import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.jclouds.http.HttpRequest;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.InvocationContext;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public class EndpointIfAlreadyExists implements Fallback<URI>, InvocationContext<EndpointIfAlreadyExists> {

   private URI endpoint;

   @Override
   public ListenableFuture<URI> create(Throwable t) throws Exception {
      return immediateFuture(createOrPropagate(t));
   }

   @Override
   public URI createOrPropagate(Throwable t) throws Exception {
      if (checkNotNull(t, "throwable") instanceof KeyAlreadyExistsException) {
         return endpoint;
      }
      throw propagate(t);
   }

   @Override
   public EndpointIfAlreadyExists setContext(HttpRequest request) {
      return setEndpoint(request == null ? null : request.getEndpoint());
   }

   @VisibleForTesting
   EndpointIfAlreadyExists setEndpoint(@Nullable URI endpoint) {
      this.endpoint = endpoint;
      return this;
   }

}
