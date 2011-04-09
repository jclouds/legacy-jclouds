/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.LoadBalancerRule.Algorithm;
import org.jclouds.cloudstack.domain.LoadBalancerRule.State;
import org.jclouds.cloudstack.predicates.LoadBalancerRuleActive;
import org.jclouds.cloudstack.predicates.NetworkPredicates;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code LoadBalancerClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "LoadBalancerClientLiveTest")
public class LoadBalancerClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private VirtualMachine vm;
   private LoadBalancerRule rule;
   private RetryablePredicate<LoadBalancerRule> loadBalancerRuleActive;
   private Network network;
   private boolean networksDisabled;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();

      loadBalancerRuleActive = new RetryablePredicate<LoadBalancerRule>(new LoadBalancerRuleActive(client), 60, 1, 1,
               TimeUnit.SECONDS);
      prefix += "rule";
      try {
         network = find(client.getNetworkClient().listNetworks(), NetworkPredicates.hasLoadBalancerService());
         vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network, client, jobComplete,
                  virtualMachineRunning);
         if (vm.getPassword() != null)
            password = vm.getPassword();
      } catch (NoSuchElementException e) {
         networksDisabled = true;
      }
   }

   public void testCreateLoadBalancerRule() throws Exception {
      if (networksDisabled)
         return;
      while (rule == null) {
         ip = reuseOrAssociate.apply(network);
         try {
            rule = client.getLoadBalancerClient().createLoadBalancerRuleForPublicIP(ip.getId(), Algorithm.LEASTCONN,
                     prefix, 22, 22);
         } catch (IllegalStateException e) {
            // very likely an ip conflict, so retry;
         }
      }
      assert (rule.getPublicIPId() == ip.getId()) : rule;
      assertEquals(rule.getPublicPort(), 22);
      assertEquals(rule.getPrivatePort(), 22);
      assertEquals(rule.getAlgorithm(), Algorithm.LEASTCONN);
      assertEquals(rule.getName(), prefix);
      assertEquals(rule.getState(), State.ADD);
      assertEquals(client.getLoadBalancerClient().listVirtualMachinesAssignedToLoadBalancerRule(rule.getId()).size(), 0);
      checkRule(rule);

   }

   @Test(dependsOnMethods = "testCreateLoadBalancerRule")
   public void testAssignToLoadBalancerRule() throws Exception {
      if (networksDisabled)
         return;
      assert jobComplete.apply(client.getLoadBalancerClient().assignVirtualMachinesToLoadBalancerRule(rule.getId(),
               vm.getId()));
      assertEquals(client.getLoadBalancerClient().listVirtualMachinesAssignedToLoadBalancerRule(rule.getId()).size(), 1);
      assert loadBalancerRuleActive.apply(rule) : rule;
      loopAndCheckSSH();
   }

   // note that when in LB mode, there's a chance you'll have a connection failure
   private void loopAndCheckSSH() throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            checkSSH(new IPSocket(ip.getIPAddress(), 22));
            return;
         } catch (SshException e) {
            e.printStackTrace();
            try {
               Thread.sleep(10 * 1000);
            } catch (InterruptedException e1) {
            }
            continue;
         }
      }
   }

   @Test(dependsOnMethods = "testAssignToLoadBalancerRule", expectedExceptions = SshException.class)
   public void testRemoveFromLoadBalancerRule() throws Exception {
      if (networksDisabled)
         throw new SshException();
      assert jobComplete.apply(client.getLoadBalancerClient().removeVirtualMachinesFromLoadBalancerRule(rule.getId(),
               vm.getId()));
      assertEquals(client.getLoadBalancerClient().listVirtualMachinesAssignedToLoadBalancerRule(rule.getId()).size(), 0);
      assertEquals(rule.getState(), State.ADD);
      checkSSH(new IPSocket(ip.getIPAddress(), 22));
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (rule != null) {
         assert jobComplete.apply(client.getLoadBalancerClient().deleteLoadBalancerRule(rule.getId()));
      }
      if (vm != null) {
         assert jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
      }
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDown();
   }

   public void testListLoadBalancerRules() throws Exception {
      Set<LoadBalancerRule> response = client.getLoadBalancerClient().listLoadBalancerRules();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (LoadBalancerRule rule : response) {
         LoadBalancerRule newDetails = findRuleWithId(rule.getId());
         assertEquals(rule.getId(), newDetails.getId());
         checkRule(rule);
      }
   }

   private LoadBalancerRule findRuleWithId(final long id) {
      return find(client.getLoadBalancerClient().listLoadBalancerRules(), new Predicate<LoadBalancerRule>() {

         @Override
         public boolean apply(LoadBalancerRule arg0) {
            return arg0.getId() == id;
         }

      });
   }

   protected void checkRule(LoadBalancerRule rule) {
      assertEquals(rule.getId(), findRuleWithId(rule.getId()).getId());
      assert rule.getId() > 0 : rule;
      assert rule.getAccount() != null : rule;
      assert rule.getAlgorithm() != null : rule;
      assert rule.getPrivatePort() > 0 : rule;
      assert rule.getPublicPort() > 0 : rule;
      assert rule.getDomain() != null : rule;
      assert rule.getDomainId() > 0 : rule;
      assert rule.getState() != null : rule;
      assert rule.getName() != null : rule;
      assert rule.getPublicIP() != null : rule;
      assert rule.getPublicIPId() > 0 : rule;
   }
}
