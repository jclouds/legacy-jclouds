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

package org.jclouds.ec2.compute.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.EC2ComputeService;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.suppliers.RegionAndNameToImageSupplier;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Provides;

/**
 * Configures the {@link ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends BaseComputeServiceContextModule {
   @Override
   protected void configure() {
      installDependencies();
      install(new EC2BindComputeStrategiesByClass());
      install(new EC2BindComputeSuppliersByClass());
      super.configure();
   }
   
   protected void installDependencies(){
      install(new EC2ComputeServiceDependenciesModule());
   }

   @Provides
   @Singleton
   protected Supplier<Map<RegionAndName, ? extends Image>> provideRegionAndNameToImageSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final RegionAndNameToImageSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<RegionAndName, ? extends Image>>(
               authException, seconds, new Supplier<Map<RegionAndName, ? extends Image>>() {
                  @Override
                  public Map<RegionAndName, ? extends Image> get() {
                     return supplier.get();
                  }
               });
   }

}
