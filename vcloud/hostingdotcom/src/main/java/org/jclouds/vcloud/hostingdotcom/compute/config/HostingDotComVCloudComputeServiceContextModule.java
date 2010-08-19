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

package org.jclouds.vcloud.hostingdotcom.compute.config;

import static org.jclouds.compute.domain.OsFamily.CENTOS;

import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.vcloud.compute.VCloudExpressComputeClient;
import org.jclouds.vcloud.compute.config.VCloudExpressComputeServiceContextModule;
import org.jclouds.vcloud.hostingdotcom.compute.HostingDotComVCloudComputeClient;

import com.google.inject.Injector;

/**
 * Configures the {@link HostingDotComVCloudComputeServiceContext}; requires
 * {@link HostingDotComVCloudComputeClient} bound.
 * 
 * @author Adrian Cole
 */
public class HostingDotComVCloudComputeServiceContextModule extends VCloudExpressComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(VCloudExpressComputeClient.class).to(HostingDotComVCloudComputeClient.class);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(CENTOS);
   }

}
