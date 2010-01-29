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
package org.jclouds.rackspace.cloudservers.config;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rackspace.cloudservers.CloudServersAsyncClient;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.handlers.ParseCloudServersErrorFromHttpResponse;
import org.jclouds.rackspace.cloudservers.predicates.ServerActive;
import org.jclouds.rackspace.cloudservers.predicates.ServerDeleted;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class CloudServersRestClientModule extends AbstractModule {
   @Override
   protected void configure() {
      bindErrorHandlers();
   }

   @Provides
   @Singleton
   @Named("ACTIVE")
   protected Predicate<Server> serverRunning(ServerActive stateRunning) {
      return new RetryablePredicate<Server>(stateRunning, 600, 1, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Named("DELETED")
   protected Predicate<Server> serverDeleted(ServerDeleted stateDeleted) {
      return new RetryablePredicate<Server>(stateDeleted, 600, 50, TimeUnit.MILLISECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<InetSocketAddress> socketTester(SocketOpen open) {
      return new RetryablePredicate<InetSocketAddress>(open, 130, 1, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected CloudServersAsyncClient provideAsyncClient(RestClientFactory factory) {
      return factory.create(CloudServersAsyncClient.class);
   }

   @Provides
   @Singleton
   public CloudServersClient provideClient(CloudServersAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(CloudServersClient.class, client);
   }
   
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseCloudServersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseCloudServersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseCloudServersErrorFromHttpResponse.class);
   }
}