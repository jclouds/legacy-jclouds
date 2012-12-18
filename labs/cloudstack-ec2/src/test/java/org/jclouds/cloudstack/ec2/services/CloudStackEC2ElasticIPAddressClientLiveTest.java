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
package org.jclouds.cloudstack.ec2.services;

import com.google.common.collect.Sets;
import org.jclouds.cloudstack.ec2.CloudStackEC2ApiMetadata;
import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.jclouds.ec2.services.ElasticIPAddressClientLiveTest;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2ElasticIPAddressClientLiveTest")
public class CloudStackEC2ElasticIPAddressClientLiveTest extends ElasticIPAddressClientLiveTest {
   private CloudStackEC2Client cloudstackEc2Client;
   private ElasticIPAddressClient cloudstackClient;
   private RetryablePredicate<RunningInstance> runningTester;
   private String cloudstackDefaultZone;
   private String imageId;
   private String regionId = "AmazonEC2";
   private RunningInstance instance;
   private String instanceId;
   private String publicIp;

   public CloudStackEC2ElasticIPAddressClientLiveTest() {
      provider = "cloudstack-ec2";
   }

   @Override
   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      initializeContext();
      cloudstackEc2Client = view.unwrap(CloudStackEC2ApiMetadata.CONTEXT_TOKEN).getApi();
      cloudstackClient = cloudstackEc2Client.getElasticIPAddressServices();
      runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(cloudstackEc2Client), 900, 5,
              TimeUnit.SECONDS);
      Set<AvailabilityZoneInfo> allResults = cloudstackEc2Client.getAvailabilityZoneAndRegionServices()
              .describeAvailabilityZonesInRegion(regionId);
      allResults.iterator().next();
      cloudstackDefaultZone = allResults.iterator().next().getZone();
      Set<? extends Image> allImageResults = cloudstackEc2Client.getAMIServices().describeImagesInRegion(regionId);
      assertNotNull(allImageResults);
      assert allImageResults.size() >= 1 : allImageResults.size();
      Iterator<? extends Image> iterator = allImageResults.iterator();
      imageId = iterator.next().getId();
      if (imageId != null) {
         runInstance();
      }
   }

   private void runInstance() {
      Reservation<? extends RunningInstance> runningInstances = cloudstackEc2Client.getInstanceServices()
              .runInstancesInRegion(
                      regionId, cloudstackDefaultZone, imageId, 1, 1);
      instance = getOnlyElement(concat(runningInstances));
      instanceId = instance.getId();
      assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
      instance = (RunningInstance) (getOnlyElement(concat(cloudstackEc2Client.getInstanceServices()
              .describeInstancesInRegion(regionId,
                      instanceId))));
   }

   @Test
   void testDescribeAddresses() {
      SortedSet<PublicIpInstanceIdPair> allResults = Sets.newTreeSet(cloudstackClient.describeAddressesInRegion
              (regionId));
      assertNotNull(allResults);
      if (allResults.size() >= 1) {
         PublicIpInstanceIdPair pair = allResults.last();
         SortedSet<PublicIpInstanceIdPair> result = Sets.newTreeSet(cloudstackClient.describeAddressesInRegion
                 (regionId, pair
                 .getPublicIp()));
         assertNotNull(result);
         PublicIpInstanceIdPair compare = result.last();
         assertEquals(compare, pair);
      }
   }

   @Test
   void testAllocateAddressInRegion() {
      publicIp = cloudstackClient.allocateAddressInRegion(regionId);
   }

   @Test(dependsOnMethods = "testAllocateAddressInRegion")
   void testAssociateAddressInRegion() {
      cloudstackClient.associateAddressInRegion(regionId, publicIp, instanceId);
   }

   @Test(dependsOnMethods = "testAssociateAddressInRegion")
   void testDisassociateAddressInRegion() {
      cloudstackClient.disassociateAddressInRegion(regionId, publicIp);
   }

   @Test(dependsOnMethods = "testDisassociateAddressInRegion")
   void testReleaseAddressInRegion() {
      cloudstackClient.releaseAddressInRegion(regionId, publicIp);
   }

   @Override
   @AfterClass(groups = {"integration", "live"})
   protected void tearDownContext() {
      if (instanceId != null) {
         cloudstackEc2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
      }
      super.tearDownContext();
   }
}