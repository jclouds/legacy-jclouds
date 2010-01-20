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

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudAsyncClient;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;
import org.jclouds.vcloud.hostingdotcom.compute.HostingDotComVCloudComputeService;
import org.jclouds.vcloud.hostingdotcom.compute.HostingDotComVCloudTemplate;
import org.jclouds.vcloud.hostingdotcom.config.HostingDotComVCloudContextModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

/**
 * Configures the {@link HostingDotComVCloudComputeServiceContext}; requires
 * {@link HostingDotComVCloudComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class HostingDotComVCloudComputeServiceContextModule extends
         HostingDotComVCloudContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ComputeService.class).to(HostingDotComVCloudComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<HostingDotComVCloudAsyncClient, HostingDotComVCloudClient> context) {
      return new ComputeServiceContextImpl<HostingDotComVCloudAsyncClient, HostingDotComVCloudClient>(
               computeService, context);
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }
   
   @Provides
   @Singleton
   Set<? extends Image> provideImages(VCloudClient client, LogHolder holder) {
      Set<Image> images = Sets.newLinkedHashSet();
      holder.logger.debug(">> providing images");
      Catalog response = client.getDefaultCatalog();
      String vDC = client.getDefaultVDC().getId();
      for (NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {

            CatalogItem item = client.getCatalogItem(resource.getId());
            OperatingSystem myOs = OperatingSystem.UNKNOWN;
            for (OperatingSystem os : OperatingSystem.values()) {
               if (resource.getName().toUpperCase().replaceAll("\\s", "").indexOf(os.toString()) != -1) {
                  myOs = os;
               }
            }
            Architecture arch = resource.getName().matches("64[- ]bit") ? Architecture.X86_32
                     : Architecture.X86_64;
            if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
               VAppTemplate template = client.getVAppTemplate(item.getEntity().getId());
               images.add(new ImageImpl(resource.getId(), template.getDescription(), myOs, null,
                        vDC, arch));
            }
         }
      }
      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }

   @Provides
   @Singleton
   Set<? extends Size> provideSizes(HostingDotComVCloudClient client, Set<? extends Image> images,
            LogHolder holder) {
      return ImmutableSet.<Size> of(new SizeImpl(1, 512, (int) (10l * 1025 * 1024), ImmutableSet
               .<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
   }

   @Provides
   @Singleton
   Set<HostingDotComVCloudTemplate> provideTemplates(HostingDotComVCloudClient client,
            Set<? extends Image> images, Set<? extends Size> sizes, LogHolder holder) {
      Set<HostingDotComVCloudTemplate> templates = Sets.newHashSet();
      holder.logger.debug(">> generating templates");
      String vDC = client.getDefaultVDC().getId();
      for (Size size : sizes) {
         for (Image image : images) {
            templates.add(new HostingDotComVCloudTemplate(client, images, sizes, vDC, size, image
                     .getOperatingSystem(), image));
         }
      }
      holder.logger.debug("<< templates(%d)", templates.size());
      return templates;
   }

}
