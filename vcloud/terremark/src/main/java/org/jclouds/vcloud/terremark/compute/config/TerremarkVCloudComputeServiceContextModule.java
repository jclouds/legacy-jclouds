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

import java.util.LinkedHashSet;
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
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeService;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudTemplate;
import org.jclouds.vcloud.terremark.config.TerremarkVCloudContextModule;
import org.jclouds.vcloud.terremark.domain.ComputeOptions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
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

   private static final ComputeOptionsToSize sizeConverter = new ComputeOptionsToSize();

   private static class ComputeOptionsToSize implements Function<ComputeOptions, Size> {

      @Override
      public Size apply(ComputeOptions from) {
         return new SizeImpl(from.getProcessorCount(), (int) from.getMemory(), null, ImmutableSet
                  .<Architecture> of(Architecture.X86_32, Architecture.X86_64));
      }

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
   Set<? extends Size> provideSizes(TerremarkVCloudClient client, Set<? extends Image> images,
            LogHolder holder) {
      Image anyImage = Iterables.get(images, 0);
      holder.logger.debug(">> providing sizes");
      LinkedHashSet<Size> sizes = Sets.newLinkedHashSet(Iterables.transform(client
               .getComputeOptionsOfCatalogItem(anyImage.getId()), sizeConverter));
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

   @Provides
   @Singleton
   Set<TerremarkVCloudTemplate> provideTemplates(TerremarkVCloudClient client,
            Set<? extends Image> images, Set<? extends Size> sizes, LogHolder holder) {
      Set<TerremarkVCloudTemplate> templates = Sets.newHashSet();
      holder.logger.debug(">> generating templates");
      String vDC = client.getDefaultVDC().getId();
      for (Size size : sizes) {
         for (Image image : images) {
            templates.add(new TerremarkVCloudTemplate(client, images, sizes, vDC, size, image
                     .getOperatingSystem(), image));
         }
      }
      holder.logger.debug("<< templates(%d)", templates.size());
      return templates;
   }

}
