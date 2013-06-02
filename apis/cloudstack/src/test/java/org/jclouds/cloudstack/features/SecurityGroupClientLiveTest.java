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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.NoSuchElementException;

import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.jclouds.util.Strings2;
import org.testng.SkipException;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;

/**
 * Tests behavior of {@code SecurityGroupClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "SecurityGroupClientLiveTest")
public class SecurityGroupClientLiveTest extends BaseCloudStackClientLiveTest {
   public SecurityGroupClientLiveTest() {
      prefix += "2";
   }

   private SecurityGroup group;
   private boolean securityGroupsSupported;
   private VirtualMachine vm;
   private Zone zone;

   @Test
   public void testCreateDestroySecurityGroup() {
      try {
         zone = Iterables.find(client.getZoneClient().listZones(), new Predicate<Zone>() {

            @Override
            public boolean apply(Zone arg0) {
               return arg0.isSecurityGroupsEnabled();
            }

         });
         securityGroupsSupported = true;
         for (SecurityGroup securityGroup : client.getSecurityGroupClient().listSecurityGroups(
               ListSecurityGroupsOptions.Builder.named(prefix))) {
            for (IngressRule rule : securityGroup.getIngressRules())
               assertTrue(jobComplete.apply(client.getSecurityGroupClient().revokeIngressRule(rule.getId())), rule.toString());
            client.getSecurityGroupClient().deleteSecurityGroup(securityGroup.getId());
         }
         group = client.getSecurityGroupClient().createSecurityGroup(prefix);
         assertEquals(group.getName(), prefix);
         checkGroup(group);
         try {
            client.getSecurityGroupClient().createSecurityGroup(prefix);
            fail("Expected IllegalStateException");
         } catch (IllegalStateException e) {

         }
      } catch (NoSuchElementException e) {
         e.printStackTrace();
      }
   }

   public static String getCurrentCIDR() throws IOException {
      URL url = new URL("http://checkip.amazonaws.com/");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.connect();
      return Strings2.toStringAndClose(connection.getInputStream()).trim() + "/32";
   }
   
   protected void skipIfSecurityGroupsNotSupported() {
      if (!securityGroupsSupported) {
         throw new SkipException("Test cannot run without security groups supported in a zone");
      }
   }
   
   @Test(dependsOnMethods = "testCreateDestroySecurityGroup")
   public void testCreateIngress() throws Exception {
      skipIfSecurityGroupsNotSupported();
      String cidr = getCurrentCIDR();
      ImmutableSet<String> cidrs = ImmutableSet.of(cidr);
      assertTrue(jobComplete.apply(client.getSecurityGroupClient().authorizeIngressICMPToCIDRs(group.getId(), 0, 8, cidrs)), group.toString());
      assertTrue(jobComplete.apply(client.getSecurityGroupClient().authorizeIngressPortsToCIDRs(group.getId(), "TCP", 22,
            22, cidrs)), group.toString());

      AccountInDomainOptions.Builder.accountInDomain(group.getAccount(), group.getDomainId());

      // replace with get once bug is fixed where getGroup returns only one
      // ingress rule
      group = Iterables.find(client.getSecurityGroupClient().listSecurityGroups(), new Predicate<SecurityGroup>() {

         @Override
         public boolean apply(SecurityGroup input) {
            return input.getId() == group.getId();
         }

      });

      IngressRule ICMPPingRule = Iterables.find(group.getIngressRules(), new Predicate<IngressRule>() {

         @Override
         public boolean apply(IngressRule input) {
            return "icmp".equals(input.getProtocol());
         }

      });

      assert ICMPPingRule.getId() != null : ICMPPingRule;
      assert "icmp".equals(ICMPPingRule.getProtocol()) : ICMPPingRule;
      assert ICMPPingRule.getStartPort() == -1 : ICMPPingRule;
      assert ICMPPingRule.getEndPort() == -1 : ICMPPingRule;
      assert ICMPPingRule.getICMPCode() == 0 : ICMPPingRule;
      assert ICMPPingRule.getICMPType() == 8 : ICMPPingRule;
      assert ICMPPingRule.getAccount() == null : ICMPPingRule;
      assert ICMPPingRule.getSecurityGroupName() == null : ICMPPingRule;
      assert cidr.equals(ICMPPingRule.getCIDR()) : ICMPPingRule;

      IngressRule SSHRule = Iterables.find(group.getIngressRules(), new Predicate<IngressRule>() {

         @Override
         public boolean apply(IngressRule input) {
            return "tcp".equals(input.getProtocol());
         }

      });

      assert SSHRule.getId() != null : SSHRule;
      assert "tcp".equals(SSHRule.getProtocol()) : SSHRule;
      assert SSHRule.getStartPort() == 22 : SSHRule;
      assert SSHRule.getEndPort() == 22 : SSHRule;
      assert SSHRule.getICMPCode() == -1 : SSHRule;
      assert SSHRule.getICMPType() == -1 : SSHRule;
      assert SSHRule.getAccount() == null : SSHRule;
      assert SSHRule.getSecurityGroupName() == null : SSHRule;
      assert cidr.equals(SSHRule.getCIDR()) : SSHRule;

   }

   public void testListSecurityGroup() throws Exception {
      skipIfSecurityGroupsNotSupported();
      for (SecurityGroup securityGroup : client.getSecurityGroupClient().listSecurityGroups())
         checkGroup(securityGroup);
   }

   @Test(dependsOnMethods = "testCreateIngress")
   public void testCreateVMInSecurityGroup() throws Exception {
      skipIfSecurityGroupsNotSupported();
      String defaultTemplate = template != null ? template.getImageId() : null;
      vm = VirtualMachineClientLiveTest.createVirtualMachineWithSecurityGroupInZone(zone.getId(),
            defaultTemplateOrPreferredInZone(defaultTemplate, client, zone.getId()), group.getId(), client,
            jobComplete, virtualMachineRunning);
      if (vm.getPassword() != null && loginCredentials.getOptionalPassword() == null)
         loginCredentials = loginCredentials.toBuilder().password(vm.getPassword()).build();
      // ingress port 22
      checkSSH(HostAndPort.fromParts(vm.getIPAddress(), 22));
      // ingress icmp disabled as this is platform dependent and may actually
      // just try tcp port 7
      // assert InetAddress.getByName(vm.getIPAddress()).isReachable(1000) : vm;
   }

   protected void checkGroup(SecurityGroup group) {
      // http://bugs.cloud.com/show_bug.cgi?id=8968
      if (group.getIngressRules().size() <= 1) {
         assertEquals(group, client.getSecurityGroupClient().getSecurityGroup(group.getId()));
         assertEquals(group, client.getSecurityGroupClient().getSecurityGroupByName(group.getName()));
      }
      assert group.getId() != null : group;
      assert group.getName() != null : group;
      assert group.getAccount() != null : group;
      assert group.getDomain() != null : group;
      assert group.getDomainId() != null : group;
      assert group.getIngressRules() != null : group;
   }

   @Test
   public void testCreateVMWithoutSecurityGroupAssignsDefault() throws Exception {
      skipIfSecurityGroupsNotSupported();
      String defaultTemplate = template != null ? template.getImageId() : null;
      VirtualMachine newVm = VirtualMachineClientLiveTest.createVirtualMachineWithOptionsInZone(DeployVirtualMachineOptions.NONE,
            zone.getId(), defaultTemplateOrPreferredInZone(defaultTemplate, client, zone.getId()), client,
            jobComplete, virtualMachineRunning);
      try {
         VirtualMachine runningVm = client.getVirtualMachineClient().getVirtualMachine(newVm.getId());
         assertEquals(1, runningVm.getSecurityGroups().size());
         assertEquals(Iterables.getOnlyElement(runningVm.getSecurityGroups()).getName(), "default");
      } finally {
         assertTrue(jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(newVm.getId())));
      }
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (vm != null) {
         assertTrue(jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId())));
      }
      if (group != null) {
         for (IngressRule rule : group.getIngressRules())
            assertTrue(jobComplete.apply(client.getSecurityGroupClient().revokeIngressRule(rule.getId())), rule.toString());
         client.getSecurityGroupClient().deleteSecurityGroup(group.getId());
         assertEquals(client.getSecurityGroupClient().getSecurityGroup(group.getId()), null);
      }
      super.tearDownContext();
   }

}
