/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import javax.inject.Named;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceContextModule;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeClient;
import org.jclouds.vcloud.terremark.compute.config.providers.ComputeOptionsToSizeProvider;
import org.jclouds.vcloud.terremark.compute.config.providers.QueryCatalogForVAppTemplatesAndConvertToImagesProvider;
import org.jclouds.vcloud.terremark.compute.strategy.ParseVAppTemplateDescriptionToGetDefaultLoginCredentials;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TerremarkVCloudComputeServiceContext}; requires
 * {@link TerremarkVCloudComputeClientImpl} bound.
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudComputeServiceContextModule extends VCloudComputeServiceContextModule {

   public TerremarkVCloudComputeServiceContextModule(String providerName) {
      super(providerName);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(VCloudComputeClient.class).to(TerremarkVCloudComputeClient.class);
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(
               ParseVAppTemplateDescriptionToGetDefaultLoginCredentials.class);
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
   String providePassword() {
      return new SecureRandom().nextLong() + "";
   }

   @Override
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(OsFamily.UBUNTU);
   }

   @Override
   protected void bindImages() {
      bind(new TypeLiteral<Set<? extends Image>>() {
      }).toProvider(QueryCatalogForVAppTemplatesAndConvertToImagesProvider.class).in(
               Scopes.SINGLETON);
   }

   @Override
   protected void bindSizes() {
      bind(new TypeLiteral<Set<? extends Size>>() {
      }).toProvider(ComputeOptionsToSizeProvider.class).in(Scopes.SINGLETON);
   }

}
