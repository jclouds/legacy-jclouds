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

import java.util.Set;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;
import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
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
      provider = "ec2";
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Test(enabled = true, dependsOnMethods = "testCorrectAuthException")
   public void testImagesResolveCorrectly() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getId(), defaultTemplate.getImage().getLocation().getId() + "/"
               + defaultTemplate.getImage().getProviderId());
      Template byId = client.templateBuilder().imageId(defaultTemplate.getImage().getId()).build();
      assertEquals(byId.getImage(), defaultTemplate.getImage());
   }

   @Test(enabled = true, dependsOnMethods = "testImagesResolveCorrectly")
   public void testDefaultTemplateBuilder() {
      assertDefaultWorks();
   }

   protected void assertDefaultWorks() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getArchitecture(), Architecture.X86_32);
      assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getSize().getCores(), 1.0d);
   }

   @Test(enabled = true, dependsOnMethods = "testDefaultTemplateBuilder")
   public void testExtendedOptionsAndLogin() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getInstanceServices();

      String tag = this.tag + "optionsandlogin";

      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).keyPair(tag);

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

         RunningInstance instance = getInstance(instanceClient, startedId);

         assertEquals(instance.getKeyName(), tag);

         // make sure we made our dummy group and also let in the user's group
         assertEquals(instance.getGroupIds(), ImmutableSet.<String> of(tag, "jclouds#" + tag));

         // make sure our dummy group has no rules
         SecurityGroup group = Iterables.getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(null,
                  "jclouds#" + tag));
         assert group.getIpPermissions().size() == 0 : group;

         // try to run a script with the original keyPair
         runScriptWithCreds(tag, first.getImage().getOsFamily(), new Credentials(first.getCredentials().identity,
                  result.getKeyMaterial()));

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

   @Test(enabled = true, dependsOnMethods = "testDefaultTemplateBuilder")
   public void testExtendedOptionsNoKeyPair() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getInstanceServices();

      String tag = this.tag + "optionsnokey";

      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).noKeyPair();

      String startedId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);

         // create the security group
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         Credentials creds = nodes.iterator().next().getCredentials();
         assert creds == null;

         startedId = Iterables.getOnlyElement(nodes).getProviderId();

         RunningInstance instance = getInstance(instanceClient, startedId);

         assertEquals(instance.getKeyName(), null);

         // make sure we made our dummy group and also let in the user's group
         assertEquals(instance.getGroupIds(), ImmutableSet.<String> of(tag, "jclouds#" + tag));

         // make sure our dummy group has no rules
         SecurityGroup group = Iterables.getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(null,
                  "jclouds#" + tag));
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

   @Test(enabled = true, dependsOnMethods = "testDefaultTemplateBuilder")
   public void testExtendedOptionsWithSubnetId() throws Exception {

      String subnetId = System.getProperty("jclouds.test.subnetId");
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

      String tag = this.tag + "optionswithsubnetid";

      TemplateOptions options = client.templateOptions();

      // options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).keyPair(tag);
      options.as(EC2TemplateOptions.class).subnetId(subnetId);

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

         RunningInstance instance = getInstance(instanceClient, startedId);

         assertEquals(instance.getSubnetId(), subnetId);
         assertEquals(instance.getSubnetId(), Iterables.getOnlyElement(nodes).getExtra().get("subnetId"));

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

   private RunningInstance getInstance(InstanceClient instanceClient, String id) {
      RunningInstance instance = Iterables.getOnlyElement(Iterables.getOnlyElement(instanceClient
               .describeInstancesInRegion(null, id)));
      return instance;
   }

   private void cleanupExtendedStuff(SecurityGroupClient securityGroupClient, KeyPairClient keyPairClient, String tag) {
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
