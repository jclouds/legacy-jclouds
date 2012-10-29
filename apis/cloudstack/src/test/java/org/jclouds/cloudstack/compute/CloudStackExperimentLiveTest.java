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
package org.jclouds.cloudstack.compute;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.vlan;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.specifyVLAN;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.EncryptedPasswordAndPrivateKey;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.functions.WindowsLoginCredentialsFromEncryptedData;
import org.jclouds.cloudstack.options.ListNetworksOptions;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.crypto.Crypto;
import org.jclouds.encryption.bouncycastle.BouncyCastleCrypto;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "CloudStackExperimentLiveTest")
public class CloudStackExperimentLiveTest extends BaseCloudStackClientLiveTest {

   public CloudStackExperimentLiveTest() {
      provider = "cloudstack";
   }

   protected void deleteNetworksInZoneWithVlanId(String zoneId, String vlanId) {
      Set<Network> networks = domainAdminContext.getApi().getNetworkClient().listNetworks(
         ListNetworksOptions.Builder
            .isDefault(false)
            .isSystem(false)
            .zoneId(zoneId)
            .trafficType(TrafficType.GUEST)
      );

      // Warning: the vlan id is not set in the response - using an workaround
      URI broadcastUri = URI.create("vlan://" + vlanId);
      for (Network net : networks) {
         if (broadcastUri.equals(net.getBroadcastURI())) {
            String jobId = domainAdminContext.getApi().getNetworkClient().deleteNetwork(net.getId());
            adminJobComplete.apply(jobId);
         }
      }
   }

   @Test
   public void testAndExperiment() {
      if (!domainAdminEnabled) {
         Logger.getAnonymousLogger().log(Level.SEVERE, "domainAdmin credentials not present, skipping test");
         return;
      }

      String group = prefix + "-vlan";
      String vlanId = "2";
      Network network = null;
      Set<? extends NodeMetadata> nodes = null;
      try {
         assert view.getComputeService().listAssignableLocations().size() > 0;

         Template template = view.getComputeService().templateBuilder().build();

         // get the zone we are launching into
         String zoneId = template.getLocation().getId();

         // cleanup before running the test
         deleteNetworksInZoneWithVlanId(zoneId, vlanId);

         // find a network offering that supports vlans in our zone
         String offeringId = get(
            cloudStackContext.getApi().getOfferingClient().listNetworkOfferings(specifyVLAN(true).zoneId(zoneId)), 0).getId();

         // create an arbitrary network
         network = domainAdminContext.getApi()
            .getNetworkClient()
               // startIP/endIP/netmask/gateway must be specified together
            .createNetworkInZone(zoneId, offeringId, group, group,
               vlan(vlanId).startIP("192.168.1.2").netmask("255.255.255.0").gateway("192.168.1.1"));

         // set options to specify this network id
         template.getOptions().as(CloudStackTemplateOptions.class).networkId(network.getId());

         // launch the VM
         nodes = view.getComputeService().createNodesInGroup(group, 1, template);

         assert nodes.size() > 0;

      } catch (RunNodesException e) {
         Logger.getAnonymousLogger().log(Level.SEVERE, "error creating nodes", e);
         nodes = newTreeSet(concat(e.getSuccessfulNodes(), e.getNodeErrors().keySet()));
      } finally {
         if (nodes != null)
            view.getComputeService().destroyNodesMatching(NodePredicates.inGroup(group));
         if (network != null)
            domainAdminContext.getApi().getNetworkClient().deleteNetwork(network.getId());
      }
   }

   @Test(enabled = false)
   public void testCreateWindowsMachineWithKeyPairAndCheckIfTheGeneratedPasswordIsEncrypted()
      throws RunNodesException, NoSuchAlgorithmException, CertificateException {
      // final Map<String, String> sshKey = SshKeys.generate();
      // final String publicKey = sshKey.get("public");

      String keyPairName = prefix + "-windows-keypair";
      client.getSSHKeyPairClient().deleteSSHKeyPair(keyPairName);
      // client.getSSHKeyPairClient().registerSSHKeyPair(keyPairName, publicKey);

      SshKeyPair keyPair = client.getSSHKeyPairClient().createSSHKeyPair(keyPairName);

      String group = prefix + "-windows-test";
      Template template = view.getComputeService().templateBuilder()
         .imageId("290").locationId("1")
         .options(new CloudStackTemplateOptions().setupStaticNat(false).keyPair(keyPairName))
         .build();

      NodeMetadata node = null;
      try {
         node = getOnlyElement(view.getComputeService()
            .createNodesInGroup(group, 1, template));

         String encryptedPassword = client.getVirtualMachineClient()
            .getEncryptedPasswordForVirtualMachine(node.getId());

         Crypto crypto = new BouncyCastleCrypto();
         WindowsLoginCredentialsFromEncryptedData passwordDecrypt = new WindowsLoginCredentialsFromEncryptedData(crypto);

         assertEquals(passwordDecrypt.apply(
            EncryptedPasswordAndPrivateKey.builder().encryptedPassword(encryptedPassword).privateKey(keyPair.getPrivateKey()).build())
               .getPassword(), "bX7vvptvw");

      } finally {
         if (node != null) {
            view.getComputeService().destroyNode(node.getId());
         }

      }

   }

}
