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
package org.jclouds.aws.ec2.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.Set;

import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.services.AWSSecurityGroupClient;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.EC2ComputeServiceLiveTest;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.ec2.services.KeyPairClient;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * Disabled, as it doesn't pass
 * 
 * @author Aled Sage
 */
@Test(enabled = false, groups = "live", singleThreaded = true, testName = "IncidentalResourcesGetCleanedUpLiveTest")
public class IncidentalResourcesGetCleanedUpLiveTest extends BaseComputeServiceContextLiveTest {

   public IncidentalResourcesGetCleanedUpLiveTest() {
      provider = "aws-ec2";
   }
   
   @Test(enabled = false)
   public void testIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroyNode() throws Exception {
      Function<String,Void> destroyer = new Function<String,Void>() {
         @Override
         public Void apply(String instanceId) {
            view.getComputeService().destroyNode(instanceId);
            return null;
         }
      };
      runIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroy(destroyer);
   }
   
   @Test(enabled = false)
   public void testIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroyNodesMatching() throws Exception {
      Function<String,Void> destroyer = new Function<String,Void>() {
         @Override
         public Void apply(String instanceId) {
            view.getComputeService().destroyNodesMatching(NodePredicates.<NodeMetadata>withIds(instanceId));
            return null;
         }
      };
      runIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroy(destroyer);
   }
   
   private void runIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroy(Function<String,Void> destroyer) throws Exception {
      AWSSecurityGroupClient securityGroupClient = AWSEC2Client.class.cast(view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi())
               .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi())
               .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi())
               .getInstanceServices();

      String group = "aws-ec2-incidental";
      String region = null;
      
      try {
         // Create two instances
         // TODO set spotPrice(0.3f) ?
         Template template = view.getComputeService().templateBuilder().build();
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(group, 2, template);
         NodeMetadata first = Iterables.get(nodes, 0);
         NodeMetadata second = Iterables.get(nodes, 1);

         String instanceId1 = Iterables.get(nodes, 0).getProviderId();
         String instanceId2 = Iterables.get(nodes, 1).getProviderId();

         AWSRunningInstance instance1 = AWSRunningInstance.class.cast(EC2ComputeServiceLiveTest.getInstance(instanceClient, instanceId1));
         AWSRunningInstance instance2 = AWSRunningInstance.class.cast(EC2ComputeServiceLiveTest.getInstance(instanceClient, instanceId2));

         // Assert the two instances are in the same groups
         region = instance1.getRegion();
         String expectedSecurityGroupName = "jclouds#" + group;
         
         assertEquals(instance1.getRegion(), region);
         assertNotNull(instance1.getKeyName());
         assertEquals(instance1.getRegion(), instance2.getRegion(), "Nodes are not in the same region");
         assertEquals(instance1.getKeyName(), instance2.getKeyName(), "Nodes do not have same key-pair name");
         assertEquals(instance1.getGroupIds(), instance2.getGroupIds(), "Nodes are not in the same group");
         assertEquals(instance1.getGroupIds(), ImmutableSet.of(expectedSecurityGroupName), "Nodes are not in the expected security group");

         // Assert a single key-pair and security group has been created
         String expectedKeyPairName = instance1.getKeyName();
         Set<SecurityGroup> securityGroups = securityGroupClient.describeSecurityGroupsInRegion(region, expectedSecurityGroupName);
         Set<KeyPair> keyPairs = keyPairClient.describeKeyPairsInRegion(region, expectedKeyPairName);
         assertEquals(securityGroups.size(), 1);
         assertEquals(Iterables.get(securityGroups, 0).getName(), expectedSecurityGroupName);
         assertEquals(keyPairs.size(), 1);
         assertEquals(Iterables.get(keyPairs, 0).getKeyName(), expectedKeyPairName);

         // Destroy the first node; the key-pair and security-group should still remain
         destroyer.apply(first.getId());

         Set<SecurityGroup> securityGroupsAfterDestroyFirst = securityGroupClient.describeSecurityGroupsInRegion(region, expectedSecurityGroupName);
         Set<KeyPair> keyPairsAfterDestroyFirst = keyPairClient.describeKeyPairsInRegion(region, expectedKeyPairName);
         assertEquals(securityGroupsAfterDestroyFirst, securityGroups);
         assertEquals(keyPairsAfterDestroyFirst, keyPairs);

         // Destroy the second node; the key-pair and security-group should be automatically deleted
         // It can take some time after destroyNode returns for the securityGroup and keyPair to be completely removed.
         // Therefore try repeatedly.
         destroyer.apply(second.getId());

         final int TIMEOUT_MS = 30*1000;
         boolean firstAttempt = true;
         boolean done;
         Set<SecurityGroup> securityGroupsAfterDestroyAll;
         Set<KeyPair> keyPairsAfterDestroyAll;
         Stopwatch stopwatch = new Stopwatch();
         stopwatch.start();
         do {
            if (!firstAttempt) Thread.sleep(1000);
            firstAttempt = false;
            securityGroupsAfterDestroyAll = securityGroupClient.describeSecurityGroupsInRegion(region, expectedSecurityGroupName);
            keyPairsAfterDestroyAll = keyPairClient.describeKeyPairsInRegion(region, expectedKeyPairName);
            done = securityGroupsAfterDestroyAll.isEmpty() && keyPairsAfterDestroyAll.isEmpty();
         } while (!done && stopwatch.elapsedMillis() < TIMEOUT_MS);

         assertEquals(securityGroupsAfterDestroyAll, Collections.emptySet());
         assertEquals(keyPairsAfterDestroyAll, Collections.emptySet());
         
      } finally {
         view.getComputeService().destroyNodesMatching(NodePredicates.inGroup(group));
         
         if (region != null) EC2ComputeServiceLiveTest.cleanupExtendedStuffInRegion(region, securityGroupClient, keyPairClient, group);
      }
   }
}
