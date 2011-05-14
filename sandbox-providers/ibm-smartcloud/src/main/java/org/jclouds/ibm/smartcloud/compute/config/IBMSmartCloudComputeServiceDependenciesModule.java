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
package org.jclouds.ibm.smartcloud.compute.config;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.ibm.smartcloud.IBMSmartCloudAsyncClient;
import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.compute.functions.InstanceToNodeMetadata;
import org.jclouds.ibm.smartcloud.compute.options.IBMSmartCloudTemplateOptions;
import org.jclouds.ibm.smartcloud.compute.strategy.CreateKeyCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public class IBMSmartCloudComputeServiceDependenciesModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(TemplateOptions.class).to(IBMSmartCloudTemplateOptions.class);
      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {
      }).to(InstanceToNodeMetadata.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<IBMSmartCloudClient, IBMSmartCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<IBMSmartCloudClient, IBMSmartCloudAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<IBMSmartCloudClient, IBMSmartCloudAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class).to(
               CreateKeyCreateNodesWithGroupEncodedIntoNameThenAddToSet.class);
   }

   @Provides
   @Singleton
   Supplier<String> provideSuffix() {
      return new Supplier<String>() {
         final SecureRandom random = new SecureRandom();

         @Override
         public String get() {
            return random.nextInt(100) + "";
         }
      };

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
