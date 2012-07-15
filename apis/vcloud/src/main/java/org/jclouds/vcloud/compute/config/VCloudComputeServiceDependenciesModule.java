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
package org.jclouds.vcloud.compute.config;


import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.util.Suppliers2;
import org.jclouds.vcloud.compute.functions.HardwareForVApp;
import org.jclouds.vcloud.compute.functions.HardwareForVAppTemplate;
import org.jclouds.vcloud.compute.functions.ImageForVAppTemplate;
import org.jclouds.vcloud.compute.functions.VAppToNodeMetadata;
import org.jclouds.vcloud.compute.internal.VCloudTemplateBuilderImpl;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.compute.strategy.VCloudComputeServiceAdapter;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.functions.VAppTemplatesInOrg;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceDependenciesModule extends AbstractModule {

   @VisibleForTesting
   public static final Map<Status, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
            .<Status, NodeMetadata.Status> builder()
            .put(Status.OFF, NodeMetadata.Status.SUSPENDED)
            .put(Status.ON, NodeMetadata.Status.RUNNING)
            .put(Status.RESOLVED, NodeMetadata.Status.PENDING)
            .put(Status.MIXED, NodeMetadata.Status.PENDING)
            .put(Status.UNKNOWN, NodeMetadata.Status.UNRECOGNIZED)
            .put(Status.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED)
            .put(Status.DEPLOYED, NodeMetadata.Status.PENDING)
            .put(Status.SUSPENDED, NodeMetadata.Status.SUSPENDED)
            .put(Status.WAITING_FOR_INPUT, NodeMetadata.Status.PENDING)
            .put(Status.INCONSISTENT, NodeMetadata.Status.PENDING)
            .put(Status.ERROR, NodeMetadata.Status.ERROR)
            .put(Status.UNRESOLVED, NodeMetadata.Status.PENDING).build();

   @Singleton
   @Provides
   protected Map<Status, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
   
   @VisibleForTesting
   public static final Map<Status, Image.Status> toPortableImageStatus = ImmutableMap
            .<Status, Image.Status> builder()
            .put(Status.RESOLVED, Image.Status.AVAILABLE)
            .put(Status.OFF, Image.Status.AVAILABLE)
            .put(Status.MIXED, Image.Status.PENDING)
            .put(Status.UNKNOWN, Image.Status.UNRECOGNIZED)
            .put(Status.UNRECOGNIZED, Image.Status.UNRECOGNIZED)
            .put(Status.DEPLOYED, Image.Status.PENDING)
            .put(Status.PENDING_DESCRIPTOR, Image.Status.PENDING)
            .put(Status.COPYING, Image.Status.PENDING)
            .put(Status.PENDING_CONTENTS, Image.Status.PENDING)
            .put(Status.QUARANTINED, Image.Status.PENDING)
            .put(Status.QUARANTINE_EXPIRED, Image.Status.ERROR)
            .put(Status.REJECTED, Image.Status.ERROR)
            .put(Status.TRANSFER_TIMEOUT, Image.Status.ERROR)
            .put(Status.ERROR, Image.Status.ERROR)
            .put(Status.UNRESOLVED, Image.Status.PENDING).build();

   @Singleton
   @Provides
   protected Map<Status, Image.Status> toPortableImageStatus() {
      return toPortableImageStatus;
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      bind(new TypeLiteral<ComputeServiceAdapter<VApp, VAppTemplate, VAppTemplate, Location>>() {
      }).to(VCloudComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<VApp, NodeMetadata>>() {
      }).to(VAppToNodeMetadata.class);

      bind(TemplateOptions.class).to(VCloudTemplateOptions.class);
      bind(TemplateBuilder.class).to(VCloudTemplateBuilderImpl.class);

      bind(new TypeLiteral<Function<VApp, Hardware>>() {
      }).to(new TypeLiteral<HardwareForVApp>() {
      });

      bind(new TypeLiteral<Function<Org, Iterable<VAppTemplate>>>() {
      }).to(VAppTemplatesInOrg.class);
      bind(new TypeLiteral<Function<VAppTemplate, Image>>() {
      }).to(ImageForVAppTemplate.class);
      bind(new TypeLiteral<Function<VAppTemplate, Hardware>>() {
      }).to(HardwareForVAppTemplate.class);

      // we aren't converting from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));
   }


   @Provides
   @Singleton
   public Supplier<NetworkConfig> networkConfig(@Network Supplier<ReferenceType> network,
         final FenceMode defaultFenceMode) {
      return Suppliers2.compose(new Function<ReferenceType, NetworkConfig>() {

         @Override
         public NetworkConfig apply(ReferenceType input) {
            return new NetworkConfig(input.getName(), input.getHref(), defaultFenceMode);
         }

      }, network);
   }

}
