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
package org.jclouds.cloudservers.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudservers.compute.extensions.CloudServersImageExtension;
import org.jclouds.cloudservers.compute.functions.CloudServersImageToImage;
import org.jclouds.cloudservers.compute.functions.CloudServersImageToOperatingSystem;
import org.jclouds.cloudservers.compute.functions.FlavorToHardware;
import org.jclouds.cloudservers.compute.functions.ServerToNodeMetadata;
import org.jclouds.cloudservers.compute.predicates.GetImageWhenStatusActivePredicateWithResult;
import org.jclouds.cloudservers.compute.strategy.CloudServersComputeServiceAdapter;
import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.ServerStatus;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.predicates.PredicateWithResult;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link CloudServersComputeServiceContext}; requires {@link BaseComputeService}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudServersComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<Server, Flavor, org.jclouds.cloudservers.domain.Image, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Server, Flavor, org.jclouds.cloudservers.domain.Image, Location>>() {
      }).to(CloudServersComputeServiceAdapter.class);
      
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);

      bind(new TypeLiteral<Function<org.jclouds.cloudservers.domain.Image, Image>>() {
      }).to(CloudServersImageToImage.class);
      bind(new TypeLiteral<Function<org.jclouds.cloudservers.domain.Image, OperatingSystem>>() {
      }).to(CloudServersImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<Flavor, Hardware>>() {
      }).to(FlavorToHardware.class);
      
      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);
      
      bind(new TypeLiteral<ImageExtension>() {
      }).to(CloudServersImageExtension.class);
      
      bind(new TypeLiteral<PredicateWithResult<Integer, Image>>() {
      }).to(GetImageWhenStatusActivePredicateWithResult.class);

   }
   
   @VisibleForTesting
   public static final Map<ServerStatus, Status> serverToNodeStatus = ImmutableMap
            .<ServerStatus, Status> builder().put(ServerStatus.ACTIVE, Status.RUNNING)//
            .put(ServerStatus.SUSPENDED, Status.SUSPENDED)//
            .put(ServerStatus.DELETED, Status.TERMINATED)//
            .put(ServerStatus.QUEUE_RESIZE, Status.PENDING)//
            .put(ServerStatus.PREP_RESIZE, Status.PENDING)//
            .put(ServerStatus.RESIZE, Status.PENDING)//
            .put(ServerStatus.VERIFY_RESIZE, Status.PENDING)//
            .put(ServerStatus.QUEUE_MOVE, Status.PENDING)//
            .put(ServerStatus.PREP_MOVE, Status.PENDING)//
            .put(ServerStatus.MOVE, Status.PENDING)//
            .put(ServerStatus.VERIFY_MOVE, Status.PENDING)//
            .put(ServerStatus.RESCUE, Status.PENDING)//
            .put(ServerStatus.ERROR, Status.ERROR)//
            .put(ServerStatus.BUILD, Status.PENDING)//
            .put(ServerStatus.RESTORING, Status.PENDING)//
            .put(ServerStatus.PASSWORD, Status.PENDING)//
            .put(ServerStatus.REBUILD, Status.PENDING)//
            .put(ServerStatus.DELETE_IP, Status.PENDING)//
            .put(ServerStatus.SHARE_IP_NO_CONFIG, Status.PENDING)//
            .put(ServerStatus.SHARE_IP, Status.PENDING)//
            .put(ServerStatus.REBOOT, Status.PENDING)//
            .put(ServerStatus.HARD_REBOOT, Status.PENDING)//
            .put(ServerStatus.UNKNOWN, Status.UNRECOGNIZED)//
            .put(ServerStatus.UNRECOGNIZED, Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   Map<ServerStatus, Status> provideServerToNodeStatus() {
      return serverToNodeStatus;
   }
   
   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.of(i.getInstance(ImageExtension.class));
   }

}
