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

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.config.BindComputeStrategiesByClass;
import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.compute.config.VCloudExpressComputeServiceContextModule;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeService;
import org.jclouds.vcloud.terremark.compute.domain.KeyPairCredentials;
import org.jclouds.vcloud.terremark.compute.domain.OrgAndName;
import org.jclouds.vcloud.terremark.compute.functions.NodeMetadataToOrgAndName;
import org.jclouds.vcloud.terremark.compute.functions.TerremarkVCloudExpressVAppToNodeMetadata;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.vcloud.terremark.compute.strategy.ParseVAppTemplateDescriptionToGetDefaultLoginCredentials;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Provides;
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

   // prefer jeos as the copy time is much shorter
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).osDescriptionMatches(".*JeOS.*").os64Bit(true);
   }

   @Override
   protected void configure() {
      super.configure();
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

   @Override
   protected void bindVAppConverter() {
      bind(new TypeLiteral<Function<VCloudExpressVApp, NodeMetadata>>() {
      }).to(TerremarkVCloudExpressVAppToNodeMetadata.class);
   }

   @Override
   public BindComputeStrategiesByClass defineComputeStrategyModule() {
      return new TerremarkBindComputeStrategiesByClass();
   }

   @Override
   public BindComputeSuppliersByClass defineComputeSupplierModule() {
      return new TerremarkBindComputeSuppliersByClass();
   }

   @Provides
   @Singleton
   ConcurrentMap<OrgAndName, KeyPairCredentials> credentialsMap() {
      return new ConcurrentHashMap<OrgAndName, KeyPairCredentials>();
   }

   @Named("PASSWORD")
   @Provides
   String providePassword(SecureRandom random) {
      return random.nextLong() + "";
   }

}
