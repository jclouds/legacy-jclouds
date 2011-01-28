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

package org.jclouds.aws.ec2.compute;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
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
import org.jclouds.ec2.services.SecurityGroupClient;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "AWSEC2ComputeServiceLiveTest")
public class AWSEC2ComputeServiceLiveTest extends EC2ComputeServiceLiveTest {

   public AWSEC2ComputeServiceLiveTest() {
      provider = "aws-ec2";
      tag = "ec2";
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
   public void testExtendedOptionsAndLogin() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getInstanceServices();

      String tag = this.tag + "o";

      TemplateOptions options = client.templateOptions();

      // Date before = new Date();

      options.as(AWSEC2TemplateOptions.class).securityGroups(tag);
      options.as(AWSEC2TemplateOptions.class).keyPair(tag);
      options.as(AWSEC2TemplateOptions.class).enableMonitoring();

      String startedId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);

         // create a security group that allows ssh in so that our scripts later
         // will work
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);
         securityGroupClient.authorizeSecurityGroupIngressInRegion(null, tag, IpProtocol.TCP, 22, 22, "0.0.0.0/0");

         // create a keypair to pass in as well
         KeyPair result = keyPairClient.createKeyPairInRegion(null, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         NodeMetadata first = Iterables.get(nodes, 0);
         assert first.getCredentials() != null : first;
         assert first.getCredentials().identity != null : first;

         startedId = Iterables.getOnlyElement(nodes).getProviderId();

         AWSRunningInstance instance = AWSRunningInstance.class.cast(getInstance(instanceClient, startedId));

         assertEquals(instance.getKeyName(), tag);
         assertEquals(instance.getMonitoringState(), MonitoringState.ENABLED);

         // TODO when the cloudwatchclient is finished

         // RestContext<CloudWatchClient, CloudWatchAsyncClient> monitoringContext = new
         // RestContextFactory().createContext(
         // "cloudwatch", identity, credential, ImmutableSet.<Module> of(new Log4JLoggingModule()));
         //
         // try {
         // Set<Datapoint> datapoints =
         // monitoringContext.getApi().getMetricStatisticsInRegion(instance.getRegion(),
         // "CPUUtilization", before, new Date(), 60, "Average");
         // assert datapoints != null;
         // } finally {
         // monitoringContext.close();
         // }

         // make sure we made our dummy group and also let in the user's group
         assertEquals(Sets.newTreeSet(instance.getGroupIds()), ImmutableSortedSet.<String> of("jclouds#" + tag + "#"
                  + instance.getRegion(), tag));

         // make sure our dummy group has no rules
         SecurityGroup group = Iterables.getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(null,
                  "jclouds#" + tag + "#" + instance.getRegion()));
         assert group.getIpPermissions().size() == 0 : group;

         // try to run a script with the original keyPair
         runScriptWithCreds(tag, first.getOperatingSystem(), new Credentials(first.getCredentials().identity, result
                  .getKeyMaterial()));

      } finally {
         client.destroyNodesMatching(NodePredicates.withTag(tag));
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(keyPairClient.describeKeyPairsInRegion(null, tag).size(), 1);
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(null, tag).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);
      }
      
   }
   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
   public void testExtendedOptionsWithSubnetId() throws Exception {

      String subnetId = System.getProperty("test.subnetId");
      if (subnetId == null) {
         // Skip test and return
         return;
      }
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getInstanceServices();

      String tag = this.tag + "g";

      TemplateOptions options = client.templateOptions();

      // options.as(AWSEC2TemplateOptions.class).securityGroups(tag);
      options.as(AWSEC2TemplateOptions.class).keyPair(tag);
      options.as(AWSEC2TemplateOptions.class).subnetId(subnetId);

      String startedId = null;
      String nodeId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);

         // create the security group
         // securityGroupClient.createSecurityGroupInRegion(null, tag, tag);

         // create a keypair to pass in as well
         keyPairClient.createKeyPairInRegion(null, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);

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
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(keyPairClient.describeKeyPairsInRegion(null, tag).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);
      }
   }

}
