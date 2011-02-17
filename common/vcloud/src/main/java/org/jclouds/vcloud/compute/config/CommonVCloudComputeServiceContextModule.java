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

import javax.inject.Singleton;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.vcloud.domain.Status;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

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
            .put(Status.ERROR, NodeState.ERROR).put(Status.UNRECOGNIZED, NodeState.UNRECOGNIZED).put(Status.DEPLOYED,
                     NodeState.PENDING).put(Status.INCONSISTENT, NodeState.PENDING).put(Status.UNKNOWN,
                     NodeState.UNRECOGNIZED).put(Status.MIXED, NodeState.PENDING).put(Status.WAITING_FOR_INPUT,
                     NodeState.PENDING).put(Status.SUSPENDED, NodeState.SUSPENDED).put(Status.UNRESOLVED,
                     NodeState.PENDING).build();

   @Singleton
   @Provides
   Map<Status, NodeState> provideVAppStatusToNodeState() {
      return vAppStatusToNodeState;
   }

   @Override
   protected void configure() {
      super.configure();
      install(defineComputeStrategyModule());
      install(defineComputeSupplierModule());
   }

   public abstract BindComputeStrategiesByClass defineComputeStrategyModule();

   public abstract BindComputeSuppliersByClass defineComputeSupplierModule();

}
