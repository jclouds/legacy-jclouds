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
package org.jclouds.rimuhosting.miro.compute.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.jclouds.compute.predicates.RunScriptRunning;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.compute.RimuHostingComputeService;
import org.jclouds.rimuhosting.miro.config.RimuHostingContextModule;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires
 * {@link RimuHostingComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceContextModule extends RimuHostingContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ComputeService.class).to(RimuHostingComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<RimuHostingAsyncClient, RimuHostingClient> context) {
      return new ComputeServiceContextImpl<RimuHostingAsyncClient, RimuHostingClient>(
               computeService, context);
   }

   @Provides
   @Singleton
   @Named("NOT_RUNNING")
   protected Predicate<SshClient> runScriptRunning(RunScriptRunning stateRunning) {
      return new RetryablePredicate<SshClient>(Predicates.not(stateRunning), 600, 3,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   Location getDefaultLocation(Map<String, ? extends Location> locations) {
      return locations.get("DCDALLAS");
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> getDefaultLocations(RimuHostingClient sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer) {
      final Set<Location> locations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {
            locations.add(new LocationImpl(LocationScope.ZONE, from.getDataCenter().getId(), from
                     .getDataCenter().getName(), null, true));
         } catch (NullPointerException e) {
            holder.logger.warn("datacenter not present in " + from.getId());
         }
      }
      holder.logger.debug("<< locations(%d)", locations.size());
      return Maps.uniqueIndex(locations, new Function<Location, String>() {

         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
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
   protected Map<String, ? extends Size> provideSizes(RimuHostingClient sync,
            Map<String, ? extends Image> images, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");
      for (final PricingPlan from : sync.getPricingPlanList()) {
         try {
            sizes.add(new SizeImpl(from.getId(), from.getId(), from.getDataCenter().getId(), null,
                     ImmutableMap.<String, String> of(), from.getDiskSize(), from.getRam(), from
                              .getDiskSize(), ImmutableSet.<Architecture> of(Architecture.X86_32,
                              Architecture.X86_64)));
         } catch (NullPointerException e) {
            holder.logger.warn("datacenter not present in " + from.getId());
         }
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return Maps.uniqueIndex(sizes, indexer);
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern RIMU_PATTERN = Pattern.compile("([^0-9]*)(.*)");

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImages(final RimuHostingClient sync,
            LogHolder holder, Function<ComputeMetadata, String> indexer)
            throws InterruptedException, ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      for (final org.jclouds.rimuhosting.miro.domain.Image from : sync.getImageList()) {
         OsFamily os = null;
         Architecture arch = from.getId().indexOf("64") == -1 ? Architecture.X86_32
                  : Architecture.X86_64;
         String osDescription = "";
         String version = "";

         osDescription = from.getId();

         Matcher matcher = RIMU_PATTERN.matcher(from.getId());
         if (matcher.find()) {
            try {
               os = OsFamily.fromValue(matcher.group(1).toLowerCase());
            } catch (IllegalArgumentException e) {
               holder.logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }

         images.add(new ImageImpl(from.getId(), from.getDescription(), null, null, ImmutableMap
                  .<String, String> of(), from.getDescription(), version, os, osDescription, arch));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return Maps.uniqueIndex(images, indexer);
   }
}
