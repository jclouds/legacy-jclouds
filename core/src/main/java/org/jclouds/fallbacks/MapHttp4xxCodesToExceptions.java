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
package org.jclouds.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Fallback;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public final class MapHttp4xxCodesToExceptions implements Fallback<Object> {

   private final PropagateIfRetryAfter propagateIfRetryAfter;

   @Inject
   MapHttp4xxCodesToExceptions(PropagateIfRetryAfter propagateIfRetryAfter) { // NO_UCD
      this.propagateIfRetryAfter = checkNotNull(propagateIfRetryAfter, "propagateIfRetryAfter");
   }

   @Override
   public ListenableFuture<Object> create(Throwable t) throws Exception { // NO_UCD
      return immediateFuture(createOrPropagate(t));
   }

   @Override
   public Object createOrPropagate(Throwable t) throws Exception {
      propagateIfRetryAfter.create(t); // if we pass here, we aren't a retry-after exception
      if (t instanceof HttpResponseException) {
         HttpResponseException responseException = HttpResponseException.class.cast(t);
         if (responseException.getResponse() != null)
            switch (responseException.getResponse().getStatusCode()) {
            case 401:
               throw new AuthorizationException(responseException);
            case 403:
               throw new AuthorizationException(responseException);
            case 404:
               throw new ResourceNotFoundException(responseException);
            case 409:
               throw new IllegalStateException(responseException);
            }
      }
      throw propagate(t);
   }
}
