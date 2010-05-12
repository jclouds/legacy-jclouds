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
import org.jclouds.compute.domain.TemplateBuilder;
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

   @Test
   public void testTemplateBuilderCanUseImageId() {
      client.templateBuilder().imageId(Iterables.get(client.listImages(), 0).getId()).build();
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assert (defaultTemplate.getImage().getId().startsWith("ami-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getName(), "9.10");
      assertEquals(defaultTemplate.getImage().getArchitecture(), Architecture.X86_32);
      assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getLocation().getId(), "us-east-1");
      assertEquals(defaultTemplate.getSize().getCores(), 1.0d);
      client.templateBuilder().osFamily(OsFamily.UBUNTU).smallest().architecture(
               Architecture.X86_32).imageId("ami-7e28ca17").build();
      client.templateBuilder().osFamily(OsFamily.UBUNTU).smallest().architecture(
               Architecture.X86_32).imageId("ami-bb709dd2").build();
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.imageId("ami-714ba518").build();
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

      Template template = buildTemplate(client.templateBuilder());

      template.getOptions().as(EC2TemplateOptions.class).securityGroups(tag);
      template.getOptions().as(EC2TemplateOptions.class).keyPair(tag);

      String startedId = null;
      try {
         // create a security group that allows ssh in so that our scripts later will work
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);
         securityGroupClient.authorizeSecurityGroupIngressInRegion(null, tag, IpProtocol.TCP, 22,
                  22, "0.0.0.0/0");

         // create a keypair to pass in as well
         KeyPair result = keyPairClient.createKeyPairInRegion(null, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, template);
         Credentials good = nodes.iterator().next().getCredentials();
         assert good.account != null;

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
         runScriptWithCreds(tag, template.getImage().getOsFamily(), new Credentials(good.account,
                  result.getKeyMaterial()));

      } finally {
         client.destroyNodesWithTag(tag);
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

      Template template = buildTemplate(client.templateBuilder());

      template.getOptions().as(EC2TemplateOptions.class).securityGroups(tag);
      template.getOptions().as(EC2TemplateOptions.class).noKeyPair();

      String startedId = null;
      try {
         // create the security group
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, template);
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
         client.destroyNodesWithTag(tag);
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(null, tag).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);
      }
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
