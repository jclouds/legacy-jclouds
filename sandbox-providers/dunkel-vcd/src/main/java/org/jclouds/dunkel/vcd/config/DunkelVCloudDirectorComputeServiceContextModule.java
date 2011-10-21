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
package org.jclouds.dunkel.vcd.config;

import static org.jclouds.compute.domain.OsFamily.RHEL;

import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceContextModule;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;

import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
public class DunkelVCloudDirectorComputeServiceContextModule extends VCloudComputeServiceContextModule {

   @Override
   protected TemplateOptions provideTemplateOptions(Injector injector, TemplateOptions options) {
      return options.as(VCloudTemplateOptions.class).ipAddressAllocationMode(IpAddressAllocationMode.POOL);
   }
   
   //CIM ostype does not include version info
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(RHEL).os64Bit(true);
   }
}
