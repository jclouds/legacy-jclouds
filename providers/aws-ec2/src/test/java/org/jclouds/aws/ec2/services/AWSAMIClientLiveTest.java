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
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.filters;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.services.AMIClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code AMIClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class AWSAMIClientLiveTest extends AMIClientLiveTest {

   public AWSAMIClientLiveTest() {
      provider = "aws-ec2";
   }

   public void testDescribeImagesCC() {
      Set<? extends Image> ccResults = client.describeImagesInRegion(Region.US_EAST_1,
            filters(ImmutableMultimap.<String, String> builder()//
                  .put("virtualization-type", "hvm")//
                  .put("architecture", "x86_64")//
                  .putAll("owner-id", ImmutableSet.<String> of("137112412989", "099720109477"))//
                  .put("hypervisor", "xen")//
                  .put("state", "available")//
                  .put("image-type", "machine")//
                  .put("root-device-type", "ebs")//
                  .build()).ownedBy("137112412989", "099720109477"));
      assertNotNull(ccResults);
      assert (ccResults.size() >= 34) : ccResults;
   }
}
