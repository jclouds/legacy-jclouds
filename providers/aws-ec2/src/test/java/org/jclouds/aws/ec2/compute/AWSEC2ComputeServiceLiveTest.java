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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.ec2.services.AWSSecurityGroupClient;
import org.jclouds.cloudwatch.CloudWatchAsyncClient;
import org.jclouds.cloudwatch.CloudWatchClient;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.EC2ComputeServiceLiveTest;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.ec2.util.IpPermissions;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.scriptbuilder.domain.Statements;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "AWSEC2ComputeServiceLiveTest")
public class AWSEC2ComputeServiceLiveTest extends EC2ComputeServiceLiveTest {

   public AWSEC2ComputeServiceLiveTest() {
      provider = "aws-ec2";
      group = "ec2";
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testCompareSizes")
   public void testExtendedOptionsAndLogin() throws Exception {
      AWSSecurityGroupClient securityGroupClient = AWSEC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getInstanceServices();

      String group = this.group + "o";

      TemplateOptions options = client.templateOptions();

      Date before = new Date();

      options.as(AWSEC2TemplateOptions.class).enableMonitoring();
      options.as(AWSEC2TemplateOptions.class).spotPrice(0.3f);

      String startedId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, group);

         // create a security group that allows ssh in so that our scripts later
         // will work
         String groupId = securityGroupClient.createSecurityGroupInRegionAndReturnId(null, group, group);
         
         securityGroupClient.authorizeSecurityGroupIngressInRegion(null, groupId,
               IpPermissions.permit(IpProtocol.TCP).port(22));

         options.as(AWSEC2TemplateOptions.class).securityGroupIds(groupId);

         // create a keypair to pass in as well
         KeyPair result = keyPairClient.createKeyPairInRegion(null, group);
         options.as(AWSEC2TemplateOptions.class).keyPair(result.getKeyName());
         
         // pass in the private key, so that we can run a script with it
         assert result.getKeyMaterial() != null : result;
         options.overrideLoginCredentialWith(result.getKeyMaterial());
         
         // an arbitrary command to run
         options.runScript(Statements.exec("find /usr"));
         
         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, options);
         NodeMetadata first = Iterables.get(nodes, 0);
         assert first.getCredentials() != null : first;
         assert first.getCredentials().identity != null : first;

         startedId = Iterables.getOnlyElement(nodes).getProviderId();

         AWSRunningInstance instance = AWSRunningInstance.class.cast(getInstance(instanceClient, startedId));

         assertEquals(instance.getKeyName(), group);
         assertEquals(instance.getMonitoringState(), MonitoringState.ENABLED);


         RestContext<CloudWatchClient, CloudWatchAsyncClient> monitoringContext = new RestContextFactory()
                  .createContext("aws-cloudwatch", identity, credential, ImmutableSet.<Module> of(new Log4JLoggingModule()));

         try {
            Set<Datapoint> datapoints = monitoringContext.getApi().getMetricStatisticsInRegion(instance.getRegion(),
                     "CPUUtilization", before, new Date(), 60, "Average");
            assert datapoints != null;
         } finally {
            monitoringContext.close();
         }

         // make sure we made our dummy group and also let in the user's group
         assertEquals(Sets.newTreeSet(instance.getGroupIds()), ImmutableSortedSet.<String> of("jclouds#" + group + "#"
                  + instance.getRegion(), group));

         // make sure our dummy group has no rules
         SecurityGroup secgroup = Iterables.getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(null,
                  "jclouds#" + group + "#" + instance.getRegion()));
         assert secgroup.getIpPermissions().size() == 0 : secgroup;

         // try to run a script with the original keyPair
         runScriptWithCreds(group, first.getOperatingSystem(), new Credentials(first.getCredentials().identity, result
                  .getKeyMaterial()));

      } finally {
         client.destroyNodesMatching(NodePredicates.inGroup(group));
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(keyPairClient.describeKeyPairsInRegion(null, group).size(), 1);
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(null, group).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, group);
      }

   }

   @Test(enabled = false, dependsOnMethods = "testCompareSizes")
   public void testSubnetId() throws Exception {

      String subnetId = System.getProperty("test.subnetId");
      if (subnetId == null) {
         // Skip test and return
         return;
      }

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getInstanceServices();

      String group = this.group + "g";

      TemplateOptions options = client.templateOptions();

      options.as(AWSEC2TemplateOptions.class).subnetId(subnetId).spotPrice(0.3f);

      String startedId = null;
      String nodeId = null;
      try {

         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, options);

         NodeMetadata first = Iterables.get(nodes, 0);
         assert first.getCredentials() != null : first;
         assert first.getCredentials().identity != null : first;

         startedId = Iterables.getOnlyElement(nodes).getProviderId();
         nodeId = Iterables.getOnlyElement(nodes).getId();

         AWSRunningInstance instance = AWSRunningInstance.class.cast(getInstance(instanceClient, startedId));

         assertEquals(instance.getSubnetId(), subnetId);

      } finally {
         if (nodeId != null)
            client.destroyNode(nodeId);
      }
   }

  @Test
  public void testSecurityGroupGetsDestroyedAfterInstanceIsDestroyed() throws Exception {
     AWSSecurityGroupClient securityGroupClient = AWSEC2Client.class.cast(context.getProviderSpecificContext().getApi())
              .getSecurityGroupServices();

     InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
              .getInstanceServices();

     String group = this.group + "foo";

     TemplateOptions options = client.templateOptions();

     try {
        Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 2, options);
        NodeMetadata first = Iterables.get(nodes, 0);
        NodeMetadata second = Iterables.get(nodes, 1);

        String startedId1 = Iterables.get(nodes, 0).getProviderId();
        String startedId2 = Iterables.get(nodes, 1).getProviderId();

        AWSRunningInstance instance1 = AWSRunningInstance.class.cast(getInstance(instanceClient, startedId1));
        AWSRunningInstance instance2 = AWSRunningInstance.class.cast(getInstance(instanceClient, startedId2));

        String instanceRegion = null;
        if(instance1.getRegion() == instance2.getRegion()){
            instanceRegion = instance1.getRegion();
        }
        assertNotNull(instanceRegion, "Nodes are not in the same region");

        Set<String> instanceGroupIds = null;
        if(instance1.getGroupIds().size() == instance2.getGroupIds().size()){
            instanceGroupIds = instance1.getGroupIds();
        }
        assertNotNull(instanceGroupIds, "Nodes group Ids are not the samee");

        String expectedSecurityGroupName = "jclouds#" + group + "#" + instanceRegion;
        assertEquals(Sets.newTreeSet(instanceGroupIds), ImmutableSortedSet.<String> of(expectedSecurityGroupName));

        Set<SecurityGroup> securityGroups = securityGroupClient.describeSecurityGroupsInRegion(instanceRegion, expectedSecurityGroupName);
        assertEquals(securityGroups.size(), 1);

        client.destroyNode(second.getId());

        Set<SecurityGroup> securityGroupsAfterDestroySecond = securityGroupClient.describeSecurityGroupsInRegion(instanceRegion, expectedSecurityGroupName);
        assertEquals(securityGroupsAfterDestroySecond.size(), 1);

        client.destroyNode(first.getId());

        Set<SecurityGroup> securityGroupsPostDestroyAll = securityGroupClient.describeSecurityGroupsInRegion(instanceRegion, expectedSecurityGroupName);
        assertEquals(securityGroupsPostDestroyAll, new HashSet());

     } finally {
        client.destroyNodesMatching(NodePredicates.inGroup(group));
     }
  }
}
