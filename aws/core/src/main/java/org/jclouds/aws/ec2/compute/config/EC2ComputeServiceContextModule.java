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
package org.jclouds.aws.ec2.compute.config;

import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.ownedBy;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.EC2ComputeService;
import org.jclouds.aws.ec2.compute.EC2Image;
import org.jclouds.aws.ec2.compute.EC2Size;
import org.jclouds.aws.ec2.config.EC2ContextModule;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.ResourceLocation;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

/**
 * Configures the {@link EC2ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends EC2ContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ComputeService.class).to(EC2ComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<EC2AsyncClient, EC2Client> context) {
      return new ComputeServiceContextImpl<EC2AsyncClient, EC2Client>(computeService, context);
   }

   @Provides
   @Singleton
   Set<? extends Size> provideSizes() {
      return ImmutableSet.of(EC2Size.C1_MEDIUM, EC2Size.C1_XLARGE, EC2Size.M1_LARGE,
               EC2Size.M1_SMALL, EC2Size.M1_XLARGE, EC2Size.M2_2XLARGE, EC2Size.M2_4XLARGE);
   }

   @Provides
   @Singleton
   @ResourceLocation
   String getRegion(@EC2 Region region) {
      return region.value();
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   // alestic-32-eu-west-1/debian-4.0-etch-base-20081130.manifest.xml
   public static final Pattern ALESTIC_PATTERN = Pattern
            .compile(".*/([^-]*)-([^-]*)-.*-(.*)\\.manifest\\.xml");

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final EC2Client sync, Map<Region, URI> regionMap,
            LogHolder holder) throws InterruptedException, ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");
      for (final Region region : regionMap.keySet()) {
         for (final org.jclouds.aws.ec2.domain.Image from : sync.getAMIServices()
                  .describeImagesInRegion(region, ownedBy("063491364108"))) {
            OperatingSystem os = null;
            String osVersion = "";
            String version = "";

            Matcher matcher = ALESTIC_PATTERN.matcher(from.getImageLocation());
            if (matcher.find()) {
               try {
                  os = OperatingSystem.fromValue(matcher.group(1));
                  osVersion = matcher.group(2);
                  version = matcher.group(3);
               } catch (IllegalArgumentException e) {
                  holder.logger.debug("<< didn't match os(%s)", matcher.group(1));
               }
            }
            images.add(new EC2Image(from, os, osVersion, version));
         }
      }
      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }
}
