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
import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMIs;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.strategy.DescribeImagesParallel;
import org.jclouds.ec2.compute.suppliers.RegionAndNameToImageSupplier;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.location.Region;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSRegionAndNameToImageSupplier extends RegionAndNameToImageSupplier {

   private final String[] ccAmis;

   @Inject
   AWSRegionAndNameToImageSupplier(@Region Set<String> regions, DescribeImagesParallel describer,
            @Named(PROPERTY_EC2_CC_AMIs) String[] ccAmis, @Named(PROPERTY_EC2_AMI_OWNERS) String[] amiOwners,
            EC2ImageParser parser, Map<RegionAndName, Image> images) {
      super(regions, describer, amiOwners, parser, images);
      this.ccAmis = ccAmis;
   }

   public Iterable<Entry<String, DescribeImagesOptions>> getDescribeQueriesForOwnersInRegions(Set<String> regions,
            String[] amiOwners) {
      return concat(super.getDescribeQueriesForOwnersInRegions(regions, amiOwners), ccAmisToDescribeQueries(ccAmis)
               .entrySet());
   }

   static Map<String, DescribeImagesOptions> ccAmisToDescribeQueries(String[] ccAmis) {
      Map<String, DescribeImagesOptions> queries = newLinkedHashMap();
      for (String from : ccAmis) {
         queries.put(from.split("/")[0], imageIds(from.split("/")[1]));
      }
      return queries;
   }

}
