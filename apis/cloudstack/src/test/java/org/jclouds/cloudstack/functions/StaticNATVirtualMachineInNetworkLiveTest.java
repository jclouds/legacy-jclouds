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
package org.jclouds.cloudstack.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.Predicate;
import org.jclouds.cloudstack.compute.config.CloudStackComputeServiceContextModule.GetIPForwardingRulesByVirtualMachine;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.features.NATClientLiveTest;
import org.jclouds.cloudstack.features.VirtualMachineClientLiveTest;
import org.jclouds.cloudstack.predicates.NetworkPredicates;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.cache.CacheBuilder;

import javax.annotation.Nullable;

/**
 * Tests behavior of {@code StaticNATVirtualMachineInNetwork}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "StaticNATVirtualMachineInNetworkLiveTest")
public class StaticNATVirtualMachineInNetworkLiveTest extends NATClientLiveTest {
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
         Long defaultTemplate = (imageId != null && !"".equals(imageId)) ? new Long(imageId) : null;
         vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network,
               defaultTemplateOrPreferredInZone(defaultTemplate, client, network.getZoneId()), client, jobComplete,
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
      ip = new StaticNATVirtualMachineInNetwork(client, reuseOrAssociate, jobComplete, CacheBuilder.newBuilder()
            .<Long, Set<IPForwardingRule>>build(new GetIPForwardingRulesByVirtualMachine(client)), network).apply(vm);

      rule = getOnlyElement(filter(client.getNATClient().getIPForwardingRulesForIPAddress(ip.getId()),
         new Predicate<IPForwardingRule>() {
            @Override
            public boolean apply(@Nullable IPForwardingRule rule) {
               return rule != null && rule.getStartPort() == 22;
            }
         }));
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

}
