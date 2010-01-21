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
package org.jclouds.vcloud.compute.config;

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.VCloudComputeService;
import org.jclouds.vcloud.compute.VCloudTemplate;
import org.jclouds.vcloud.config.VCloudContextModule;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires {@link VCloudComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class VCloudComputeServiceContextModule extends VCloudContextModule {

   @Override
   protected void configure() {
      super.configure();
   }

   @Provides
   @Singleton
   protected ComputeService provideComputeService(Injector injector) {
      return injector.getInstance(VCloudComputeService.class);
   }

   @Provides
   @Singleton
   protected VCloudComputeClient provideComputeClient(VCloudComputeService in) {
      return in;
   }

   @Provides
   @Singleton
   protected ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<VCloudAsyncClient, VCloudClient> context) {
      return new ComputeServiceContextImpl<VCloudAsyncClient, VCloudClient>(computeService, context);
   }

   protected static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      public Logger logger = Logger.NULL;
   }

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final VCloudClient client, LogHolder holder,
            ExecutorService executor) throws InterruptedException, ExecutionException,
            TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      Catalog response = client.getDefaultCatalog();
      final String vDC = client.getDefaultVDC().getId();
      Set<Future<Void>> responses = Sets.newHashSet();

      for (final NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
            final CatalogItem item = client.getCatalogItem(resource.getId());
            if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
               responses.add(executor.submit(new Callable<Void>() {
                  @Override
                  public Void call() throws Exception {
                     OperatingSystem myOs = OperatingSystem.UNKNOWN;
                     for (OperatingSystem os : OperatingSystem.values()) {
                        if (resource.getName().toUpperCase().replaceAll("\\s", "").indexOf(
                                 os.toString()) != -1) {
                           myOs = os;
                        }
                     }
                     Architecture arch = resource.getName().matches("64[- ]bit") ? Architecture.X86_32
                              : Architecture.X86_64;
                     VAppTemplate template = client.getVAppTemplate(item.getEntity().getId());
                     images.add(new ImageImpl(resource.getId(), template.getDescription(), myOs,
                              null, vDC, arch));
                     return null;
                  }
               }));
            }
         }
      }
      pollResponsesAndLogWhenComplete(images.size(), "images", holder, responses);
      return images;
   }

   protected void pollResponsesAndLogWhenComplete(int total, String description, LogHolder holder,
            Set<Future<Void>> responses) throws InterruptedException, TimeoutException,
            ExecutionException {
      int complete = 0;
      long start = System.currentTimeMillis();
      long timeOut = 60 * 1000;
      do {
         Set<Future<Void>> retries = Sets.newHashSet();
         for (Future<Void> future : responses) {
            try {
               future.get(100, TimeUnit.MILLISECONDS);
               complete++;
            } catch (ExecutionException e) {
               Throwables.propagate(e);
            } catch (TimeoutException e) {
               retries.add(future);
            }
         }
         responses = Sets.newHashSet(retries);
      } while (responses.size() > 0 && System.currentTimeMillis() < start + timeOut);
      long duration = System.currentTimeMillis() - start;
      if (duration > timeOut)
         throw new TimeoutException(String.format("TIMEOUT: %s(%d/%d) rate: %f %s/second%n",
                  description, complete, total, ((double) complete) / (duration / 1000.0),
                  description));
      for (Future<Void> future : responses)
         future.get(30, TimeUnit.SECONDS);
      holder.logger.debug("<< %s(%d) rate: %f %s/second%n", description, total, ((double) complete)
               / (duration / 1000.0), description);
   }

   @Provides
   @Singleton
   protected SortedSet<? extends Size> provideSizes(VCloudClient client,
            Set<? extends Image> images, LogHolder holder, ExecutorService executor)
            throws InterruptedException, TimeoutException, ExecutionException {
      return ImmutableSortedSet.of(new SizeImpl(1, 512, 10, ImmutableSet.<Architecture> of(
               Architecture.X86_32, Architecture.X86_64)));
   }

   @Provides
   @Singleton
   protected Set<? extends VCloudTemplate> provideTemplates(VCloudClient client,
            Set<? extends Image> images, SortedSet<? extends Size> sizes, LogHolder holder) {
      Set<VCloudTemplate> templates = Sets.newHashSet();
      holder.logger.debug(">> generating templates");
      String vDC = client.getDefaultVDC().getId();
      for (Size size : sizes) {
         for (Image image : images) {
            templates.add(new VCloudTemplate(client, images, sizes, vDC, size, image
                     .getOperatingSystem(), image));
         }
      }
      holder.logger.debug("<< templates(%d)", templates.size());
      return templates;
   }

}
