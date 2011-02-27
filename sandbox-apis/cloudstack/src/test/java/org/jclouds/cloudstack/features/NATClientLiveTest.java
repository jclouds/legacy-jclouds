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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.options.ListIPForwardingRulesOptions;
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
@Test(groups = "live", sequential = true, testName = "NATClientLiveTest")
public class NATClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private VirtualMachine vm;
   private IPForwardingRule rule;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();
      prefix += "nat";
      ip = AddressClientLiveTest.createPublicIPAddress(client, jobComplete);
      vm = VirtualMachineClientLiveTest.createVirtualMachine(client, jobComplete, virtualMachineRunning);
      if (vm.getPassword() != null)
         password = vm.getPassword();
   }

   public void testCreateIPForwardingRule() throws Exception {

      assert !ip.isStaticNAT();
      client.getNATClient().enableStaticNATForVirtualMachine(vm.getId(), ip.getId());
      ip = client.getAddressClient().getPublicIPAddress(ip.getId());
      assert ip.isStaticNAT();

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
      SshClient client = sshFactory.create(socket, new Credentials("root",password));
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
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      if (vm != null) {
         assert jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
      }
      super.tearDown();
   }

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
