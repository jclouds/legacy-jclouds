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

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.compute.domain.OsFamily.AMZN_LINUX;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.ec2.util.IpPermissions.permit;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.cloudwatch.AWSCloudWatchProviderMetadata;
import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.ec2.services.AWSSecurityGroupClient;
import org.jclouds.cloudwatch.CloudWatchAsyncClient;
import org.jclouds.cloudwatch.CloudWatchClient;
import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.options.GetMetricStatisticsOptions;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.EC2ComputeServiceLiveTest;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.rest.RestContext;
import org.jclouds.scriptbuilder.domain.Statements;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

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

   // aws-ec2 supports userMetadata
   @Override
   protected void checkUserMetadataInNodeEquals(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().equals(userMetadata) : String.format("node userMetadata did not match %s %s",
               userMetadata, node);
   }
   
   @Override
   @Test
   public void testExtendedOptionsAndLogin() throws Exception {
      String region = "us-west-2";

      AWSSecurityGroupClient securityGroupClient = AWSEC2Client.class.cast(
               view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi()).getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi())
               .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi())
               .getInstanceServices();

      String group = this.group + "o";

      Date before = new Date();

      ImmutableMap<String, String> userMetadata = ImmutableMap.<String, String> of("Name", group);

      // note that if you change the location, you must also specify image parameters
      Template template = client.templateBuilder().locationId(region).osFamily(AMZN_LINUX).os64Bit(true).build();
      template.getOptions().userMetadata(userMetadata);
      template.getOptions().as(AWSEC2TemplateOptions.class).enableMonitoring();
      template.getOptions().as(AWSEC2TemplateOptions.class).spotPrice(0.3f);

      String startedId = null;
      try {
         cleanupExtendedStuffInRegion(region, securityGroupClient, keyPairClient, group);

         Thread.sleep(3000);// eventual consistency if deletes actually occurred.

         // create a security group that allows ssh in so that our scripts later
         // will work
         String groupId = securityGroupClient.createSecurityGroupInRegionAndReturnId(region, group, group);

         securityGroupClient.authorizeSecurityGroupIngressInRegion(region, groupId, permit(IpProtocol.TCP).port(22));

         template.getOptions().as(AWSEC2TemplateOptions.class).securityGroupIds(groupId);

         // create a keypair to pass in as well
         KeyPair result = keyPairClient.createKeyPairInRegion(region, group);
         template.getOptions().as(AWSEC2TemplateOptions.class).keyPair(result.getKeyName());

         // pass in the private key, so that we can run a script with it
         assert result.getKeyMaterial() != null : result;
         template.getOptions().overrideLoginPrivateKey(result.getKeyMaterial());

         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, template);
         NodeMetadata first = getOnlyElement(nodes);

         // Name metadata should turn into node.name
         assertEquals(first.getName(), group);

         checkUserMetadataInNodeEquals(first, userMetadata);

         assert first.getCredentials() != null : first;
         assert first.getCredentials().identity != null : first;

         startedId = first.getProviderId();

         AWSRunningInstance instance = AWSRunningInstance.class.cast(getOnlyElement(getOnlyElement(instanceClient
                  .describeInstancesInRegion(region, startedId))));

         assertEquals(instance.getKeyName(), group);
         assert instance.getSpotInstanceRequestId() != null;
         assertEquals(instance.getMonitoringState(), MonitoringState.ENABLED);

         // generate some load
         ListenableFuture<ExecResponse> future = client.submitScriptOnNode(first.getId(), Statements
                  .exec("while true; do true; done"), runAsRoot(false).nameTask("cpuSpinner"));

         // monitoring granularity for free tier is 5 minutes, so lets make sure we have data.
         Thread.sleep(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));

         // stop the spinner
         future.cancel(true);

         RestContext<CloudWatchClient, CloudWatchAsyncClient> monitoringContext = ContextBuilder
               .newBuilder(new AWSCloudWatchProviderMetadata())
               .credentials(identity, credential)
               .modules(setupModules()).build();

         try {
            Set<Datapoint> datapoints = monitoringContext.getApi().getMetricStatisticsInRegion(instance.getRegion(),
                     "CPUUtilization", "AWS/EC2", before, new Date(), 60, Statistics.AVERAGE,
                     GetMetricStatisticsOptions.Builder.instanceId(instance.getId()).unit(Unit.PERCENT));
            assert (datapoints.size() > 0) : instance;
         } finally {
            monitoringContext.close();
         }

         // make sure we made our dummy group and also let in the user's group
         assertEquals(newTreeSet(instance.getGroupIds()), ImmutableSortedSet.<String> of("jclouds#" + group, group));

         // make sure our dummy group has no rules
         SecurityGroup secgroup = getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(instance
                  .getRegion(), "jclouds#" + group));

         assert secgroup.getIpPermissions().size() == 0 : secgroup;

         // try to run a script with the original keyPair
         runScriptWithCreds(group, first.getOperatingSystem(), LoginCredentials.builder().user(
                  first.getCredentials().identity).privateKey(result.getKeyMaterial()).build());

      } finally {
         client.destroyNodesMatching(NodePredicates.inGroup(group));
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(keyPairClient.describeKeyPairsInRegion(region, group).size(), 1);
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(region, group).size(), 1);
         }
         cleanupExtendedStuffInRegion(region, securityGroupClient, keyPairClient, group);
      }

   }

   @Test
   public void testIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroyNode() throws Exception {
      Function<String,Void> destroyer = new Function<String,Void>() {
         @Override
         public Void apply(String instanceId) {
            client.destroyNode(instanceId);
            return null;
         }
      };
      runIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroy(destroyer);
   }
   
   @Test
   public void testIncidentalResourcesGetCleanedUpOnlyOnLastInstanceDestroyNodesMatching() throws Exception {
      Function<String,Void> destroyer = new Function<String,Void>() {
         @Override
         public Void apply(String instanceId) {
            client.destroyNodesMatching(NodePredicates.<NodeMetadata>withIds(instanceId));
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

      String group = this.group + "incidental";
      String region = null;
      
      try {
         // Create two instances
         // TODO set spotPrice(0.3f) ?
         Template template = client.templateBuilder().build();
         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 2, template);
         NodeMetadata first = Iterables.get(nodes, 0);
         NodeMetadata second = Iterables.get(nodes, 1);

         String instanceId1 = Iterables.get(nodes, 0).getProviderId();
         String instanceId2 = Iterables.get(nodes, 1).getProviderId();

         AWSRunningInstance instance1 = AWSRunningInstance.class.cast(getInstance(instanceClient, instanceId1));
         AWSRunningInstance instance2 = AWSRunningInstance.class.cast(getInstance(instanceClient, instanceId2));

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
         client.destroyNodesMatching(NodePredicates.inGroup(group));
         
         if (region != null) cleanupExtendedStuffInRegion(region, securityGroupClient, keyPairClient, group);
      }
   }
}
