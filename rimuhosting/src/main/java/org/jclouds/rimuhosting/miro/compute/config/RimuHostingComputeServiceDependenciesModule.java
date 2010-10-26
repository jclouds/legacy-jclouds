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

package org.jclouds.rimuhosting.miro.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.compute.functions.ServerToNodeMetadata;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires
 * {@link RimuHostingComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceDependenciesModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<RimuHostingClient, RimuHostingAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<RimuHostingClient, RimuHostingAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<RimuHostingClient, RimuHostingAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Function<Server, Iterable<String>>>() {
      }).to(ServerToPublicAddresses.class);

   }

   @VisibleForTesting
   static final Map<RunningState, NodeState> runningStateToNodeState = ImmutableMap.<RunningState, NodeState> builder()
            .put(RunningState.RUNNING, NodeState.RUNNING)//
            .put(RunningState.NOTRUNNING, NodeState.SUSPENDED)//
            .put(RunningState.POWERCYCLING, NodeState.PENDING)//
            .put(RunningState.RESTARTING, NodeState.PENDING)//
            .put(RunningState.UNRECOGNIZED, NodeState.UNRECOGNIZED)//
            .build();

   @Singleton
   @Provides
   Map<RunningState, NodeState> provideServerToNodeState() {
      return runningStateToNodeState;
   }

   @Singleton
   private static class ServerToPublicAddresses implements Function<Server, Iterable<String>> {
      @Override
      public Iterable<String> apply(Server server) {
         return server.getIpAddresses() == null ? ImmutableSet.<String> of() : Iterables.concat(ImmutableList.of(server
                  .getIpAddresses().getPrimaryIp()), server.getIpAddresses().getSecondaryIps());
      }
   }
}
