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
package org.jclouds.aws.ec2.compute;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.compute.domain.OsFamily.AMZN_LINUX;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.ec2.util.IpPermissions.permit;
import static org.testng.Assert.assertEquals;

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
import org.jclouds.cloudwatch.CloudWatchApi;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.EC2Constants;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.GetMetricStatisticsResponse;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
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
import org.jclouds.scriptbuilder.domain.Statements;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
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

      ImmutableMap<String, String> userMetadata = ImmutableMap.<String, String> of("test", group);

      ImmutableSet<String> tags = ImmutableSet.of(group);

      // note that if you change the location, you must also specify image parameters
      Template template = client.templateBuilder().locationId(region).osFamily(AMZN_LINUX).os64Bit(true).build();
      template.getOptions().tags(tags);
      template.getOptions().userMetadata(userMetadata);
      template.getOptions().tags(tags);
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

         checkUserMetadataContains(first, userMetadata);
         checkTagsInNodeEquals(first, tags);

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

         CloudWatchApi monitoringApi = ContextBuilder.newBuilder(new AWSCloudWatchProviderMetadata())
                                                     .credentials(identity, credential)
                                                     .modules(setupModules())
                                                     .buildApi(CloudWatchApi.class);

         try {
            GetMetricStatisticsResponse datapoints = monitoringApi.getMetricApiForRegion(instance.getRegion())
                     .getMetricStatistics(GetMetricStatistics.builder()
                                                             .dimension(new Dimension(EC2Constants.Dimension.INSTANCE_ID, instance.getId()))
                                                             .unit(Unit.PERCENT)
                                                             .namespace("AWS/EC2")
                                                             .metricName("CPUUtilization")
                                                             .startTime(before)
                                                             .endTime(new Date())
                                                             .period(60)
                                                             .statistic(Statistics.AVERAGE)
                                                             .build());
            assert (datapoints.size() > 0) : instance;
         } finally {
            monitoringApi.close();
         }

         // make sure we made our dummy group and also let in the user's group
         assertEquals(newTreeSet(instance.getGroupNames()), ImmutableSortedSet.<String> of("jclouds#" + group, group));

         // make sure our dummy group has no rules
         SecurityGroup secgroup = getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(instance
                  .getRegion(), "jclouds#" + group));

         assert secgroup.size() == 0 : secgroup;

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
}
