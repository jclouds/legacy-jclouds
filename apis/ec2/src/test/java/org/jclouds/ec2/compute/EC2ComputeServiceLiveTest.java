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

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceType;
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

import com.google.common.base.Predicate;
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

      String group = this.group + "o";

      TemplateOptions options = client.templateOptions();

      options.as(EC2TemplateOptions.class).securityGroups(group);
      options.as(EC2TemplateOptions.class).keyPair(group);

      String startedId = null;
      try {
         cleanupExtendedStuff(securityGroupClient, keyPairClient, group);

         // create a security group that allows ssh in so that our scripts later
         // will work
         securityGroupClient.createSecurityGroupInRegion(null, group, group);
         securityGroupClient.authorizeSecurityGroupIngressInRegion(null, group, IpProtocol.TCP, 22, 22, "0.0.0.0/0");

         // create a keypair to pass in as well
         KeyPair result = keyPairClient.createKeyPairInRegion(null, group);

         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, options);
         NodeMetadata first = Iterables.get(nodes, 0);
         assert first.getCredentials() != null : first;
         assert first.getCredentials().identity != null : first;

         startedId = Iterables.getOnlyElement(nodes).getProviderId();

         RunningInstance instance = getInstance(instanceClient, startedId);

         assertEquals(instance.getKeyName(), group);

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

   /**
    * Note we cannot use the micro size as it has no ephemeral space.
    */
   @Test(enabled = true)
   public void testMapEBS() throws Exception {

      InstanceClient instanceClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getInstanceServices();

      ElasticBlockStoreClient ebsClient = EC2Client.class.cast(context.getProviderSpecificContext().getApi())
               .getElasticBlockStoreServices();

      String group = this.group + "e";
      int volumeSize = 8;

      Location zone = Iterables.find(context.getComputeService().listAssignableLocations(), new Predicate<Location>() {

         @Override
         public boolean apply(Location arg0) {
            return arg0.getScope() == LocationScope.ZONE;
         }

      });

      // create volume only to make a snapshot
      Volume volume = ebsClient.createVolumeInAvailabilityZone(zone.getId(), 4);
      Snapshot snapshot = ebsClient.createSnapshotInRegion(volume.getRegion(), volume.getId());
      ebsClient.deleteVolumeInRegion(volume.getRegion(), volume.getId());

      Template template = context.getComputeService().templateBuilder().locationId(volume.getRegion()).hardwareId(
               InstanceType.M1_SMALL).imageDescriptionMatches(".*ebs.*").build();

      template.getOptions().as(EC2TemplateOptions.class)//
               // .unmapDeviceNamed("/dev/foo)
               .mapEphemeralDeviceToDeviceName("/dev/sdm", "ephemeral0")//
               .mapNewVolumeToDeviceName("/dev/sdn", volumeSize, true)//
               .mapEBSSnapshotToDeviceName("/dev/sdo", snapshot.getId(), volumeSize, true);

      try {
         NodeMetadata node = Iterables.getOnlyElement(client.createNodesInGroup(group, 1, template));

         // TODO figure out how to validate the ephemeral drive. perhaps with df -k?

         Map<String, BlockDevice> devices = instanceClient.getBlockDeviceMappingForInstanceInRegion(node.getLocation()
                  .getParent().getId(), node.getProviderId());

         BlockDevice device = devices.get("/dev/sdn");
         // check delete on termination
         assertTrue(device.isDeleteOnTermination());

         volume = Iterables.getOnlyElement(ebsClient.describeVolumesInRegion(node.getLocation().getParent().getId(),
                  device.getVolumeId()));
         // check volume size
         assertEquals(volumeSize, volume.getSize());

         device = devices.get("/dev/sdo");
         // check delete on termination
         assertTrue(device.isDeleteOnTermination());

         volume = Iterables.getOnlyElement(ebsClient.describeVolumesInRegion(node.getLocation().getParent().getId(),
                  device.getVolumeId()));
         // check volume size
         assertEquals(volumeSize, volume.getSize());
         // check volume's snapshot id
         assertEquals(snapshot.getId(), volume.getSnapshotId());

      } finally {
         client.destroyNodesMatching(NodePredicates.inGroup(group));
         ebsClient.deleteSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
      }
   }

   protected RunningInstance getInstance(InstanceClient instanceClient, String id) {
      RunningInstance instance = Iterables.getOnlyElement(Iterables.getOnlyElement(instanceClient
               .describeInstancesInRegion(null, id)));
      return instance;
   }

   protected void cleanupExtendedStuff(SecurityGroupClient securityGroupClient, KeyPairClient keyPairClient, String group)
            throws InterruptedException {
      try {
         for (SecurityGroup secgroup : securityGroupClient.describeSecurityGroupsInRegion(null))
            if (secgroup.getName().startsWith("jclouds#" + group) || secgroup.getName().equals(group)) {
               securityGroupClient.deleteSecurityGroupInRegion(null, secgroup.getName());
            }
      } catch (Exception e) {

      }
      try {
         for (KeyPair pair : keyPairClient.describeKeyPairsInRegion(null))
            if (pair.getKeyName().startsWith("jclouds#" + group) || pair.getKeyName().equals(group)) {
               keyPairClient.deleteKeyPairInRegion(null, pair.getKeyName());
            }
      } catch (Exception e) {

      }
      Thread.sleep(2000);
   }

}
