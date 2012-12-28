/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.deltacloud;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpResponseException;

import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;

public final class DeltacloudFallbacks {
   private DeltacloudFallbacks() {
   }

   /**
    * When a delete operation is performed, Deltacloud returns 302.
    * 
    * @author Adrian Cole
    */
   public static final class VoidOnRedirectedDelete implements FutureFallback<Void> {
      @Override
      public ListenableFuture<Void> create(final Throwable t) {
         HttpResponseException exception = getFirstThrowableOfType(t, HttpResponseException.class);
         if (exception != null && exception.getCommand().getCurrentRequest().getMethod().equals(HttpMethod.DELETE)
               && exception.getResponse().getStatusCode() == 302) {
            return immediateFuture(null);
         }
         throw propagate(t);
      }
   }
}
