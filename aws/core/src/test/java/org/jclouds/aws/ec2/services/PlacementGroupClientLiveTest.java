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
import static org.jclouds.io.Payloads.newStringPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.PlacementGroup.State;
import org.jclouds.aws.ec2.predicates.PlacementGroupAvailable;
import org.jclouds.aws.ec2.predicates.PlacementGroupDeleted;
import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code PlacementGroupClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.PlacementGroupClientLiveTest")
public class PlacementGroupClientLiveTest {

   private EC2Client client;
   private ComputeServiceContext context;
   private RetryablePredicate<PlacementGroup> availableTester;
   private RetryablePredicate<PlacementGroup> deletedTester;
   private PlacementGroup group;
   private Map<String, String> keyPair;
   protected String provider = "ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
               + ".credential");
      endpoint = checkNotNull(System.getProperty("test." + provider + ".endpoint"), "test." + provider + ".endpoint");
      apiversion = checkNotNull(System.getProperty("test." + provider + ".apiversion"), "test." + provider
               + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      overrides.setProperty(provider + ".endpoint", endpoint);
      overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws FileNotFoundException, IOException {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider, ImmutableSet
               .<Module> of(new Log4JLoggingModule()), overrides);
      keyPair = BaseComputeServiceLiveTest.setupKeyPair();

      client = EC2Client.class.cast(context.getProviderSpecificContext().getApi());

      availableTester = new RetryablePredicate<PlacementGroup>(new PlacementGroupAvailable(client), 60, 1,
               TimeUnit.SECONDS);

      deletedTester = new RetryablePredicate<PlacementGroup>(new PlacementGroupDeleted(client), 60, 1, TimeUnit.SECONDS);
   }

   @Test
   void testDescribe() {
      for (String region : Lists.newArrayList(Region.US_EAST_1)) {
         SortedSet<PlacementGroup> allResults = Sets.newTreeSet(client.getPlacementGroupServices()
                  .describePlacementGroupsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            PlacementGroup group = allResults.last();
            SortedSet<PlacementGroup> result = Sets.newTreeSet(client.getPlacementGroupServices()
                     .describePlacementGroupsInRegion(region, group.getName()));
            assertNotNull(result);
            PlacementGroup compare = result.last();
            assertEquals(compare, group);
         }
      }

      for (String region : Lists.newArrayList(Region.EU_WEST_1, Region.US_WEST_1, Region.AP_SOUTHEAST_1)) {
         try {
            client.getPlacementGroupServices().describePlacementGroupsInRegion(region);
            assert false : "should be unsupported";
         } catch (AWSResponseException e) {
            assertEquals(e.getError().getCode(), "UnsupportedOperation");
         }
      }
   }

   @Test
   void testCreatePlacementGroup() {
      String groupName = PREFIX + "1";
      client.getPlacementGroupServices().deletePlacementGroupInRegion(null, groupName);
      client.getPlacementGroupServices().createPlacementGroupInRegion(null, groupName);

      verifyPlacementGroup(groupName);
   }

   private void verifyPlacementGroup(String groupName) {
      assert availableTester.apply(new PlacementGroup(Region.US_EAST_1, groupName, "cluster", State.PENDING)) : group;
      Set<PlacementGroup> oneResult = client.getPlacementGroupServices().describePlacementGroupsInRegion(null,
               groupName);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 1);
      group = oneResult.iterator().next();
      assertEquals(group.getName(), groupName);
      assertEquals(group.getStrategy(), "cluster");
      assert availableTester.apply(group) : group;
   }

   public void testStartCCInstance() throws Exception {
      Set<? extends Hardware> sizes = context.getComputeService().listHardwareProfiles();
      assert Iterables.any(sizes, new Predicate<Hardware>() {

         @Override
         public boolean apply(Hardware arg0) {
            return arg0.getProviderId().equals(InstanceType.CC1_4XLARGE);
         }

      }) : sizes;
      Set<? extends Image> images = context.getComputeService().listImages();
      assert Iterables.any(images, new Predicate<Image>() {

         @Override
         public boolean apply(Image arg0) {
            return arg0.getId().equals("us-east-1/ami-7ea24a17");
         }

      }) : images;

      Template template = context.getComputeService().templateBuilder().fastest().build();
      assert template != null : "The returned template was null, but it should have a value.";
      assertEquals(template.getHardware().getProviderId(), InstanceType.CC1_4XLARGE);
      assertEquals(template.getImage().getId(), "us-east-1/ami-7ea24a17");

      template.getOptions().installPrivateKey(keyPair.get("private")).authorizePublicKey(keyPair.get("public"))
               .runScript(
                        newStringPayload(BaseComputeServiceLiveTest.buildScript(template.getImage()
                                 .getOperatingSystem())));

      String tag = PREFIX + "cccluster";
      context.getComputeService().destroyNodesMatching(NodePredicates.withTag(tag));

      try {
         Set<? extends NodeMetadata> nodes = context.getComputeService().runNodesWithTag(tag, 1, template);
         NodeMetadata node = Iterables.getOnlyElement(nodes);

         Iterables.getOnlyElement(Iterables.getOnlyElement(client.getInstanceServices().describeInstancesInRegion(null,
                  node.getProviderId())));

      } catch (RunNodesException e) {
         System.err.println(e.getNodeErrors().keySet());
         Throwables.propagate(e);
      } finally {
         context.getComputeService().destroyNodesMatching(NodePredicates.withTag(tag));
      }
   }

   public static final String PREFIX = System.getProperty("user.name") + "ec2";

   @AfterTest
   public void shutdown() {
      if (group != null) {
         client.getPlacementGroupServices().deletePlacementGroupInRegion(group.getRegion(), group.getName());
         assert deletedTester.apply(group) : group;
      }
      context.close();
   }
}
