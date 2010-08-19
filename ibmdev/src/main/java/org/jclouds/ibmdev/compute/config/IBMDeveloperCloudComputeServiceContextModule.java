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

import static org.jclouds.compute.domain.OsFamily.SUSE;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.compute.functions.InstanceToNodeMetadata;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudAddNodeWithTagStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudDestroyNodeStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudGetNodeMetadataStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudListNodesStrategy;
import org.jclouds.ibmdev.compute.strategy.IBMDeveloperCloudRebootNodeStrategy;
import org.jclouds.ibmdev.compute.suppliers.IBMDeveloperCloudImageSupplier;
import org.jclouds.ibmdev.compute.suppliers.IBMDeveloperCloudLocationSupplier;
import org.jclouds.ibmdev.compute.suppliers.IBMDeveloperCloudSizeSupplier;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * @author Adrian Cole
 */
public class IBMDeveloperCloudComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {
      }).to(InstanceToNodeMetadata.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(IBMDeveloperCloudAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(IBMDeveloperCloudListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(IBMDeveloperCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(IBMDeveloperCloudRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(IBMDeveloperCloudDestroyNodeStrategy.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
   }

   /**
    * tested known configuration
    */
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(SUSE);
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
            .put(Instance.Status.UNKNOWN, NodeState.UNKNOWN).build();

   @Singleton
   @Provides
   Map<Instance.Status, NodeState> provideServerToNodeState() {
      return instanceStatusToNodeState;
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      return injector.getInstance(IBMDeveloperCloudImageSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Size>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(IBMDeveloperCloudSizeSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Injector injector) {
      return injector.getInstance(IBMDeveloperCloudLocationSupplier.class);
   }
}