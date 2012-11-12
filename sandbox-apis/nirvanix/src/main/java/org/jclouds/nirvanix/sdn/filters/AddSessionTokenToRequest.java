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
package org.jclouds.nirvanix.sdn.filters;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import com.google.commons.util.concurrent.Atomitcs;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.nirvanix.sdn.SessionToken;
import org.jclouds.nirvanix.sdn.reference.SDNQueryParams;

/**
 * Adds the Session Token to the request. This will update the Session Token before 20 minutes is
 * up.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class AddSessionTokenToRequest implements HttpRequestFilter {

   private final Provider<String> authTokenProvider;
   private final Provider<UriBuilder> builder;

   public final long BILLION = 1000000000;
   public final long MINUTES = 60 * BILLION;

   private final AtomicReference<String> authToken;
   private final AtomicLong trigger = new AtomicLong(0);

   /**
    * Start the time update service. Nirvanix clocks need to be 20 minutes of the session token.
    * This is not performed per-request, as creation of the token is a slow, synchronized command.
    */
   synchronized void updateIfTimeOut() {

      if (trigger.get() - System.nanoTime() <= 0) {
         createNewToken();
      }

   }

   // this is a hotspot when submitted concurrently, so be lazy.
   // session tokens expire in 20 minutes of no use, but let's be a little paranoid and go 19
   public String createNewToken() {
      authToken.set(authTokenProvider.get());
      trigger.set(System.nanoTime() + System.nanoTime() + 19 * MINUTES);
      return authToken.get();

   }

   public String getSessionToken() {
      updateIfTimeOut();
      return authToken.get();
   }

   @Inject
   public AddSessionTokenToRequest(@SessionToken Provider<String> authTokenProvider, Provider<UriBuilder> builder) {
      this.builder = builder;
      this.authTokenProvider = authTokenProvider;
      authToken = Atomics.newReference();
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return ModifyRequest.addQueryParam(request, SDNQueryParams.SESSIONTOKEN, getSessionToken(), builder.get());
   }

}
