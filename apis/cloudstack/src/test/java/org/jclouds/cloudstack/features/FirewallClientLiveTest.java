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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsPortForwarding;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.Predicates;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.net.IPSocket;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * Tests behavior of {@code FirewallClientLiveTest}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "FirewallClientLiveTest")
public class FirewallClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private VirtualMachine vm;

   private FirewallRule firewallRule;
   private PortForwardingRule portForwardingRule;

   private Network network;
   private boolean networksDisabled;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();
      prefix += "rule";
      try {
         network = find(client.getNetworkClient().listNetworks(), Predicates.and(supportsPortForwarding(),
            new Predicate<Network>() {
               @Override
               public boolean apply(@Nullable Network network) {
                  return network.isDefault()
                     && !network.isSecurityGroupEnabled()
                     && network.getAccount().equals(user.getAccount());
               }
            }));

         Long defaultTemplate = (imageId != null && !"".equals(imageId)) ? new Long(imageId) : null;

         vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network,
            defaultTemplateOrPreferredInZone(defaultTemplate, client, network.getZoneId()),
            client, jobComplete, virtualMachineRunning);

         if (vm.getPassword() != null && !loginCredentials.hasPasswordOption())
            loginCredentials = loginCredentials.toBuilder().password(vm.getPassword()).build();

      } catch (NoSuchElementException e) {
         networksDisabled = true;
      }
   }

   public void testCreatePortForwardingRule() throws Exception {
      if (networksDisabled)
         return;
      while (portForwardingRule == null) {
         ip = reuseOrAssociate.apply(network);
         try {
            AsyncCreateResponse job = client.getFirewallClient().createPortForwardingRuleForVirtualMachine(vm.getId(),
               ip.getId(), "tcp", 22, 22);
            assertTrue(jobComplete.apply(job.getJobId()));
            portForwardingRule = client.getFirewallClient().getPortForwardingRule(job.getId());

         } catch (IllegalStateException e) {
            // very likely an ip conflict, so retry;
         }
      }

      assertEquals(portForwardingRule.getIPAddressId(), ip.getId());
      assertEquals(portForwardingRule.getVirtualMachineId(), vm.getId());
      assertEquals(portForwardingRule.getPublicPort(), 22);
      assertEquals(portForwardingRule.getProtocol(), "tcp");

      checkPortForwardingRule(portForwardingRule);
      checkSSH(new IPSocket(ip.getIPAddress(), 22));
   }

   @Test(dependsOnMethods = "testCreatePortForwardingRule")
   public void testListPortForwardingRules() throws Exception {
      Set<PortForwardingRule> response = client.getFirewallClient().listPortForwardingRules();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (final PortForwardingRule rule : response) {
         PortForwardingRule newDetails = client.getFirewallClient().getPortForwardingRule(rule.getId());
         assertEquals(rule.getId(), newDetails.getId());
         checkPortForwardingRule(rule);
      }
   }

   @Test(dependsOnMethods = "testCreatePortForwardingRule")
   public void testCreateFirewallRule() {

   }

   @Test(dependsOnMethods = "testCreateFirewallRule")
   public void testListFirewallRules() {
      Set<FirewallRule> rules = client.getFirewallClient().listFirewallRules();
      assert rules != null;
      assertTrue(rules.size() >= 0);
      // TODO: check each rule
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (portForwardingRule != null) {
         client.getFirewallClient().deletePortForwardingRule(portForwardingRule.getId());
      }
      if (vm != null) {
         jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
      }
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDown();
   }

   protected void checkPortForwardingRule(PortForwardingRule rule) {
      assertEquals(rule.getId(), client.getFirewallClient().getPortForwardingRule(rule.getId()).getId());
      assert rule.getId() > 0 : rule;
      assert rule.getIPAddress() != null : rule;
      assert rule.getIPAddressId() > 0 : rule;
      assert rule.getPrivatePort() > 0 : rule;
      assert rule.getProtocol() != null : rule;
      assert rule.getPublicPort() > 0 : rule;
      assert rule.getState() != null : rule;
      assert rule.getVirtualMachineId() > 0 : rule;
      assert rule.getVirtualMachineName() != null : rule;
   }
}
