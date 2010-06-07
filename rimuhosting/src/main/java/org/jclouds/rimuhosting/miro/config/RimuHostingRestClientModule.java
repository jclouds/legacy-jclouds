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
package org.jclouds.rimuhosting.miro.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rimuhosting.miro.RimuHosting;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;
import org.jclouds.rimuhosting.miro.predicates.ServerDestroyed;
import org.jclouds.rimuhosting.miro.predicates.ServerRunning;
import org.jclouds.rimuhosting.miro.reference.RimuHostingConstants;

import com.google.common.base.Predicate;
import com.google.inject.Provides;

/**
 * Configures the RimuHosting connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class RimuHostingRestClientModule extends
         RestClientModule<RimuHostingClient, RimuHostingAsyncClient> {

   public RimuHostingRestClientModule() {
      super(RimuHostingClient.class, RimuHostingAsyncClient.class);
   }

   @Provides
   @Singleton
   @Named("RUNNING")
   protected Predicate<Server> serverRunning(ServerRunning stateRunning) {
      return new RetryablePredicate<Server>(stateRunning, 600, 1, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Named("DESTROYED")
   protected Predicate<Server> serverDeleted(ServerDestroyed stateDeleted) {
      return new RetryablePredicate<Server>(stateDeleted, 600, 50, TimeUnit.MILLISECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<IPSocket> socketTester(SocketOpen open) {
      return new RetryablePredicate<IPSocket>(open, 130, 1, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   public RimuHostingAuthentication provideRimuHostingAuthentication(
            @Named(RimuHostingConstants.PROPERTY_RIMUHOSTING_APIKEY) String apikey)
            throws UnsupportedEncodingException {
      return new RimuHostingAuthentication(apikey);
   }

   @Provides
   @Singleton
   @RimuHosting
   protected URI provideURI(
            @Named(RimuHostingConstants.PROPERTY_RIMUHOSTING_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

}