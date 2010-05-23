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
package org.jclouds.vcloud.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.domain.Location;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.compute.config.providers.OrgAndVDCToLocationProvider;
import org.jclouds.vcloud.compute.config.providers.StaticSizeProvider;
import org.jclouds.vcloud.compute.config.providers.VCloudImageProvider;
import org.jclouds.vcloud.compute.strategy.EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudAddNodeWithTagStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudDestroyNodeStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudGetNodeMetadataStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudListNodesStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudRebootNodeStrategy;
import org.jclouds.vcloud.config.VCloudContextModule;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.endpoints.VCloud;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link BaseVCloudComputeClient}
 * bound.
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceContextModule extends VCloudContextModule {

   private final String providerName;

   public VCloudComputeServiceContextModule(String providerName) {
      this.providerName = providerName;
   }

   
   @Singleton
   @Provides
   Map<VAppStatus, NodeState> provideVAppStatusToNodeState() {
      return ImmutableMap.<VAppStatus, NodeState> builder().put(VAppStatus.OFF,
               NodeState.SUSPENDED).put(VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED,
               NodeState.PENDING).put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(
               VAppStatus.UNRESOLVED, NodeState.PENDING).build();
   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(UBUNTU);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(String.class).annotatedWith(VCloud.class).toInstance(providerName);
      bind(AddNodeWithTagStrategy.class).to(VCloudAddNodeWithTagStrategy.class);
      bind(RunNodesAndAddToSetStrategy.class).to(
               EncodeTemplateIdIntoNameRunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(VCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(VCloudRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(VCloudDestroyNodeStrategy.class);
      bindSizes();
      bindImages();
      bindLocations();
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   protected String provideNamingConvention() {
      return "%s-%s%s";
   }

   @Provides
   @Singleton
   @Named("NOT_RUNNING")
   protected Predicate<CommandUsingClient> runScriptRunning(ScriptStatusReturnsZero stateRunning) {
      return new RetryablePredicate<CommandUsingClient>(Predicates.not(stateRunning), 600, 3,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<VCloudAsyncClient, VCloudClient> context) {
      return new ComputeServiceContextImpl<VCloudAsyncClient, VCloudClient>(computeService, context);
   }

   protected void bindImages() {
      bind(new TypeLiteral<Set<? extends Image>>() {
      }).toProvider(VCloudImageProvider.class).in(Scopes.SINGLETON);
   }

   protected void bindSizes() {
      bind(new TypeLiteral<Set<? extends Size>>() {
      }).toProvider(StaticSizeProvider.class).in(Scopes.SINGLETON);
   }

   protected void bindLocations() {
      bind(new TypeLiteral<Set<? extends Location>>() {
      }).toProvider(OrgAndVDCToLocationProvider.class).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getProviderId();
         }
      };
   }

   @Provides
   @Singleton
   Location getVDC(VCloudClient client, Set<? extends Location> locations) {
      final String vdc = client.getDefaultVDC().getId();
      return Iterables.find(locations, new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(vdc);
         }

      });
   }

}
