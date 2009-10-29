/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.filters;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.reference.RackspaceHeaders;

/**
 * Signs the Rackspace request. This will update the Authentication Token before 24 hours is up.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class AuthenticateRequest implements HttpRequestFilter {

   private final Provider<String> authTokenProvider;

   public final long BILLION = 1000000000;
   public final long MINUTES = 60 * BILLION;
   public final long HOURS = 60 * MINUTES;

   private final AtomicReference<String> authToken;
   private final AtomicLong trigger = new AtomicLong(0);

   /**
    * Start the time update service. Rackspace clocks need to be 24 hours of the auth token. This is
    * not performed per-request, as creation of the token is a slow, synchronized command.
    */
   synchronized void updateIfTimeOut() {

      if (trigger.get() - System.nanoTime() <= 0) {
         createNewToken();
      }

   }

   // this is a hotspot when submitted concurrently, so be lazy.
   // rackspace is ok with up to 23:59 off their time, so let's
   // be as lazy as possible.
   public String createNewToken() {
      authToken.set(authTokenProvider.get());
      trigger.set(System.nanoTime() + System.nanoTime() + 23 * HOURS);
      return authToken.get();

   }

   public String getAuthToken() {
      updateIfTimeOut();
      return authToken.get();
   }

   @Inject
   public AuthenticateRequest(@Authentication Provider<String> authTokenProvider) {
      this.authTokenProvider = authTokenProvider;
      authToken = new AtomicReference<String>();
   }

   public void filter(HttpRequest request) throws HttpException {
      request.getHeaders().replaceValues(RackspaceHeaders.AUTH_TOKEN,
               Collections.singletonList(getAuthToken()));
   }

}