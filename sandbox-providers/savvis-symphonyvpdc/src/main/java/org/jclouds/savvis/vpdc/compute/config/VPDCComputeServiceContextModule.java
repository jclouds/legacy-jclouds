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

package org.jclouds.savvis.vpdc.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.savvis.vpdc.domain.VApp;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public class VPDCComputeServiceContextModule extends BaseComputeServiceContextModule {

   @VisibleForTesting
   public static final Map<VApp.Status, NodeState> VAPPSTATUS_TO_NODESTATE = ImmutableMap
         .<VApp.Status, NodeState> builder().put(VApp.Status.OFF, NodeState.SUSPENDED)
         .put(VApp.Status.ON, NodeState.RUNNING).put(VApp.Status.RESOLVED, NodeState.PENDING)
         .put(VApp.Status.UNRECOGNIZED, NodeState.UNRECOGNIZED).put(VApp.Status.UNKNOWN, NodeState.UNRECOGNIZED)
         .put(VApp.Status.SUSPENDED, NodeState.SUSPENDED).put(VApp.Status.UNRESOLVED, NodeState.PENDING).build();

   @Singleton
   @Provides
   protected Map<VApp.Status, NodeState> provideVAppStatusToNodeState() {
      return VAPPSTATUS_TO_NODESTATE;
   }

}
