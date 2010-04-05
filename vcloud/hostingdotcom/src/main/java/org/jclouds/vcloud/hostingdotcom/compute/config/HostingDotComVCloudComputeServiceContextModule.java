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
package org.jclouds.vcloud.hostingdotcom.compute.config;

import static org.jclouds.compute.domain.OsFamily.CENTOS;

import java.util.Map;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.TemplateBuilderImpl;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceContextModule;
import org.jclouds.vcloud.hostingdotcom.compute.HostingDotComVCloudComputeClient;

import com.google.inject.Provides;

/**
 * Configures the {@link HostingDotComVCloudComputeServiceContext}; requires
 * {@link HostingDotComVCloudComputeClient} bound.
 * 
 * @author Adrian Cole
 */
public class HostingDotComVCloudComputeServiceContextModule extends
         VCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(VCloudComputeClient.class).to(HostingDotComVCloudComputeClient.class);
   }

   @Override
   @Provides
   protected TemplateBuilder provideTemplate(Map<String, ? extends Location> locations,
            Map<String, ? extends Image> images, Map<String, ? extends Size> sizes,
            Location defaultLocation) {
      return new TemplateBuilderImpl(locations, images, sizes, defaultLocation).osFamily(CENTOS);
   }

}
