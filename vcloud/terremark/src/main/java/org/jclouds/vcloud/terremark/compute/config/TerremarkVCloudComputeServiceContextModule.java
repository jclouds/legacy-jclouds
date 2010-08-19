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

package org.jclouds.vcloud.terremark.compute.config;


import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.compute.config.VCloudExpressComputeServiceContextModule;
import org.jclouds.vcloud.compute.strategy.VCloudExpressDestroyNodeStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudExpressListNodesStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudExpressRebootNodeStrategy;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeService;
import org.jclouds.vcloud.terremark.compute.domain.KeyPairCredentials;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.compute.functions.NodeMetadataToOrgAndName;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.vcloud.terremark.compute.strategy.ParseVAppTemplateDescriptionToGetDefaultLoginCredentials;
import org.jclouds.vcloud.terremark.compute.strategy.TerremarkEncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.vcloud.terremark.compute.strategy.TerremarkVCloudAddNodeWithTagStrategy;
import org.jclouds.vcloud.terremark.compute.strategy.TerremarkVCloudGetNodeMetadataStrategy;
import org.jclouds.vcloud.terremark.compute.suppliers.VAppTemplatesInOrgs;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TerremarkVCloudComputeServiceContext}; requires
 * {@link TerremarkVCloudComputeClientImpl} bound.
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudComputeServiceContextModule extends VCloudExpressComputeServiceContextModule {

   @Provides
   @Singleton
   Supplier<String> provideSuffix(final SecureRandom random) {
      return new Supplier<String>() {
         @Override
         public String get() {
            return random.nextInt(4096) + "";
         }
      };

   }

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      // NOTE
      bind(AddNodeWithTagStrategy.class).to(TerremarkVCloudAddNodeWithTagStrategy.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<VCloudExpressClient, VCloudExpressAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      // NOTE
      bind(RunNodesAndAddToSetStrategy.class).to(TerremarkEncodeTagIntoNameRunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudExpressListNodesStrategy.class);
      // NOTE
      bind(GetNodeMetadataStrategy.class).to(TerremarkVCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(VCloudExpressRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(VCloudExpressDestroyNodeStrategy.class);
      bindLoadBalancer();
      // MORE specifics...
      bind(new TypeLiteral<Function<NodeMetadata, OrgAndName>>() {
      }).to(new TypeLiteral<NodeMetadataToOrgAndName>() {
      });
      bind(TemplateOptions.class).to(TerremarkVCloudTemplateOptions.class);
      bind(ComputeService.class).to(TerremarkVCloudComputeService.class);
      bind(VCloudExpressComputeClient.class).to(TerremarkVCloudComputeClient.class);
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(
               ParseVAppTemplateDescriptionToGetDefaultLoginCredentials.class);
      bind(SecureRandom.class).toInstance(new SecureRandom());
   }

   @Provides
   @Singleton
   ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap() {
      return new ConcurrentHashMap<OrgAndName, KeyPairCredentials>();
   }

   // TODO
   // @Override
   // protected void bindLoadBalancer() {
   // bind(LoadBalanceNodesStrategy.class).to(TerremarkLoadBalanceNodesStrategy.class);
   // bind(DestroyLoadBalancerStrategy.class).to(TerremarkDestroyLoadBalancerStrategy.class);
   // }
   //   

   @Named("PASSWORD")
   @Provides
   String providePassword(SecureRandom random) {
      return random.nextLong() + "";
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      return injector.getInstance(VAppTemplatesInOrgs.class);
   }

}
