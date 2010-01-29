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
import org.jclouds.rackspace.cloudservers.CloudServersAsyncClient;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.compute.CloudServersComputeService;
import org.jclouds.rackspace.cloudservers.config.CloudServersContextModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rest.RestContext;
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
   @Named("NOT_RUNNING")
   protected Predicate<SshClient> runScriptRunning(RunScriptRunning stateRunning) {
      return new RetryablePredicate<SshClient>(Predicates.not(stateRunning), 600, 3,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   Location getRegion() {
      return new LocationImpl(LocationScope.ZONE, "DALLAS", "Dallas, TX", null, true);
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> provideLocations(Location location) {
      return ImmutableMap.of(location.getId(), location);
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
   protected Map<String, ? extends Size> provideSizes(CloudServersClient sync,
            Map<String, ? extends Image> images, Location location, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");
      for (final Flavor from : sync.listFlavors(ListOptions.Builder.withDetails())) {
         sizes.add(new SizeImpl(from.getId() + "", from.getName(), location.getId(), null,
                  ImmutableMap.<String, String> of(), from.getDisk() / 10, from.getRam(), from
                           .getDisk(), ImmutableSet.<Architecture> of(Architecture.X86_32,
                           Architecture.X86_64)));
      }
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return Maps.uniqueIndex(sizes, indexer);
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   public static final Pattern RACKSPACE_PATTERN = Pattern.compile("(([^ ]*) .*)");

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImages(final CloudServersClient sync,
            Location location, LogHolder holder, Function<ComputeMetadata, String> indexer)
            throws InterruptedException, ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      for (final org.jclouds.rackspace.cloudservers.domain.Image from : sync
               .listImages(ListOptions.Builder.withDetails())) {
         OsFamily os = null;
         Architecture arch = Architecture.X86_64;
         String osDescription = "";
         String version = "";
         Matcher matcher = RACKSPACE_PATTERN.matcher(from.getName());
         osDescription = from.getName();
         if (from.getName().indexOf("Red Hat EL") != -1) {
            os = OsFamily.RHEL;
         } else if (matcher.find()) {
            try {
               os = OsFamily.fromValue(matcher.group(2).toLowerCase());
            } catch (IllegalArgumentException e) {
               holder.logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }
         images.add(new ImageImpl(from.getId() + "", from.getName(), location.getId(), null,
                  ImmutableMap.<String, String> of(), from.getName(), version, os, osDescription,
                  arch));
      }
      holder.logger.debug("<< images(%d)", images.size());
      return Maps.uniqueIndex(images, indexer);
   }
}
