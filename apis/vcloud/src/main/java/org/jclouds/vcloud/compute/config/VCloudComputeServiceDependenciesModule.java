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
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
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
   public static final Map<Status, NodeState> VAPPSTATUS_TO_NODESTATE = ImmutableMap.<Status, NodeState> builder().put(
            Status.OFF, NodeState.SUSPENDED).put(Status.ON, NodeState.RUNNING).put(Status.RESOLVED, NodeState.PENDING)
            .put(Status.ERROR, NodeState.ERROR).put(Status.UNRECOGNIZED, NodeState.UNRECOGNIZED).put(Status.DEPLOYED,
                     NodeState.PENDING).put(Status.INCONSISTENT, NodeState.PENDING).put(Status.UNKNOWN,
                     NodeState.UNRECOGNIZED).put(Status.MIXED, NodeState.PENDING).put(Status.WAITING_FOR_INPUT,
                     NodeState.PENDING).put(Status.SUSPENDED, NodeState.SUSPENDED).put(Status.UNRESOLVED,
                     NodeState.PENDING).build();

   @Singleton
   @Provides
   protected Map<Status, NodeState> provideVAppStatusToNodeState() {
      return VAPPSTATUS_TO_NODESTATE;
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
      }).to((Class) IdentityFunction.class);
   }


   @Provides
   @Singleton
   public NetworkConfig networkConfig(@Network ReferenceType network, FenceMode defaultFenceMode) {
      return new NetworkConfig(network.getName(), network.getHref(), defaultFenceMode);
   }

}
