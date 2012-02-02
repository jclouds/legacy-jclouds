/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.inject.Injector;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link VCloudComputeClientImpl}
 * bound.
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceContextModule
         extends
         ComputeServiceAdapterContextModule<VCloudClient, VCloudAsyncClient, VApp, VAppTemplate, VAppTemplate, Location> {
   public VCloudComputeServiceContextModule() {
      super(VCloudClient.class, VCloudAsyncClient.class);
   }

   @Override
   protected void configure() {
      super.configure();
      install(new VCloudComputeServiceDependenciesModule());
   }
   
   // CIM ostype does not include version info
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).os64Bit(true);
   }

}
