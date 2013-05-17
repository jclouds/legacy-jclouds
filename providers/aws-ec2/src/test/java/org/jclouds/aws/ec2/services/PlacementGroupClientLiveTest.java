/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.ec2.services;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.PlacementGroup.State;
import org.jclouds.aws.ec2.predicates.PlacementGroupAvailable;
import org.jclouds.aws.ec2.predicates.PlacementGroupDeleted;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.ec2.compute.domain.EC2HardwareBuilder;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.inject.Module;

/**
 * Tests behavior of {@code PlacementGroupClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "PlacementGroupClientLiveTest")
public class PlacementGroupClientLiveTest extends BaseComputeServiceContextLiveTest {
   ArrayList<String> supportedRegions = newArrayList(Region.US_EAST_1, Region.US_WEST_2, Region.EU_WEST_1);

   public PlacementGroupClientLiveTest() {
      provider = "aws-ec2";
   }

   private AWSEC2Client client;
   private Predicate<PlacementGroup> availableTester;
   private Predicate<PlacementGroup> deletedTester;
   private PlacementGroup group;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi();
      availableTester = retry(new PlacementGroupAvailable(client), 60, 1, SECONDS);
      deletedTester = retry(new PlacementGroupDeleted(client), 60, 1, SECONDS);
   }

   @Test
   void testDescribe() {
      for (String region : supportedRegions) {
         SortedSet<PlacementGroup> allResults = newTreeSet(client.getPlacementGroupServices()
                  .describePlacementGroupsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            PlacementGroup group = allResults.last();
            SortedSet<PlacementGroup> result = newTreeSet(client.getPlacementGroupServices()
                     .describePlacementGroupsInRegion(region, group.getName()));
            assertNotNull(result);
            PlacementGroup compare = result.last();
            assertEquals(compare, group);
         }
      }

      for (String region : client.getAvailabilityZoneAndRegionServices().describeRegions().keySet()) {
         if (!supportedRegions.contains(region))
            try {
               client.getPlacementGroupServices().describePlacementGroupsInRegion(region);
               fail("should be unsupported for region: " + region);
            } catch (UnsupportedOperationException e) {
            }
      }
   }

   @Test
   void testCreatePlacementGroup() {
      String groupName = PREFIX + "1";
      for (String region : supportedRegions) {

         client.getPlacementGroupServices().deletePlacementGroupInRegion(region, groupName);
         client.getPlacementGroupServices().createPlacementGroupInRegion(region, groupName);

         verifyPlacementGroup(region, groupName);
      }
   }

   private void verifyPlacementGroup(String region, String groupName) {
      assert availableTester.apply(new PlacementGroup(region, groupName, "cluster", State.PENDING)) : group;
      Set<PlacementGroup> oneResult = client.getPlacementGroupServices().describePlacementGroupsInRegion(region,
               groupName);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 1);
      group = oneResult.iterator().next();
      assertEquals(group.getName(), groupName);
      assertEquals(group.getStrategy(), "cluster");
      assert availableTester.apply(group) : group;
   }

   public void testStartCCInstance() throws Exception {

      Template template = view.getComputeService().templateBuilder()
               .fromHardware(EC2HardwareBuilder.cc2_8xlarge().build()).osFamily(OsFamily.AMZN_LINUX).build();
      assert template != null : "The returned template was null, but it should have a value.";
      assertEquals(template.getHardware().getProviderId(), InstanceType.CC2_8XLARGE);
      assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "ebs");
      assertEquals(template.getImage().getUserMetadata().get("virtualizationType"), "hvm");
      assertEquals(template.getImage().getUserMetadata().get("hypervisor"), "xen");

      template.getOptions().runScript(Statements.newStatementList(AdminAccess.standard(), InstallJDK.fromOpenJDK()));

      String group = PREFIX + "cccluster";
      view.getComputeService().destroyNodesMatching(NodePredicates.inGroup(group));
      // TODO make this not lookup an explicit region
      client.getPlacementGroupServices().deletePlacementGroupInRegion(null, "jclouds#" + group + "#us-east-1");

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(group, 1, template);
         NodeMetadata node = getOnlyElement(nodes);

         getOnlyElement(getOnlyElement(client.getInstanceServices().describeInstancesInRegion(null,
                  node.getProviderId())));

      } catch (RunNodesException e) {
         System.err.println(e.getNodeErrors().keySet());
         Throwables.propagate(e);
      } finally {
         view.getComputeService().destroyNodesMatching(NodePredicates.inGroup(group));
      }
   }

   public static final String PREFIX = System.getProperty("user.name") + "ec2";

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      if (group != null) {
         client.getPlacementGroupServices().deletePlacementGroupInRegion(group.getRegion(), group.getName());
         assert deletedTester.apply(group) : group;
      }
      super.tearDownContext();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
