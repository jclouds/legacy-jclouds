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
package org.jclouds.vcloud.bluelock.compute.config;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.vcloud.bluelock.compute.BlueLockVCloudComputeClient;
import org.jclouds.vcloud.bluelock.compute.config.providers.ParseVAppTemplatesInVDCToSizeProvider;
import org.jclouds.vcloud.bluelock.compute.strategy.DefaultLoginCredentialsFromBlueLockFAQ;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceContextModule;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.compute.functions.ImageForVAppTemplate;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link BlueLockVCloudComputeServiceContext}; requires
 * {@link BlueLockVCloudComputeClient} bound.
 * 
 * @author Adrian Cole
 */
public class BlueLockVCloudComputeServiceContextModule extends VCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ImageForVAppTemplate.class).to(BlueLockImageForVAppTemplate.class);
      bind(VCloudComputeClient.class).to(BlueLockVCloudComputeClient.class);
      bind(PopulateDefaultLoginCredentialsForImageStrategy.class).to(DefaultLoginCredentialsFromBlueLockFAQ.class);
   }

   protected void bindSizes() {
      bind(new TypeLiteral<Set<? extends Size>>() {
      }).toProvider(ParseVAppTemplatesInVDCToSizeProvider.class).in(Scopes.SINGLETON);
   }

   @Singleton
   private static class BlueLockImageForVAppTemplate extends ImageForVAppTemplate {

      @Inject
      protected BlueLockImageForVAppTemplate(FindLocationForResource findLocationForResource,
               PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider) {
         super(findLocationForResource, credentialsProvider);
      }

      // Extremely important, as otherwise the size encoded into the name will throw off the
      // template matching, accidentally choosing the largest size by default
      protected String getName(String name) {
         return name.split(" ")[0];
      }
   }
}
