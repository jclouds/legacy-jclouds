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

package org.jclouds.ec2.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.AvailabilityZone;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class EC2ComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public EC2ComputeServiceLiveTest() {
      provider = "ec2";
   }

   @Override
   protected Module getSshModule() {
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
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "2010.11.1-beta");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

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
         assertEquals(Sets.newTreeSet(instance.getGroupIds()),
               ImmutableSortedSet.<String> of("jclouds#" + tag + "#" + instance.getRegion(), tag));

         // make sure our dummy group has no rules
         SecurityGroup group = Iterables.getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(null,
               "jclouds#" + tag + "#" + instance.getRegion()));
         assert group.getIpPermissions().size() == 0 : group;

         // try to run a script with the original keyPair
         runScriptWithCreds(tag, first.getOperatingSystem(),
               new Credentials(first.getCredentials().identity, result.getKeyMaterial()));

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
   public void testExtendedOptionsNoKeyPair() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getInstanceServices();

      String tag = this.tag + "k";

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
         assertEquals(Sets.newTreeSet(instance.getGroupIds()),
               ImmutableSortedSet.<String> of(tag, String.format("jclouds#%s#%s", tag, instance.getRegion())));

         // make sure our dummy group has no rules
         SecurityGroup group = Iterables.getOnlyElement(securityGroupClient.describeSecurityGroupsInRegion(null,
               String.format("jclouds#%s#%s", tag, instance.getRegion())));
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
   
   @Test(enabled = true)
   public void testMapNewVolumeToDeviceName() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getInstanceServices();

      String tag = this.tag + "BDM1";
      int volumeSize = 120;
      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).noKeyPair();
      options.as(EC2TemplateOptions.class).mapNewVolumeToDeviceName("/dev/sda1", volumeSize, true);

      String startedId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);

         // create the security group
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         Credentials creds = nodes.iterator().next().getCredentials();
         assert creds == null;

         NodeMetadata node = nodes.iterator().next();
         startedId = node.getId();
         
         Map<String, BlockDevice> devices = instanceClient
         .getBlockDeviceMappingForInstanceInRegion(node.getLocation()
                 .getParent().getId(), node.getProviderId());

         BlockDevice device = devices.get("/dev/sda1");
         //check  delete on termination
         assertTrue(device.isDeleteOnTermination());
         ElasticBlockStoreClient ebsClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
             .getElasticBlockStoreServices();

         Set<Volume> volumes = ebsClient.describeVolumesInRegion(node
                   .getLocation().getParent().getId(), device.getVolumeId());
         // check volume size
         assertEquals(volumeSize, volumes.iterator().next().getSize());

      } finally {
         client.destroyNodesMatching(NodePredicates.withTag(tag));
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(null, tag).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);
      }
   }
   
   @Test(enabled = true)
   public void testMapEBSSnapshotToDeviceName() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getInstanceServices();
      
      ElasticBlockStoreClient ebsClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
      .getElasticBlockStoreServices();
      //create snapshot
      Volume volume = ebsClient.createVolumeInAvailabilityZone(AvailabilityZone.US_EAST_1A, 4);
      Snapshot snapshot = ebsClient.createSnapshotInRegion(volume.getRegion(), volume.getId());

      String tag = this.tag + "BDM2";
      int volumeSize = 120;
      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).noKeyPair();
      options.as(EC2TemplateOptions.class).mapEBSSnapshotToDeviceName("/dev/sda1", snapshot.getId(), 120, true);      
      
      String startedId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);

         // create the security group
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         Credentials creds = nodes.iterator().next().getCredentials();
         assert creds == null;

         NodeMetadata node = nodes.iterator().next();

         Map<String, BlockDevice> devices = instanceClient
         .getBlockDeviceMappingForInstanceInRegion(node.getLocation()
                 .getParent().getId(), node.getProviderId());

         BlockDevice device = devices.get("/dev/sda1");        
         //check  delete on termination
         assertTrue(device.isDeleteOnTermination());
         
         Set<Volume> volumes = ebsClient.describeVolumesInRegion(node
                   .getLocation().getParent().getId(), device.getVolumeId());
         // check volume size
         assertEquals(volumeSize, volumes.iterator().next().getSize());
         //check volume's snapshot id
         assertEquals(snapshot.getId(), volumes.iterator().next().getSnapshotId());


      } finally {
         client.destroyNodesMatching(NodePredicates.withTag(tag));
         ebsClient.deleteSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
         ebsClient.deleteVolumeInRegion(volume.getRegion(), volume.getId());
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(null, tag).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);
      }
   }
   
   @Test(enabled = true)
   public void testMapEphemeralDeviceToDeviceName() throws Exception {
      SecurityGroupClient securityGroupClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getSecurityGroupServices();

      KeyPairClient keyPairClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getKeyPairServices();

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
            .getInstanceServices();

      String tag = this.tag + "BDM3";

      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(tag);
      options.as(EC2TemplateOptions.class).noKeyPair();
      options.as(EC2TemplateOptions.class).mapEphemeralDeviceToDeviceName("/dev/sdh", "ephemeral0");

      String startedId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);

         // create the security group
         securityGroupClient.createSecurityGroupInRegion(null, tag, tag);

         Set<? extends NodeMetadata> nodes = client.runNodesWithTag(tag, 1, options);
         Credentials creds = nodes.iterator().next().getCredentials();
         assert creds == null;

         NodeMetadata node = nodes.iterator().next();
         startedId = node.getId();
         
         Map<String, BlockDevice> devices = instanceClient
         .getBlockDeviceMappingForInstanceInRegion(node.getLocation()
                 .getParent().getId(), node.getProviderId());

         BlockDevice device = devices.get("/dev/sdh");
         assertNotNull(device);

      } finally {
         client.destroyNodesMatching(NodePredicates.withTag(tag));
         if (startedId != null) {
            // ensure we didn't delete these resources!
            assertEquals(securityGroupClient.describeSecurityGroupsInRegion(null, tag).size(), 1);
         }
         cleanupExtendedStuff(securityGroupClient, keyPairClient, tag);
      }
   }
   

   protected RunningInstance getInstance(InstanceClient instanceClient, String id) {
      RunningInstance instance = Iterables.getOnlyElement(Iterables.getOnlyElement(instanceClient
            .describeInstancesInRegion(null, id)));
      return instance;
   }

   protected void cleanupExtendedStuff(SecurityGroupClient securityGroupClient, KeyPairClient keyPairClient, String tag)
         throws InterruptedException {
      try {
         for (SecurityGroup group : securityGroupClient.describeSecurityGroupsInRegion(null))
            if (group.getName().startsWith("jclouds#" + tag) || group.getName().equals(tag)) {
               securityGroupClient.deleteSecurityGroupInRegion(null, group.getName());
            }
      } catch (Exception e) {

      }
      try {
         for (KeyPair pair : keyPairClient.describeKeyPairsInRegion(null))
            if (pair.getKeyName().startsWith("jclouds#" + tag) || pair.getKeyName().equals(tag)) {
               keyPairClient.deleteKeyPairInRegion(null, pair.getKeyName());
            }
      } catch (Exception e) {

      }
      Thread.sleep(2000);
   }

}
