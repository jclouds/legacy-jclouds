/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.compute.config;


import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.functions.HardwareForVApp;
import org.jclouds.vcloud.compute.functions.HardwareInOrg;
import org.jclouds.vcloud.compute.functions.ImagesInOrg;
import org.jclouds.vcloud.compute.functions.VAppToNodeMetadata;
import org.jclouds.vcloud.compute.internal.VCloudTemplateBuilderImpl;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.compute.strategy.GetLoginCredentialsFromGuestConfiguration;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link VCloudComputeClientImpl}
 * bound.
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceContextModule extends BaseComputeServiceContextModule {

   @VisibleForTesting
   public static final Map<Status, NodeState> VAPPSTATUS_TO_NODESTATE = ImmutableMap.<Status, NodeState> builder()
         .put(Status.OFF, NodeState.SUSPENDED).put(Status.ON, NodeState.RUNNING)
         .put(Status.RESOLVED, NodeState.PENDING).put(Status.ERROR, NodeState.ERROR)
         .put(Status.UNRECOGNIZED, NodeState.UNRECOGNIZED).put(Status.DEPLOYED, NodeState.PENDING)
         .put(Status.INCONSISTENT, NodeState.PENDING).put(Status.UNKNOWN, NodeState.UNRECOGNIZED)
         .put(Status.MIXED, NodeState.PENDING).put(Status.WAITING_FOR_INPUT, NodeState.PENDING)
         .put(Status.SUSPENDED, NodeState.SUSPENDED).put(Status.UNRESOLVED, NodeState.PENDING).build();

   @Singleton
   @Provides
   protected Map<Status, NodeState> provideVAppStatusToNodeState() {
      return VAPPSTATUS_TO_NODESTATE;
   }

   @Override
   protected void configure() {
      super.configure();
      install(defineComputeStrategyModule());
      install(defineComputeSupplierModule());
      bind(new TypeLiteral<Function<VApp, NodeMetadata>>() {
      }).to(VAppToNodeMetadata.class);
      bind(TemplateOptions.class).to(VCloudTemplateOptions.class);
      bind(TemplateBuilder.class).to(VCloudTemplateBuilderImpl.class);
      bind(new TypeLiteral<Function<VApp, Hardware>>() {
      }).to(new TypeLiteral<HardwareForVApp>() {
      });
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<VCloudClient, VCloudClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<VCloudClient, VCloudClient>>() {
      }).to(new TypeLiteral<RestContextImpl<VCloudClient, VCloudClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Function<Org, Iterable<? extends Image>>>() {
      }).to(new TypeLiteral<ImagesInOrg>() {
      });
      bind(new TypeLiteral<Function<Org, Iterable<? extends Hardware>>>() {
      }).to(new TypeLiteral<HardwareInOrg>() {
      });
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(GetLoginCredentialsFromGuestConfiguration.class);
   }
   
   //CIM ostype does not include version info
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).os64Bit(true);
   }
   
   public BindComputeStrategiesByClass defineComputeStrategyModule() {
      return new VCloudBindComputeStrategiesByClass();
   }

   public BindComputeSuppliersByClass defineComputeSupplierModule() {
      return new VCloudBindComputeSuppliersByClass();
   }
}
