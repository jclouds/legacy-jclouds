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
package org.jclouds.rackspace.cloudservers.compute.config;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.ResourceLocation;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.CloudServersAsyncClient;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.compute.CloudServersComputeService;
import org.jclouds.rackspace.cloudservers.compute.domain.CloudServersImage;
import org.jclouds.rackspace.cloudservers.compute.domain.CloudServersSize;
import org.jclouds.rackspace.cloudservers.config.CloudServersContextModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

/**
 * Configures the {@link CloudServersComputeServiceContext}; requires
 * {@link CloudServersComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class CloudServersComputeServiceContextModule extends CloudServersContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ComputeService.class).to(CloudServersComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<CloudServersAsyncClient, CloudServersClient> context) {
      return new ComputeServiceContextImpl<CloudServersAsyncClient, CloudServersClient>(
               computeService, context);
   }

   @Provides
   @Singleton
   @ResourceLocation
   String getRegion() {
      return "default";
   }

   @Provides
   @Singleton
   protected Set<? extends Size> provideSizes(CloudServersClient sync, Set<? extends Image> images,
            LogHolder holder, ExecutorService executor) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");
      for (final Flavor from : sync.listFlavors(ListOptions.Builder.withDetails())) {
         sizes.add(new CloudServersSize(from, from.getId() + "", from.getDisk() / 10,
                  from                  .getRam(), from.getDisk(), ImmutableSet.<Architecture> of(Architecture.X86_32,
                  Architecture.X86_64)));
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern RACKSPACE_PATTERN = Pattern.compile("(([^ ]*) .*)");

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final CloudServersClient sync,
            @ResourceLocation String location, LogHolder holder) throws InterruptedException,
            ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      for (final org.jclouds.rackspace.cloudservers.domain.Image from : sync
               .listImages(ListOptions.Builder.withDetails())) {
         OsFamily os = null;
         Architecture arch = Architecture.X86_64;
         String osDescription = "";
         String version = "";

         Matcher matcher = RACKSPACE_PATTERN.matcher(from.getName());
         if (matcher.find()) {
            try {
               os = OsFamily.fromValue(matcher.group(2).toLowerCase());
               osDescription = matcher.group(1);
            } catch (IllegalArgumentException e) {
               holder.logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }
         images.add(new CloudServersImage(from, location, arch, os, osDescription, version));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }
}
