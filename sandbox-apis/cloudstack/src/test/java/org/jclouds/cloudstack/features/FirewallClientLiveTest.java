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
 * Unless required by applicable law or agred to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Se the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code FirewallClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "FirewallClientLiveTest")
public class FirewallClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private VirtualMachine vm;
   private PortForwardingRule rule;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();
      prefix += "rule";
      ip = AddressClientLiveTest.createPublicIPAddress(client, jobComplete);
      vm = VirtualMachineClientLiveTest.createVirtualMachine(client, jobComplete, virtualMachineRunning);
   }

   public void testCreatePortForwardingRule() throws Exception {
      AsyncCreateResponse job = client.getFirewallClient().createPortForwardingRuleForVirtualMachine(vm.getId(),
               ip.getId(), "tcp", 22, 22);
      assert jobComplete.apply(job.getJobId());
      rule = findRuleWithId(job.getId());
      assertEquals(rule.getIPAddressId(), ip.getId());
      assertEquals(rule.getVirtualMachineId(), vm.getId());
      assertEquals(rule.getPublicPort(), 22);
      assertEquals(rule.getProtocol(), "tcp");
      checkRule(rule);
      IPSocket socket = new IPSocket(ip.getIPAddress(), 22);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials("root", "password"));
      try {
         client.connect();
         ExecResponse exec = client.exec("echo hello");
         assertEquals(exec.getOutput().trim(), "hello");
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (rule != null) {
         client.getFirewallClient().deletePortForwardingRule(rule.getId());
      }
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      if (vm != null) {
         assert jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
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
