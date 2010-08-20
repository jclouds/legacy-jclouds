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
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.compute.suppliers.StaticSizeSupplier;
import org.jclouds.vcloud.domain.VAppStatus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Providers;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link BaseVCloudComputeClient}
 * bound.
 * 
 * @author Adrian Cole
 */
public abstract class CommonVCloudComputeServiceContextModule extends BaseComputeServiceContextModule {

   @VisibleForTesting
   static final Map<VAppStatus, NodeState> vAppStatusToNodeState = ImmutableMap.<VAppStatus, NodeState> builder().put(
            VAppStatus.OFF, NodeState.SUSPENDED).put(VAppStatus.ON, NodeState.RUNNING).put(VAppStatus.RESOLVED,
            NodeState.PENDING).put(VAppStatus.SUSPENDED, NodeState.SUSPENDED).put(VAppStatus.UNRESOLVED,
            NodeState.PENDING).build();

   @Singleton
   @Provides
   Map<VAppStatus, NodeState> provideVAppStatusToNodeState() {
      return vAppStatusToNodeState;
   }

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bindLoadBalancer();
   }

   protected void bindLoadBalancer() {
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
   }

   @Override
   protected Supplier<Set<? extends Size>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(StaticSizeSupplier.class);
   }

}
