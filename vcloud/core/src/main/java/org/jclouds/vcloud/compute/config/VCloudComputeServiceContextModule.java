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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.VCloudComputeClient;
import org.jclouds.vcloud.compute.VCloudComputeService;
import org.jclouds.vcloud.config.VCloudContextModule;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
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
   protected Map<String, ? extends Image> provideImages(final VCloudClient client,
            final Location vDC, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            ExecutionException, TimeoutException {
      // TODO multi-VDC
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      Catalog response = client.getDefaultCatalog();
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();

      for (final NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
            final CatalogItem item = client.getCatalogItem(resource.getId());
            if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
               responses.add(ConcurrentUtils.makeListenable(executor.submit(new Callable<Void>() {
                  @Override
                  public Void call() throws Exception {
                     OsFamily myOs = null;
                     for (OsFamily os : OsFamily.values()) {
                        if (resource.getName().toLowerCase().replaceAll("\\s", "").indexOf(
                                 os.toString()) != -1) {
                           myOs = os;
                        }
                     }
                     Architecture arch = resource.getName().indexOf("64") == -1 ? Architecture.X86_32
                              : Architecture.X86_64;
                     VAppTemplate template = client.getVAppTemplate(item.getEntity().getId());
                     images.add(new ImageImpl(resource.getId(), template.getName(), vDC.getId(),
                              template.getLocation(), ImmutableMap.<String, String> of(), template
                                       .getDescription(), "", myOs, template.getName(), arch));
                     return null;
                  }
               }), executor));
            }
         }
      }

      ConcurrentUtils.awaitCompletion(responses, executor, null, holder.logger, "images");
      return Maps.uniqueIndex(images, indexer);
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getId();
         }
      };
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> provideLocations(final VCloudClient client) {
      return Maps.uniqueIndex(Iterables.transform(client.getDefaultOrganization().getVDCs()
               .values(), new Function<NamedResource, Location>() {

         @Override
         public Location apply(NamedResource from) {
            VDC vdc = client.getVDC(from.getId());
            return new LocationImpl(LocationScope.ZONE, vdc.getId(), vdc.getName(), null, true);
         }

      }), new Function<Location, String>() {

         @Override
         public String apply(Location from) {
            return from.getId();
         }

      });
   }

   @Provides
   @Singleton
   Location getVDC(VCloudClient client, Map<String, ? extends Location> locations) {
      return locations.get(client.getDefaultVDC().getId());
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Size> provideSizes(Function<ComputeMetadata, String> indexer,
            VCloudClient client, Map<String, ? extends Image> images, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor)
            throws InterruptedException, TimeoutException, ExecutionException {
      Set<Size> sizes = Sets.newHashSet();
      for (int cpus : new int[] { 1, 2, 4 })
         for (int ram : new int[] { 512, 1024, 2048, 4096, 8192, 16384 })
            sizes.add(new SizeImpl(String.format("cpu=%d,ram=%s,disk=%d", cpus, ram, 10), null,
                     null, null, ImmutableMap.<String, String> of(), cpus, ram, 10, ImmutableSet
                              .<Architecture> of(Architecture.X86_32, Architecture.X86_64)));
      return Maps.uniqueIndex(sizes, indexer);
   }
}
