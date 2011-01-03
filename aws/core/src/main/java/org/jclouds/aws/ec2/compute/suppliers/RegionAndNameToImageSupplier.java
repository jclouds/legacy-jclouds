/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute.suppliers;

/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.ownedBy;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_CC_AMIs;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.functions.ImageParser;
import org.jclouds.aws.ec2.compute.strategy.DescribeImagesParallel;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionAndNameToImageSupplier implements Supplier<Map<RegionAndName, ? extends Image>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Set<String> regions;
   private final DescribeImagesParallel describer;
   private final String[] ccAmis;
   private final String[] amiOwners;
   private final ImageParser parser;
   private final Map<RegionAndName, Image> images;

   @Inject
   RegionAndNameToImageSupplier(@Region Set<String> regions, DescribeImagesParallel describer,
         @Named(PROPERTY_EC2_CC_AMIs) String[] ccAmis, @Named(PROPERTY_EC2_AMI_OWNERS) final String[] amiOwners,
         final ImageParser parser, final Map<RegionAndName, Image> images) {
      this.regions = regions;
      this.describer = describer;
      this.ccAmis = ccAmis;
      this.amiOwners = amiOwners;
      this.parser = parser;
      this.images = images;
   }

   @Override
   public Map<RegionAndName, ? extends Image> get() {
      if (amiOwners.length == 0) {
         logger.debug(">> no owners specified, skipping image parsing");
      } else {
         logger.debug(">> providing images");

         Iterable<Entry<String, DescribeImagesOptions>> queries = concat(
               getDescribeQueriesForOwnersInRegions(regions, amiOwners).entrySet(), ccAmisToDescribeQueries(ccAmis)
                     .entrySet());

         Iterable<? extends Image> parsedImages = filter(transform(describer.apply(queries), parser),
               Predicates.notNull());

         images.putAll(uniqueIndex(parsedImages, new Function<Image, RegionAndName>() {

            @Override
            public RegionAndName apply(Image from) {
               return new RegionAndName(from.getLocation().getId(), from.getProviderId());
            }

         }));

         logger.debug("<< images(%d)", images.size());
      }
      return images;
   }

   private static Map<String, DescribeImagesOptions> ccAmisToDescribeQueries(String[] ccAmis) {
      Map<String, DescribeImagesOptions> queries = newLinkedHashMap();
      for (String from : ccAmis) {
         queries.put(from.split("/")[0], imageIds(from.split("/")[1]));
      }
      return queries;
   }

   private static Map<String, DescribeImagesOptions> getDescribeQueriesForOwnersInRegions(Set<String> regions,
         final String[] amiOwners) {
      final DescribeImagesOptions options = getOptionsForOwners(amiOwners);
      Builder<String, DescribeImagesOptions> builder = ImmutableMap.<String, DescribeImagesOptions> builder();
      for (String region : regions)
         builder.put(region, options);
      return builder.build();
   }

   private static DescribeImagesOptions getOptionsForOwners(final String[] amiOwners) {
      final DescribeImagesOptions options;
      if (amiOwners.length == 1 && amiOwners[0].equals("*"))
         options = new DescribeImagesOptions();
      else
         options = ownedBy(amiOwners);
      return options;
   }
}