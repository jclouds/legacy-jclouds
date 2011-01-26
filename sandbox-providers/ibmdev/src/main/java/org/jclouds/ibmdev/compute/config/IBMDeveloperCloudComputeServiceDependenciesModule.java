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

package org.jclouds.ibmdev.compute.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.compute.functions.InstanceToNodeMetadata;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public class IBMDeveloperCloudComputeServiceDependenciesModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {
      }).to(InstanceToNodeMetadata.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   @Named("CREDENTIALS")
   Map<String, String> credentialsMap() {
      return new ConcurrentHashMap<String, String>();
   }

   @VisibleForTesting
   static final Map<Instance.Status, NodeState> instanceStatusToNodeState = ImmutableMap
            .<Instance.Status, NodeState> builder().put(Instance.Status.ACTIVE, NodeState.RUNNING)//
            .put(Instance.Status.STOPPED, NodeState.SUSPENDED)//
            .put(Instance.Status.REMOVED, NodeState.TERMINATED)//
            .put(Instance.Status.DEPROVISIONING, NodeState.PENDING)//
            .put(Instance.Status.FAILED, NodeState.ERROR)//
            .put(Instance.Status.NEW, NodeState.PENDING)//
            .put(Instance.Status.PROVISIONING, NodeState.PENDING)//
            .put(Instance.Status.REJECTED, NodeState.ERROR)//
            .put(Instance.Status.RESTARTING, NodeState.PENDING)//
            .put(Instance.Status.STARTING, NodeState.PENDING)//
            .put(Instance.Status.STOPPING, NodeState.PENDING)//
            .put(Instance.Status.DEPROVISION_PENDING, NodeState.PENDING)//
            .put(Instance.Status.UNKNOWN, NodeState.UNRECOGNIZED).build();

   @Singleton
   @Provides
   Map<Instance.Status, NodeState> provideServerToNodeState() {
      return instanceStatusToNodeState;
   }
}