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
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rackspace.CloudServers;
import org.jclouds.rackspace.cloudservers.CloudServersAsyncClient;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.predicates.ServerActive;
import org.jclouds.rackspace.cloudservers.predicates.ServerDeleted;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@RequiresHttp
public class CloudServersContextModule extends AbstractModule {

   @Override
   protected void configure() {
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
   RestContext<CloudServersAsyncClient, CloudServersClient> provideContext(Closer closer,
            CloudServersAsyncClient asynchApi, CloudServersClient defaultApi,
            @CloudServers URI endPoint,
            @Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account) {
      return new RestContextImpl<CloudServersAsyncClient, CloudServersClient>(closer, asynchApi,
               defaultApi, endPoint, account);
   }

}