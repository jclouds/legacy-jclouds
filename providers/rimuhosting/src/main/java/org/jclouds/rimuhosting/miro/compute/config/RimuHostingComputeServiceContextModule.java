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
package org.jclouds.rimuhosting.miro.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.rimuhosting.miro.compute.functions.RimuHostingImageToImage;
import org.jclouds.rimuhosting.miro.compute.functions.ServerToNodeMetadata;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingComputeServiceAdapter;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires {@link BaseComputeService}
 * bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<Server, Hardware, org.jclouds.rimuhosting.miro.domain.Image, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(
               new TypeLiteral<ComputeServiceAdapter<Server, Hardware, org.jclouds.rimuhosting.miro.domain.Image, Location>>() {
               }).to(RimuHostingComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(new TypeLiteral<Function<Server, Iterable<String>>>() {
      }).to(ServerToPublicAddresses.class);

      bind(new TypeLiteral<Function<org.jclouds.rimuhosting.miro.domain.Image, Image>>() {
      }).to(RimuHostingImageToImage.class);

      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));

   }

   @VisibleForTesting
   static final Map<RunningState, Status> runningStateToNodeStatus = ImmutableMap.<RunningState, Status> builder().put(
            RunningState.RUNNING, Status.RUNNING)//
            .put(RunningState.NOTRUNNING, Status.SUSPENDED)//
            .put(RunningState.POWERCYCLING, Status.PENDING)//
            .put(RunningState.RESTARTING, Status.PENDING)//
            .put(RunningState.UNRECOGNIZED, Status.UNRECOGNIZED)//
            .build();

   @Singleton
   @Provides
   Map<RunningState, Status> provideServerToNodeStatus() {
      return runningStateToNodeStatus;
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
