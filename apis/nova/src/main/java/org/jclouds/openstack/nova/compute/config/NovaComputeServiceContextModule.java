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
package org.jclouds.openstack.nova.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.openstack.nova.compute.functions.FlavorToHardware;
import org.jclouds.openstack.nova.compute.functions.NovaImageToImage;
import org.jclouds.openstack.nova.compute.functions.NovaImageToOperatingSystem;
import org.jclouds.openstack.nova.compute.functions.ServerToNodeMetadata;
import org.jclouds.openstack.nova.compute.strategy.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.ImageStatus;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link NovaComputeServiceContext}; requires {@link BaseComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class NovaComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<Server, Flavor, org.jclouds.openstack.nova.domain.Image, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Server, Flavor, org.jclouds.openstack.nova.domain.Image, Location>>() {
      }).to(NovaComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);

      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.domain.Image, Image>>() {
      }).to(NovaImageToImage.class);
      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.domain.Image, OperatingSystem>>() {
      }).to(NovaImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<Flavor, Hardware>>() {
      }).to(FlavorToHardware.class);

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));

   }
   
   @VisibleForTesting
   public static final Map<ServerStatus, Status> toPortableNodeStatus = ImmutableMap
            .<ServerStatus, Status> builder().put(ServerStatus.ACTIVE, Status.RUNNING)//
            .put(ServerStatus.SUSPENDED, Status.SUSPENDED)//
            .put(ServerStatus.DELETED, Status.TERMINATED)//
            .put(ServerStatus.QUEUE_RESIZE, Status.PENDING)//
            .put(ServerStatus.PREP_RESIZE, Status.PENDING)//
            .put(ServerStatus.RESIZE, Status.PENDING)//
            .put(ServerStatus.VERIFY_RESIZE, Status.PENDING)//
            .put(ServerStatus.RESCUE, Status.PENDING)//
            .put(ServerStatus.BUILD, Status.PENDING)//
            .put(ServerStatus.PASSWORD, Status.PENDING)//
            .put(ServerStatus.REBUILD, Status.PENDING)//
            .put(ServerStatus.DELETE_IP, Status.PENDING)//
            .put(ServerStatus.REBOOT, Status.PENDING)//
            .put(ServerStatus.HARD_REBOOT, Status.PENDING)//
            .put(ServerStatus.UNKNOWN, Status.UNRECOGNIZED)//
            .put(ServerStatus.UNRECOGNIZED, Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   Map<ServerStatus, Status> toPortableNodeStatus() {
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
   
}
