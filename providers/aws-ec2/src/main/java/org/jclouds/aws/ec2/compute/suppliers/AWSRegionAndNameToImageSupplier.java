/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.aws.ec2.compute.suppliers;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_REGIONS;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.compute.config.ClusterCompute;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Futures;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSRegionAndNameToImageSupplier implements Supplier<Cache<RegionAndName, ? extends Image>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CacheLoader<RegionAndName, Image> regionAndIdToImage;
   private final Set<String> clusterComputeIds;
   private final CallForImages.Factory factory;
   private final ExecutorService executor;

   private final Iterable<String> regions;
   private final String amiQuery;
   private final Iterable<String> clusterRegions;
   private final String ccAmiQuery;

   @Inject
   protected AWSRegionAndNameToImageSupplier(@Region Set<String> regions,
            @Named(PROPERTY_EC2_AMI_QUERY) String amiQuery, @Named(PROPERTY_EC2_CC_REGIONS) String clusterRegions,
            @Named(PROPERTY_EC2_CC_AMI_QUERY) String ccAmiQuery, CacheLoader<RegionAndName, Image> regionAndIdToImage,
            CallForImages.Factory factory, @ClusterCompute Set<String> clusterComputeIds,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.factory = factory;
      this.regions = regions;
      this.amiQuery = amiQuery;
      this.clusterRegions = Splitter.on(',').split(clusterRegions);
      this.ccAmiQuery = ccAmiQuery;
      this.regionAndIdToImage = regionAndIdToImage;
      this.clusterComputeIds = clusterComputeIds;
      this.executor = executor;
   }

   @Override
   public Cache<RegionAndName, ? extends Image> get() {
      Future<Iterable<Image>> normalImages = images(regions, amiQuery, PROPERTY_EC2_AMI_QUERY);
      ImmutableSet<Image> clusterImages;
      try {
         clusterImages = ImmutableSet.copyOf(images(clusterRegions, ccAmiQuery, PROPERTY_EC2_CC_AMI_QUERY).get());
      } catch (Exception e) {
         logger.warn(e, "Error parsing images in query %s", ccAmiQuery);
         Throwables.propagate(e);
         return null;
      }
      Iterables.addAll(clusterComputeIds, transform(clusterImages, new Function<Image, String>() {

         @Override
         public String apply(Image arg0) {
            return arg0.getId();
         }
      }));
      Iterable<? extends Image> parsedImages;
      try {
         parsedImages = ImmutableSet.copyOf(concat(clusterImages, normalImages.get()));
      } catch (Exception e) {
         logger.warn(e, "Error parsing images in query %s", amiQuery);
         Throwables.propagate(e);
         return null;
      }
      Cache<RegionAndName, Image> cache = CacheBuilder.newBuilder().build(regionAndIdToImage);

      cache.asMap().putAll(uniqueIndex(parsedImages, new Function<Image, RegionAndName>() {

         @Override
         public RegionAndName apply(Image from) {
            return new RegionAndName(from.getLocation().getId(), from.getProviderId());
         }

      }));
      logger.debug("<< images(%d)", cache.asMap().size());
      return cache;
   }

   private Future<Iterable<Image>> images(Iterable<String> regions, String query, String tag) {
      if (query.equals("")) {
         logger.debug(">> no %s specified, skipping image parsing", tag);
         return Futures.<Iterable<Image>> immediateFuture(ImmutableSet.<Image> of());
      } else {
         return executor.submit(factory.parseImagesFromRegionsUsingFilter(regions, QueryStringToMultimap.INSTANCE
                  .apply(query)));
      }
   }

   public static enum QueryStringToMultimap implements Function<String, Multimap<String, String>> {
      INSTANCE;
      @Override
      public Multimap<String, String> apply(String arg0) {
         ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
         for (String pair : Splitter.on(';').split(arg0)) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 1)
               builder.putAll(keyValue[0], ImmutableSet.<String> of());
            else
               builder.putAll(keyValue[0], Splitter.on(',').split(keyValue[1]));
         }
         return builder.build();
      }
   }
}
