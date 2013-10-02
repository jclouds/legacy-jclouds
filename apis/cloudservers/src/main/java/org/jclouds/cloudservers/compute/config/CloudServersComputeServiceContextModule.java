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
package org.jclouds.cloudservers.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudservers.compute.extensions.CloudServersImageExtension;
import org.jclouds.cloudservers.compute.functions.CloudServersImageToImage;
import org.jclouds.cloudservers.compute.functions.CloudServersImageToOperatingSystem;
import org.jclouds.cloudservers.compute.functions.FlavorToHardware;
import org.jclouds.cloudservers.compute.functions.ServerToNodeMetadata;
import org.jclouds.cloudservers.compute.strategy.CloudServersComputeServiceAdapter;
import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.cloudservers.domain.ImageStatus;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.ServerStatus;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;

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
      }).to(Class.class.cast(IdentityFunction.class));
      
      bind(new TypeLiteral<ImageExtension>() {
      }).to(CloudServersImageExtension.class);
   }
   
   @VisibleForTesting
   public static final Map<ServerStatus, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
            .<ServerStatus, NodeMetadata.Status> builder()
            .put(ServerStatus.ACTIVE, NodeMetadata.Status.RUNNING)
            .put(ServerStatus.SUSPENDED, NodeMetadata.Status.SUSPENDED)
            .put(ServerStatus.DELETED, NodeMetadata.Status.TERMINATED)
            .put(ServerStatus.QUEUE_RESIZE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.PREP_RESIZE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.RESIZE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.VERIFY_RESIZE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.QUEUE_MOVE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.PREP_MOVE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.MOVE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.VERIFY_MOVE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.RESCUE, NodeMetadata.Status.PENDING)
            .put(ServerStatus.ERROR, NodeMetadata.Status.ERROR)
            .put(ServerStatus.BUILD, NodeMetadata.Status.PENDING)
            .put(ServerStatus.RESTORING, NodeMetadata.Status.PENDING)
            .put(ServerStatus.PASSWORD, NodeMetadata.Status.PENDING)
            .put(ServerStatus.REBUILD, NodeMetadata.Status.PENDING)
            .put(ServerStatus.DELETE_IP, NodeMetadata.Status.PENDING)
            .put(ServerStatus.SHARE_IP_NO_CONFIG, NodeMetadata.Status.PENDING)
            .put(ServerStatus.SHARE_IP, NodeMetadata.Status.PENDING)
            .put(ServerStatus.REBOOT, NodeMetadata.Status.PENDING)
            .put(ServerStatus.HARD_REBOOT, NodeMetadata.Status.PENDING)
            .put(ServerStatus.UNKNOWN, NodeMetadata.Status.UNRECOGNIZED)
            .put(ServerStatus.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   Map<ServerStatus, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
   
   @VisibleForTesting
   public static final Map<ImageStatus, Image.Status> toPortableImageStatus = ImmutableMap
            .<ImageStatus, Image.Status> builder()
            .put(ImageStatus.ACTIVE, Image.Status.AVAILABLE)
            .put(ImageStatus.SAVING, Image.Status.PENDING)
            .put(ImageStatus.PREPARING, Image.Status.PENDING)
            .put(ImageStatus.QUEUED, Image.Status.PENDING)
            .put(ImageStatus.FAILED, Image.Status.ERROR)
            .put(ImageStatus.UNKNOWN, Image.Status.UNRECOGNIZED)
            .put(ImageStatus.UNRECOGNIZED, Image.Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   Map<ImageStatus, Image.Status> toPortableImageStatus() {
      return toPortableImageStatus;
   }
   
   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.of(i.getInstance(ImageExtension.class));
   }

}
