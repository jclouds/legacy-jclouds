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

import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeService;
import org.jclouds.vcloud.terremark.config.TerremarkVCloudContextModule;

import com.google.inject.Provides;

/**
 * Configures the {@link TerremarkVCloudComputeServiceContext}; requires
 * {@link TerremarkVCloudComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudComputeServiceContextModule extends TerremarkVCloudContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ComputeService.class).to(TerremarkVCloudComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<TerremarkVCloudAsyncClient, TerremarkVCloudClient> context) {
      return new ComputeServiceContextImpl<TerremarkVCloudAsyncClient, TerremarkVCloudClient>(
               computeService, context);
   }

}
