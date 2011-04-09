/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev.compute.config;

import static org.jclouds.compute.domain.OsFamily.SUSE;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;

import com.google.inject.Injector;

/**
 * Configures the {@link IBMDeveloperCloudComputeServiceContext}; requires
 * {@link BaseComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class IBMDeveloperCloudComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new IBMDeveloperCloudComputeServiceDependenciesModule());
      install(new IBMDeveloperCloudBindComputeStrategiesByClass());
      install(new IBMDeveloperCloudBindComputeSuppliersByClass());
      super.configure();
   }

   /**
    * tested known configuration
    */
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(SUSE);
   }
}
