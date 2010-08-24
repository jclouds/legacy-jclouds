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

package org.jclouds.vcloud.compute.config;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.compute.internal.VCloudComputeClientImpl;
import org.jclouds.vcloud.compute.strategy.VCloudDestroyNodeStrategy;
import org.jclouds.vcloud.compute.suppliers.OrgAndVDCToLocationSupplier;
import org.jclouds.vcloud.compute.suppliers.StaticSizeSupplier;
import org.jclouds.vcloud.compute.suppliers.VCloudImageSupplier;
import org.jclouds.vcloud.domain.Status;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Providers;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link VCloudComputeClientImpl}
 * bound.
 * 
 * @author Adrian Cole
 */
public abstract class CommonVCloudComputeServiceContextModule extends BaseComputeServiceContextModule {

   @VisibleForTesting
   static final Map<Status, NodeState> vAppStatusToNodeState = ImmutableMap.<Status, NodeState> builder().put(
            Status.OFF, NodeState.SUSPENDED).put(Status.ON, NodeState.RUNNING).put(Status.RESOLVED, NodeState.PENDING)
            .put(Status.ERROR, NodeState.ERROR).put(Status.UNRECOGNIZED, NodeState.UNKNOWN).put(Status.DEPLOYED,
                     NodeState.PENDING).put(Status.INCONSISTENT, NodeState.PENDING).put(Status.UNKNOWN,
                     NodeState.UNKNOWN).put(Status.MIXED, NodeState.PENDING).put(Status.WAITING_FOR_INPUT,
                     NodeState.PENDING).put(Status.SUSPENDED, NodeState.SUSPENDED).put(Status.UNRESOLVED,
                     NodeState.PENDING).build();

   @Singleton
   @Provides
   Map<Status, NodeState> provideVAppStatusToNodeState() {
      return vAppStatusToNodeState;
   }

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(DestroyNodeStrategy.class).to(VCloudDestroyNodeStrategy.class);
      bind(RunNodesAndAddToSetStrategy.class).to(EncodeTagIntoNameRunNodesAndAddToSetStrategy.class);
      bindLoadBalancer();
   }

   protected void bindLoadBalancer() {
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
   }

   @Override
   protected Supplier<Set<? extends Size>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(StaticSizeSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Injector injector) {
      return injector.getInstance(OrgAndVDCToLocationSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      return injector.getInstance(VCloudImageSupplier.class);
   }

}
