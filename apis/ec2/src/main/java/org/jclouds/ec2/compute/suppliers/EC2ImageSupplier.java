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
package org.jclouds.ec2.compute.suppliers;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.ownedBy;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.functions.ImagesToRegionAndIdMap;
import org.jclouds.ec2.compute.strategy.DescribeImagesParallel;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ImageSupplier implements Supplier<Set<? extends Image>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<Set<String>> regions;
   private final DescribeImagesParallel describer;
   private final String[] amiOwners;
   private final EC2ImageParser parser;
   private final Supplier<LoadingCache<RegionAndName, ? extends Image>> cache;

   @Inject
   protected EC2ImageSupplier(@Region Supplier<Set<String>> regions, DescribeImagesParallel describer,
         @Named(PROPERTY_EC2_AMI_OWNERS) String[] amiOwners, Supplier<LoadingCache<RegionAndName, ? extends Image>> cache,
         EC2ImageParser parser) {
      this.regions = regions;
      this.describer = describer;
      this.amiOwners = amiOwners;
      this.cache = cache;
      this.parser = parser;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public Set<? extends Image> get() {
      if (amiOwners.length == 0) {
         logger.debug(">> no owners specified, skipping image parsing");
         return Collections.emptySet();
      
      } else {
         logger.debug(">> providing images");

         Iterable<Entry<String, DescribeImagesOptions>> queries = getDescribeQueriesForOwnersInRegions(regions.get(),
                  amiOwners);

         Iterable<? extends Image> parsedImages = ImmutableSet.copyOf(filter(transform(describer.apply(queries), parser), Predicates
                  .notNull()));

         Map<RegionAndName, ? extends Image> imageMap = ImagesToRegionAndIdMap.imagesToMap(parsedImages);
         cache.get().invalidateAll();
         cache.get().asMap().putAll((Map)imageMap);
         logger.debug("<< images(%d)", imageMap.size());
         
         return Sets.newLinkedHashSet(imageMap.values());
      }
   }

   public Iterable<Entry<String, DescribeImagesOptions>> getDescribeQueriesForOwnersInRegions(Set<String> regions,
         String[] amiOwners) {
      DescribeImagesOptions options = getOptionsForOwners(amiOwners);
      Builder<String, DescribeImagesOptions> builder = ImmutableMap.builder();
      for (String region : regions)
         builder.put(region, options);
      return builder.build().entrySet();
   }

   public DescribeImagesOptions getOptionsForOwners(String... amiOwners) {
      DescribeImagesOptions options;
      if (amiOwners.length == 1 && amiOwners[0].equals("*"))
         options = new DescribeImagesOptions();
      else
         options = ownedBy(amiOwners);
      return options;
   }
}