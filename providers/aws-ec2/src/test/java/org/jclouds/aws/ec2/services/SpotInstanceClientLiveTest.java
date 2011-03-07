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

package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.SortedSet;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SpotInstanceClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class SpotInstanceClientLiveTest {

   private AWSEC2Client client;
   private ComputeServiceContext context;
   private RetryablePredicate<SpotInstanceRequest> availableTester;
   private RetryablePredicate<SpotInstanceRequest> deletedTester;
   private SpotInstanceRequest request;
   protected String provider = "aws-ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

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
      context = new ComputeServiceContextFactory().createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new JschSshClientModule()), overrides);

      client = AWSEC2Client.class.cast(context.getProviderSpecificContext().getApi());
      // TODO
      // availableTester = new RetryablePredicate<SpotInstanceRequest>(new
      // SpotInstanceAvailable(client), 60, 1,
      // TimeUnit.SECONDS);
      //
      // deletedTester = new RetryablePredicate<SpotInstanceRequest>(new
      // SpotInstanceDeleted(client), 60, 1, TimeUnit.SECONDS);
   }

   @Test
   void testDescribe() {
      for (String region : Region.DEFAULT_REGIONS) {
         String string = client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(region);
         assertNotNull(string);// TODO
         SortedSet<SpotInstanceRequest> allResults = ImmutableSortedSet.<SpotInstanceRequest>of(SpotInstanceRequest.builder().id("foo")
               .build());
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            SpotInstanceRequest request = allResults.last();
            string = client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(region, request.getId());
            assertNotNull(string);// TODO
            SortedSet<SpotInstanceRequest> result = ImmutableSortedSet.of(SpotInstanceRequest.builder().id("foo")
                  .build());
            assertNotNull(result);
            SpotInstanceRequest compare = result.last();
            assertEquals(compare, request);
         }
      }

      for (String region : client.getAvailabilityZoneAndRegionServices().describeRegions().keySet()) {
         if (!region.equals(Region.US_EAST_1))
            try {
               client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(region);
               assert false : "should be unsupported";
            } catch (UnsupportedOperationException e) {
            }
      }
   }

   @Test
   void testCreateSpotInstance() {
      String launchGroup = PREFIX + "1";
      for (SpotInstanceRequest request : ImmutableSet.<SpotInstanceRequest> of())
         // client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(null);
         if (request.getLaunchGroup().equals(launchGroup))
            client.getSpotInstanceServices().cancelSpotInstanceRequestsInRegion(null, request.getId());
      String string = client.getSpotInstanceServices().requestSpotInstancesInRegion(
            null,
            null,
            "TODO",
            1,
            0.01f,
            RequestSpotInstancesOptions.Builder.launchSpecification(AWSRunInstancesOptions.Builder.asType("m1.small"))
                  .launchGroup(PREFIX));
      assertNotNull(string);

      verifySpotInstance(request);
   }

   private void verifySpotInstance(SpotInstanceRequest request) {
      assert availableTester.apply(request) : request;
      String string = client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(null, request.getId());
      assertNotNull(string);// TODO
      SpotInstanceRequest oneResult = Iterables.getOnlyElement(ImmutableSet.of(SpotInstanceRequest.builder().id("foo")
            .build()));
      assertNotNull(oneResult);
      assertEquals(oneResult, request);
      // TODO: more
   }

   public static final String PREFIX = System.getProperty("user.name") + "ec2";

   @AfterTest
   public void shutdown() {
      if (request != null) {
         client.getSpotInstanceServices().cancelSpotInstanceRequestsInRegion(request.getRegion(), request.getId());
         assert deletedTester.apply(request) : request;
      }
      context.close();
   }
}
