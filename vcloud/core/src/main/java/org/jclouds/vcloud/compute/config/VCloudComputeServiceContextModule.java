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

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.compute.strategy.VCloudAddNodeWithTagStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudDestroyNodeStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudGetNodeMetadataStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudListNodesStrategy;
import org.jclouds.vcloud.compute.strategy.VCloudRebootNodeStrategy;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link BaseVCloudComputeClient}
 * bound.
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceContextModule extends CommonVCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<VCloudClient, VCloudClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<VCloudClient, VCloudClient>>() {
      }).to(new TypeLiteral<RestContextImpl<VCloudClient, VCloudClient>>() {
      }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(VCloudAddNodeWithTagStrategy.class);
      bind(DestroyNodeStrategy.class).to(VCloudDestroyNodeStrategy.class);
      bind(RunNodesAndAddToSetStrategy.class).to(EncodeTagIntoNameRunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(VCloudListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(VCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(VCloudRebootNodeStrategy.class);
   }
}
