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

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.compute.CommonVCloudComputeClient;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.compute.functions.ImagesInVCloudExpressOrg;
import org.jclouds.vcloud.compute.functions.ParseOsFromVAppTemplateName;
import org.jclouds.vcloud.compute.functions.VCloudExpressVAppToNodeMetadata;
import org.jclouds.vcloud.compute.internal.VCloudExpressComputeClientImpl;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.VCloudExpressVApp;

import com.google.common.base.Function;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires
 * {@link VCloudExpressComputeClientImpl} bound.
 * 
 * @author Adrian Cole
 */
public class VCloudExpressComputeServiceContextModule extends CommonVCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bindVAppConverter();
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<VCloudExpressClient, VCloudExpressClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<VCloudExpressClient, VCloudExpressClient>>() {
      }).to(new TypeLiteral<RestContextImpl<VCloudExpressClient, VCloudExpressClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Function<Org, Iterable<? extends Image>>>() {
      }).to(new TypeLiteral<ImagesInVCloudExpressOrg>() {
      });
      bind(new TypeLiteral<Function<String, OperatingSystem>>() {
      }).to(ParseOsFromVAppTemplateName.class);
   }

   protected void bindVAppConverter() {
      bind(new TypeLiteral<Function<VCloudExpressVApp, NodeMetadata>>() {
      }).to(VCloudExpressVAppToNodeMetadata.class);
   }

   @Provides
   @Singleton
   CommonVCloudComputeClient provideCommonVCloudComputeClient(VCloudExpressComputeClient in) {
      return in;
   }

   @Override
   public BindComputeStrategiesByClass defineComputeStrategyModule() {
      return new VCloudExpressBindComputeStrategiesByClass();
   }

   @Override
   public BindComputeSuppliersByClass defineComputeSupplierModule() {
      return new CommonVCloudBindComputeSuppliersByClass();
   }
}
