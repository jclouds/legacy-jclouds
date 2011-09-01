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
package org.jclouds.ec2.options;

import java.util.Arrays;
import java.util.Set;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the DescribeRegions operation. <h2>
 * Usage</h2> The recommended way to instantiate a DescribeRegionsOptions object is to statically
 * import DescribeRegionsOptions.Builder.* and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.ec2.options.DescribeRegionsOptions.Builder.*
 * <p/>
 * EC2Client connection = // get connection
 * Future<Set<ImageMetadata>> images = connection.getRegionsAndRegionsServices().describeRegions(regions("us-east-1a", "us-east-1b"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-DescribeRegions.html"
 *      />
 */
public class DescribeRegionsOptions extends BaseEC2RequestOptions {

   /**
    * Name of a Region.
    */
   public DescribeRegionsOptions regions(String... regions) {
      String[] regionStrings = Arrays.copyOf(regions, regions.length, String[].class);
      indexFormValuesWithPrefix("RegionName", regionStrings);
      return this;
   }

   public Set<String> getZones() {
      return getFormValuesWithKeysPrefixedBy("RegionName.");
   }

   public static class Builder {

      /**
       * @see DescribeRegionsOptions#regions(String[] )
       */
      public static DescribeRegionsOptions regions(String... regions) {
         DescribeRegionsOptions options = new DescribeRegionsOptions();
         return options.regions(regions);
      }

   }
}
