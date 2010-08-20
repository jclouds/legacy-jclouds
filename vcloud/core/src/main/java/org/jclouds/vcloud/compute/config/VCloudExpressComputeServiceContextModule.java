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
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.domain.Location;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.compute.BaseVCloudExpressComputeClient;
import org.jclouds.vcloud.compute.strategy.VCloudExpressAddNodeWithTagStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudExpressDestroyNodeStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudExpressGetNodeMetadataStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudExpressListNodesStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudExpressRebootNodeStrategy;
import org.jclouds.vcloud.compute.suppliers.OrganizationAndVDCToLocationSupplier;
import org.jclouds.vcloud.compute.suppliers.VCloudExpressImageSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires
 * {@link BaseVCloudExpressComputeClient} bound.
 * 
 * @author Adrian Cole
 */
public class VCloudExpressComputeServiceContextModule extends CommonVCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<VCloudExpressClient, VCloudExpressClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<VCloudExpressClient, VCloudExpressClient>>() {
      }).to(new TypeLiteral<RestContextImpl<VCloudExpressClient, VCloudExpressClient>>() {
      }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(VCloudExpressAddNodeWithTagStrategy.class);
      bind(DestroyNodeStrategy.class).to(VCloudExpressDestroyNodeStrategy.class);
      bind(RunNodesAndAddToSetStrategy.class).to(EncodeTagIntoNameRunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudExpressListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(VCloudExpressGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(VCloudExpressRebootNodeStrategy.class);
   }

   @Override
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Injector injector) {
      return injector.getInstance(OrganizationAndVDCToLocationSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      return injector.getInstance(VCloudExpressImageSupplier.class);
   }

}
