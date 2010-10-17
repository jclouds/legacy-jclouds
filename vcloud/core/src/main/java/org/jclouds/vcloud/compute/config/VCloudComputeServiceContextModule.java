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

import java.util.Set;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
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
import org.jclouds.vcloud.compute.strategy.VCloudAddNodeWithTagStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudDestroyNodeStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudGetNodeMetadataStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudListNodesStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudRebootNodeStrategy;
import org.jclouds.vcloud.compute.suppliers.VCloudHardwareSupplier;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link VCloudComputeClientImpl}
 * bound.
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceContextModule extends CommonVCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<VApp, NodeMetadata>>() {
      }).to(VAppToNodeMetadata.class);
      bind(TemplateOptions.class).to(VCloudTemplateOptions.class);
      bind(TemplateBuilder.class).to(VCloudTemplateBuilderImpl.class);
      bind(RebootNodeStrategy.class).to(VCloudRebootNodeStrategy.class);
      bind(new TypeLiteral<Function<VApp, Hardware>>() {
      }).to(new TypeLiteral<HardwareForVApp>() {
      });
      bind(GetNodeMetadataStrategy.class).to(VCloudGetNodeMetadataStrategy.class);
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
      bind(AddNodeWithTagStrategy.class).to(VCloudAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudListNodesStrategy.class);
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(GetLoginCredentialsFromGuestConfiguration.class);
      bind(DestroyNodeStrategy.class).to(VCloudDestroyNodeStrategy.class);
   }

   @Override
   protected Supplier<Set<? extends Hardware>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(VCloudHardwareSupplier.class);
   }

}
