/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.features.NATClientLiveTest;
import org.jclouds.cloudstack.features.VirtualMachineClientLiveTest;
import org.jclouds.cloudstack.predicates.NetworkPredicates;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;

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
      super.setupContext();
      prefix += "nat";
      try {
         network = find(client.getNetworkClient().listNetworks(), NetworkPredicates.supportsStaticNAT());
         String defaultTemplate = template != null ? template.getImageId() : null;
         vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network,
               defaultTemplateOrPreferredInZone(defaultTemplate, client, network.getZoneId()), client, jobComplete,
               virtualMachineRunning);
         if (vm.getPassword() != null && loginCredentials.getOptionalPassword() == null)
            loginCredentials = loginCredentials.toBuilder().password(vm.getPassword()).build();
      } catch (NoSuchElementException e) {
         networksDisabled = true;
      }
   }

   public void testCreateIPForwardingRule() throws Exception {
      if (networksDisabled)
         return;
      BlockUntilJobCompletesAndReturnResult blocker = new BlockUntilJobCompletesAndReturnResult(client, jobComplete);
      StaticNATVirtualMachineInNetwork fn = new StaticNATVirtualMachineInNetwork(client, reuseOrAssociate, network);
      CreatePortForwardingRulesForIP createPortForwardingRulesForIP = new CreatePortForwardingRulesForIP(client,
            blocker, CacheBuilder.newBuilder().<String, Set<IPForwardingRule>> build(
                  new GetIPForwardingRulesByVirtualMachine(client)));

      // logger
      injector.injectMembers(blocker);
      injector.injectMembers(fn);
      injector.injectMembers(createPortForwardingRulesForIP);

      ip = fn.apply(vm);

      createPortForwardingRulesForIP.apply(ip, ImmutableSet.of(22));

      rule = getOnlyElement(filter(client.getNATClient().getIPForwardingRulesForIPAddress(ip.getId()),
            new Predicate<IPForwardingRule>() {
               @Override
               public boolean apply(IPForwardingRule rule) {
                  return rule != null && rule.getStartPort() == 22;
               }
            }));
      assertEquals(rule.getIPAddressId(), ip.getId());
      assertEquals(rule.getVirtualMachineId(), vm.getId());
      assertEquals(rule.getStartPort(), 22);
      assertEquals(rule.getProtocol(), "tcp");
      checkRule(rule);
      HostAndPort socket = HostAndPort.fromParts(ip.getIPAddress(), 22);
      checkSSH(socket);
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (rule != null) {
         client.getNATClient().deleteIPForwardingRule(rule.getId());
      }
      if (vm != null) {
         jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
      }
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDownContext();
   }

}
