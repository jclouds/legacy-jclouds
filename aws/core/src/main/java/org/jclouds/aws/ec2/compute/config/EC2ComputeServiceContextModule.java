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
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

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
import org.jclouds.aws.ec2.compute.domain.EC2Size;
import org.jclouds.aws.ec2.compute.domain.KeyPairCredentials;
import org.jclouds.aws.ec2.compute.domain.PortsRegionTag;
import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.compute.functions.CreateKeyPairIfNeeded;
import org.jclouds.aws.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.aws.ec2.config.EC2ContextModule;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
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
   protected final Map<RegionTag, KeyPairCredentials> credentialsMap(CreateKeyPairIfNeeded in) {
      // doesn't seem to clear when someone issues remove(key)
      // return new MapMaker().makeComputingMap(in);
      return Maps.newLinkedHashMap();
   }

   @Provides
   @Singleton
   protected final Map<PortsRegionTag, String> securityGroupMap(CreateSecurityGroupIfNeeded in) {
      // doesn't seem to clear when someone issues remove(key)
      // return new MapMaker().makeComputingMap(in);
      return Maps.newLinkedHashMap();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<EC2AsyncClient, EC2Client> context) {
      return new ComputeServiceContextImpl<EC2AsyncClient, EC2Client>(computeService, context);
   }

   @Provides
   @Singleton
   Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getId();
         }
      };
   }

   @Provides
   @Singleton
   Map<String, ? extends Size> provideSizes(Function<ComputeMetadata, String> indexer) {
      return Maps.uniqueIndex(ImmutableSet.of(EC2Size.C1_MEDIUM, EC2Size.C1_XLARGE,
               EC2Size.M1_LARGE, EC2Size.M1_SMALL, EC2Size.M1_XLARGE, EC2Size.M2_2XLARGE,
               EC2Size.M2_4XLARGE), indexer);
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> provideLocations(Map<AvailabilityZone, Region> map) {
      Set<Location> locations = Sets.newHashSet();
      for (AvailabilityZone zone : map.keySet()) {
         locations.add(new LocationImpl(LocationScope.ZONE, zone.toString(), zone.toString(), map
                  .get(zone).toString(), true));
      }
      for (Region region : map.values()) {
         locations.add(new LocationImpl(LocationScope.REGION, region.toString(), region.toString(),
                  null, true));
      }
      return Maps.uniqueIndex(locations, new Function<Location, String>() {
         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
   }

   @Provides
   @Singleton
   Location getDefaultLocation(@EC2 Region region, Map<String, ? extends Location> map) {
      return map.get(region.toString());
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
   @Named(PROPERTY_EC2_AMI_OWNERS)
   String[] amiOwners(@Named(PROPERTY_EC2_AMI_OWNERS) String amiOwners) {
      return Iterables.toArray(Splitter.on('.').split(amiOwners), String.class);
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImages(final EC2Client sync,
            Map<Region, URI> regionMap, LogHolder holder,
            Function<ComputeMetadata, String> indexer,
            @Named(PROPERTY_EC2_AMI_OWNERS) String[] amiOwners) throws InterruptedException,
            ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");

      for (final Region region : regionMap.keySet()) {
         for (final org.jclouds.aws.ec2.domain.Image from : sync.getAMIServices()
                  .describeImagesInRegion(region, ownedBy(amiOwners))) {
            OsFamily os = null;
            String osDescription = from.getImageLocation();
            String version = "";

            Matcher matcher = ALESTIC_PATTERN.matcher(from.getImageLocation());
            if (matcher.find()) {
               try {
                  os = OsFamily.fromValue(matcher.group(1));
                  matcher.group(2);// TODO no field for os version
                  version = matcher.group(3);
               } catch (IllegalArgumentException e) {
                  holder.logger.debug("<< didn't match os(%s)", matcher.group(1));
               }
            }
            images
                     .add(new ImageImpl(
                              from.getId(),
                              from.getName(),
                              region.toString(),
                              null,
                              ImmutableMap.<String, String> of(),
                              from.getDescription(),
                              version,
                              os,
                              osDescription,
                              from.getArchitecture() == org.jclouds.aws.ec2.domain.Image.Architecture.I386 ? Architecture.X86_32
                                       : Architecture.X86_64));
         }
      }
      holder.logger.debug("<< images(%d)", images.size());
      return Maps.uniqueIndex(images, indexer);
   }
}
