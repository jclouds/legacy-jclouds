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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.hasLoadBalancerService;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.isVirtualNetwork;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.JobResult;
import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.LoadBalancerRule.Algorithm;
import org.jclouds.cloudstack.domain.LoadBalancerRule.State;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.predicates.LoadBalancerRuleActive;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.net.HostAndPort;

/**
 * Tests behavior of {@code LoadBalancerClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "LoadBalancerClientLiveTest")
public class LoadBalancerClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private VirtualMachine vm;
   private LoadBalancerRule rule;
   private Predicate<LoadBalancerRule> loadBalancerRuleActive;
   private Network network;
   private boolean networksDisabled;

   @BeforeGroups(groups = "live")
   public void setupContext() {
      super.setupContext();
      loadBalancerRuleActive = retry(new LoadBalancerRuleActive(client), 60, 1, 1, SECONDS);
      prefix += "rule";
      try {
         network = find(client.getNetworkClient().listNetworks(),
               Predicates.and(hasLoadBalancerService(), isVirtualNetwork(),
                  new Predicate<Network>() {
                     @Override
                     public boolean apply(Network network) {
                        return network.isDefault()
                           && !network.isSecurityGroupEnabled()
                           && !network.isSystem()
                           && network.getAccount().equals(user.getName());
                     }
                  }));
      } catch (NoSuchElementException e) {
         networksDisabled = true;
      }
   }

   public void testCreateVm() {
      if (networksDisabled)
         return;
      String defaultTemplate = template != null ? template.getImageId() : null;
      vm = VirtualMachineClientLiveTest.createVirtualMachineInNetwork(network,
            defaultTemplateOrPreferredInZone(defaultTemplate, client, network.getZoneId()),
            client, jobComplete, virtualMachineRunning);
      if (vm.getPassword() != null && loginCredentials.getOptionalPassword() == null)
         loginCredentials = loginCredentials.toBuilder().password(vm.getPassword()).build();
   }

   @Test(dependsOnMethods = "testCreateVm")
   public void testCreateLoadBalancerRule() throws Exception {
      if (networksDisabled)
         return;
      int attempts = 0;
      while (rule == null && attempts < 10) {
         ip = reuseOrAssociate.apply(network);
         try {
            String jobId = client.getLoadBalancerClient().createLoadBalancerRuleForPublicIP(ip.getId(),
                  Algorithm.LEASTCONN, prefix, 22, 22);
            assertTrue(jobComplete.apply(jobId));
            AsyncJob<LoadBalancerRule> asyncJob = client.getAsyncJobClient().getAsyncJob(jobId);
            LoadBalancerRule result = asyncJob.getResult();
            rule = result;
         } catch (IllegalStateException e) {
            // very likely an ip conflict, so retry;
            attempts++;
         }
      }
      assertNotNull(rule, "Failed to get a load balancer rule after " + attempts + " attempts");
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
      String jobId = client.getLoadBalancerClient().assignVirtualMachinesToLoadBalancerRule(rule.getId(), vm.getId());
      assertTrue(jobComplete.apply(jobId));
      AsyncJob<JobResult> result = client.getAsyncJobClient().getAsyncJob(jobId);
      assertTrue(result.hasSucceed());
      Set<VirtualMachine> machines = client.getLoadBalancerClient().listVirtualMachinesAssignedToLoadBalancerRule(
            rule.getId());
      assertEquals(machines.size(), 1);
      assertTrue(loadBalancerRuleActive.apply(rule), rule.toString());
   }

   @Test(dependsOnMethods = "testAssignToLoadBalancerRule")
   public void testCanSshInThroughNewLoadBalancerRule() throws Exception {
      loopAndCheckSSH();
   }

   // note that when in LB mode, there's a chance you'll have a connection
   // failure
   private void loopAndCheckSSH() throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            checkSSH(HostAndPort.fromParts(ip.getIPAddress(), 22));
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
      assertTrue(jobComplete.apply(client.getLoadBalancerClient().removeVirtualMachinesFromLoadBalancerRule(
            rule.getId(), vm.getId())));
      assertEquals(client.getLoadBalancerClient().listVirtualMachinesAssignedToLoadBalancerRule(rule.getId()).size(), 0);
      assertEquals(rule.getState(), State.ADD);
      checkSSH(HostAndPort.fromParts(ip.getIPAddress(), 22));
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (rule != null) {
         assertTrue(jobComplete.apply(client.getLoadBalancerClient().deleteLoadBalancerRule(rule.getId())));
      }
      if (vm != null) {
         assertTrue(jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId())));
      }
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDownContext();
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

   private LoadBalancerRule findRuleWithId(final String id) {
      return find(client.getLoadBalancerClient().listLoadBalancerRules(), new Predicate<LoadBalancerRule>() {

         @Override
         public boolean apply(LoadBalancerRule arg0) {
            return arg0.getId() == id;
         }

      });
   }

   protected void checkRule(LoadBalancerRule rule) {
      assertEquals(rule.getId(), findRuleWithId(rule.getId()).getId());
      assert rule.getId() != null : rule;
      assert rule.getAccount() != null : rule;
      assert rule.getAlgorithm() != null : rule;
      assert rule.getPrivatePort() > 0 : rule;
      assert rule.getPublicPort() > 0 : rule;
      assert rule.getDomain() != null : rule;
      assert rule.getDomainId() != null : rule;
      assert rule.getState() != null : rule;
      assert rule.getName() != null : rule;
      assert rule.getPublicIP() != null : rule;
      assert rule.getPublicIPId() != null : rule;
   }
}
