/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions.Builder.from;
import static org.jclouds.aws.ec2.options.RequestSpotInstancesOptions.Builder.launchGroup;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.domain.Spot;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.predicates.SpotInstanceRequestActive;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SpotInstanceClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class SpotInstanceClientLiveTest {

   private static final int SPOT_DELAY_SECONDS = 300;
   private AWSEC2Client client;
   private ComputeServiceContext context;
   private RetryablePredicate<SpotInstanceRequest> activeTester;
   private Set<SpotInstanceRequest> requests;
   protected String provider = "aws-ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   private AWSRunningInstance instance;
   private long start;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws FileNotFoundException, IOException {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider, ImmutableSet.<Module> of(
               new Log4JLoggingModule(), new JschSshClientModule()), overrides);

      client = AWSEC2Client.class.cast(context.getProviderSpecificContext().getApi());
      activeTester = new RetryablePredicate<SpotInstanceRequest>(new SpotInstanceRequestActive(client),
               SPOT_DELAY_SECONDS, 1, 1, TimeUnit.SECONDS);
   }

   @Test
   void testDescribeSpotRequestsInRegion() {
      for (String region : Region.DEFAULT_REGIONS) {
         SortedSet<SpotInstanceRequest> allResults = ImmutableSortedSet.copyOf(client.getSpotInstanceServices()
                  .describeSpotInstanceRequestsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            SpotInstanceRequest request = allResults.last();
            SortedSet<SpotInstanceRequest> result = ImmutableSortedSet.copyOf(client.getSpotInstanceServices()
                     .describeSpotInstanceRequestsInRegion(region, request.getId()));
            assertNotNull(result);
            SpotInstanceRequest compare = result.last();
            assertEquals(compare, request);
         }
      }

   }

   @Test
   void testDescribeSpotPriceHistoryInRegion() {
      for (final String region : Region.DEFAULT_REGIONS) {
         Set<Spot> spots = client.getSpotInstanceServices().describeSpotPriceHistoryInRegion(region, from(new Date()));
         assertNotNull(spots);
         assert spots.size() > 0;
         for (Spot spot : spots) {
            assert spot.getSpotPrice() > 0 : spots;
            assertEquals(spot.getRegion(), region);
            assert in(ImmutableSet.of("Linux/UNIX", "SUSE Linux", "Windows")).apply(spot.getProductDescription()) : spot;
            assert in(
                     ImmutableSet.of("c1.medium", "c1.xlarge", "cc1.4xlarge", "cg1.4xlarge", "m1.large", "m1.small",
                              "m1.xlarge", "m2.2xlarge", "m2.4xlarge", "m2.xlarge", "t1.micro")).apply(
                     spot.getInstanceType()) : spot;

         }
      }

   }

   @Test(enabled = true)
   void testCreateSpotInstance() {
      String launchGroup = PREFIX + "1";
      for (SpotInstanceRequest request : client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(
               "us-west-1"))
         if (launchGroup.equals(request.getLaunchGroup()))
            client.getSpotInstanceServices().cancelSpotInstanceRequestsInRegion("us-west-1", request.getId());
      start = System.currentTimeMillis();

      requests = client.getSpotInstanceServices().requestSpotInstancesInRegion(
               "us-west-1",
               0.03f,
               1,
               LaunchSpecification.builder().imageId("ami-595a0a1c").instanceType(InstanceType.T1_MICRO).build(),
               launchGroup(launchGroup).availabilityZoneGroup(launchGroup).validFrom(
                        new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1))).validUntil(
                        new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SPOT_DELAY_SECONDS))));
      assertNotNull(requests);

      for (SpotInstanceRequest request : requests)
         verifySpotInstance(request);
   }

   private void verifySpotInstance(SpotInstanceRequest request) {
      SpotInstanceRequest spot = refresh(request);
      assertNotNull(spot);
      assertEquals(spot, request);
      assert activeTester.apply(request) : refresh(request);
      System.out.println(System.currentTimeMillis() - start);
      spot = refresh(request);
      assert spot.getInstanceId() != null : spot;
      instance = getOnlyElement(getOnlyElement(client.getInstanceServices().describeInstancesInRegion("us-west-1",
               spot.getInstanceId())));
      assertEquals(instance.getSpotInstanceRequestId(), spot.getId());
   }

   public SpotInstanceRequest refresh(SpotInstanceRequest request) {
      return getOnlyElement(client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion("us-west-1",
               request.getId()));
   }

   public static final String PREFIX = System.getProperty("user.name") + "ec2";

   @AfterTest
   public void shutdown() {
      if (requests != null) {
         for (SpotInstanceRequest request : requests)
            client.getSpotInstanceServices().cancelSpotInstanceRequestsInRegion(request.getRegion(), request.getId());
         // assert deletedTester.apply(request) : request;
      }
      if (instance != null) {
         client.getInstanceServices().terminateInstancesInRegion("us-west-1", instance.getId());
      }
      context.close();
   }
}
