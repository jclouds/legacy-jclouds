/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.testng.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.services.ElasticLoadBalancerClient;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;
import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Credentials;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.EC2ComputeServiceLiveTest")
public class EC2ComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "ec2";
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Test
   public void testExtendedOptionsAndLogin() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(
               context.getProviderSpecificContext().getApi()).getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(
               context.getProviderSpecificContext().getApi()).getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(
               context.getProviderSpecificContext().getApi()).getInstanceServices();

      String tag = this.tag + "optionsandlogin";

      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).keyPair(tag);

      String startedId = null;
      try {
         // create a security group that allows ssh in so that our scripts later will work
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);
         securityGroupClient.authorizeSecurityGroupIngressInRegion(null, tag, IpProtocol.TCP, 22,
                  22, "0.0.0.0/0");

         // create a keypair to pass in as well
         KeyPair result = keyPairClient.createKeyPairInRegion(null, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         NodeMetadata first = Iterables.get(nodes, 0);
         assert first.getCredentials() != null : first;
         assert first.getCredentials().account != null : first;

         startedId = Iterables.getOnlyElement(nodes).getId();

         RunningInstance instance = getInstance(instanceClient, startedId);

         assertEquals(instance.getKeyName(), tag);

         // make sure we made our dummy group and also let in the user's group
         assertEquals(instance.getGroupIds(), ImmutableSet.<String> of(tag, "jclouds#" + tag));

         // make sure our dummy group has no rules
         SecurityGroup group = Iterables.getOnlyElement(securityGroupClient
                  .describeSecurityGroupsInRegion(null, "jclouds#" + tag));
         assert group.getIpPermissions().size() == 0 : group;

         // try to run a script with the original keyPair
         runScriptWithCreds(tag, first.getImage().getOsFamily(), new Credentials(first
                  .getCredentials().account, result.getKeyMaterial()));

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

   @Test
   public void testExtendedOptionsNoKeyPair() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(
               context.getProviderSpecificContext().getApi()).getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(
               context.getProviderSpecificContext().getApi()).getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(
               context.getProviderSpecificContext().getApi()).getInstanceServices();

      String tag = this.tag + "optionsnokey";

      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).noKeyPair();

      String startedId = null;
      try {
         // create the security group
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         Credentials creds = nodes.iterator().next().getCredentials();
         assert creds == null;

         startedId = Iterables.getOnlyElement(nodes).getId();

         RunningInstance instance = getInstance(instanceClient, startedId);

         assertEquals(instance.getKeyName(), null);

         // make sure we made our dummy group and also let in the user's group
         assertEquals(instance.getGroupIds(), ImmutableSet.<String> of(tag, "jclouds#" + tag));

         // make sure our dummy group has no rules
         SecurityGroup group = Iterables.getOnlyElement(securityGroupClient
                  .describeSecurityGroupsInRegion(null, "jclouds#" + tag));
         assert group.getIpPermissions().size() == 0 : group;

      } finally {
         client.destroyNodesMatching(NodePredicates.withTag(tag));
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(null, tag).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);
      }
   }
   
    @Test
    public void testLoadBalanceNodesMatching() throws Exception{

        ElasticLoadBalancerClient elbClient = EC2Client.class.cast(
                context.getProviderSpecificContext().getApi())
                .getElasticLoadBalancerServices();

        String tag = "jcloudsElbTest";
        Template template = client.templateBuilder().build();
        Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 2,
                template);
        Set<String> instanceIds = new HashSet<String>();
        for (NodeMetadata node : nodes)
        {
            instanceIds.add(node.getId());
        }

        // create load balancer
        String dnsName = client.loadBalanceNodesMatching(tag, "HTTP", 80, 80,
                NodePredicates.withTag(tag));
        assertNotNull(dnsName);
        Set<ElasticLoadBalancer> elbs = elbClient.describeLoadBalancers(
                Region.US_EAST_1, tag);
        assertNotNull(elbs);
        ElasticLoadBalancer elb = elbs.iterator().next();
        assertEquals(elb.getInstanceIds(), instanceIds);
        
        elbClient.deleteLoadBalancer(Region.US_EAST_1, tag);
        //finaly destroy nodes
        client.destroyNodesMatching(NodePredicates.withTag(tag));

    }

   private RunningInstance getInstance(InstanceClient instanceClient, String id) {
      RunningInstance instance = Iterables.getOnlyElement(Iterables.getOnlyElement(instanceClient
               .describeInstancesInRegion(null, id)));
      return instance;
   }

   private void cleanupExtendedStuff(SecurityGroupClient securityGroupClient,
            KeyPairClient keyPairClient, String tag) {
      try {
         securityGroupClient.deleteSecurityGroupInRegion(null, tag);
      } catch (Exception e) {

      }
      try {
         keyPairClient.deleteKeyPairInRegion(null, tag);
      } catch (Exception e) {

      }
   }

}
