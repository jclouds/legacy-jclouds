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
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.options.ListIPForwardingRulesOptions;
import org.jclouds.cloudstack.predicates.NetworkPredicates;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NATClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "NATClientLiveTest")
public class NATClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private VirtualMachine vm;
   private IPForwardingRule rule;
   private Network network;
   private boolean networksDisabled;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();
      prefix += "nat";
      try {
         network = find(client.getNetworkClient().listNetworks(), NetworkPredicates.supportsStaticNAT());
         vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network, client, jobComplete,
               virtualMachineRunning);
         if (vm.getPassword() != null)
            password = vm.getPassword();
      } catch (NoSuchElementException e) {
         networksDisabled = true;
      }
   }

   public void testCreateIPForwardingRule() throws Exception {
      if (networksDisabled)
         return;
      for (ip = reuseOrAssociate.apply(network); (!ip.isStaticNAT() || ip.getVirtualMachineId() != vm.getId()); ip = reuseOrAssociate
            .apply(network)) {
         // check to see if someone already grabbed this ip
         if (ip.getVirtualMachineId() > 0 && ip.getVirtualMachineId() != vm.getId())
            continue;
         try {
            client.getNATClient().enableStaticNATForVirtualMachine(vm.getId(), ip.getId());
            ip = client.getAddressClient().getPublicIPAddress(ip.getId());
            if (ip.isStaticNAT() && ip.getVirtualMachineId() == vm.getId())
               break;
         } catch (IllegalStateException e) {
            // very likely an ip conflict, so retry;
         }
      }

      AsyncCreateResponse job = client.getNATClient().createIPForwardingRule(ip.getId(), "tcp", 22);
      assert jobComplete.apply(job.getJobId());
      rule = client.getNATClient().getIPForwardingRule(job.getId());
      assertEquals(rule.getIPAddressId(), ip.getId());
      assertEquals(rule.getVirtualMachineId(), vm.getId());
      assertEquals(rule.getStartPort(), 22);
      assertEquals(rule.getProtocol(), "tcp");
      checkRule(rule);
      IPSocket socket = new IPSocket(ip.getIPAddress(), 22);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials("root", password));
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
         client.getNATClient().deleteIPForwardingRule(rule.getId());
      }
      if (vm != null) {
         jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
      }
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDown();
   }

   @Test(enabled = false)
   // takes too long
   public void testListIPForwardingRules() throws Exception {
      Set<IPForwardingRule> response = client.getNATClient().listIPForwardingRules();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (IPForwardingRule rule : response) {
         IPForwardingRule newDetails = getOnlyElement(client.getNATClient().listIPForwardingRules(
               ListIPForwardingRulesOptions.Builder.id(rule.getId())));
         assertEquals(rule.getId(), newDetails.getId());
         checkRule(rule);
      }
   }

   protected void checkRule(IPForwardingRule rule) {
      assertEquals(rule.getId(), client.getNATClient().getIPForwardingRule(rule.getId()).getId());
      assert rule.getId() > 0 : rule;
      assert rule.getIPAddress() != null : rule;
      assert rule.getIPAddressId() > 0 : rule;
      assert rule.getStartPort() > 0 : rule;
      assert rule.getProtocol() != null : rule;
      assert rule.getEndPort() > 0 : rule;
      assert rule.getState() != null : rule;
      assert rule.getVirtualMachineId() > 0 : rule;
      assert rule.getVirtualMachineName() != null : rule;

   }
}
