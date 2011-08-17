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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.predicates.NetworkPredicates;
import org.jclouds.net.IPSocket;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code FirewallClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "FirewallClientLiveTest")
public class FirewallClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private VirtualMachine vm;
   private PortForwardingRule rule;
   private Network network;
   private boolean networksDisabled;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();
      prefix += "rule";
      try {
         network = find(client.getNetworkClient().listNetworks(), NetworkPredicates.supportsPortForwarding());
         vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network, client, jobComplete,
                  virtualMachineRunning);
         if (vm.getPassword() != null)
            password = vm.getPassword();
      } catch (NoSuchElementException e) {
         networksDisabled = true;
      }
   }

   public void testCreatePortForwardingRule() throws Exception {
      if (networksDisabled)
         return;
      while (rule == null) {
         ip = reuseOrAssociate.apply(network);
         try {
            AsyncCreateResponse job = client.getFirewallClient().createPortForwardingRuleForVirtualMachine(vm.getId(),
                     ip.getId(), "tcp", 22, 22);
            assert jobComplete.apply(job.getJobId());
            rule = findRuleWithId(job.getId());
         } catch (IllegalStateException e) {
            // very likely an ip conflict, so retry;
         }
      }

      assertEquals(rule.getIPAddressId(), ip.getId());
      assertEquals(rule.getVirtualMachineId(), vm.getId());
      assertEquals(rule.getPublicPort(), 22);
      assertEquals(rule.getProtocol(), "tcp");
      checkRule(rule);
      checkSSH(new IPSocket(ip.getIPAddress(), 22));
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (rule != null) {
         client.getFirewallClient().deletePortForwardingRule(rule.getId());
      }
      if (vm != null) {
         jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
      }
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDown();
   }

   public void testListPortForwardingRules() throws Exception {
      Set<PortForwardingRule> response = client.getFirewallClient().listPortForwardingRules();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (final PortForwardingRule rule : response) {
         PortForwardingRule newDetails = findRuleWithId(rule.getId());
         assertEquals(rule.getId(), newDetails.getId());
         checkRule(rule);
      }
   }

   private PortForwardingRule findRuleWithId(final long id) {
      return find(client.getFirewallClient().listPortForwardingRules(), new Predicate<PortForwardingRule>() {

         @Override
         public boolean apply(PortForwardingRule arg0) {
            return arg0.getId() == id;
         }

      });
   }

   protected void checkRule(PortForwardingRule rule) {
      assertEquals(rule.getId(), findRuleWithId(rule.getId()).getId());
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
