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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.NoSuchElementException;

import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.jclouds.net.IPSocket;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code SecurityGroupClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "SecurityGroupClientLiveTest")
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
               assert this.jobComplete.apply(client.getSecurityGroupClient().revokeIngressRule(rule.getId())) : rule;
            client.getSecurityGroupClient().deleteSecurityGroup(securityGroup.getId());
         }
         group = client.getSecurityGroupClient().createSecurityGroup(prefix);
         assertEquals(group.getName(), prefix);
         checkGroup(group);
         try {
            client.getSecurityGroupClient().createSecurityGroup(prefix);
            assert false;
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
      // http://bugs.cloud.com/show_bug.cgi?id=8969
      // return Strings2.toStringAndClose(connection.getInputStream()).trim()+"/32";
      return Strings2.toStringAndClose(connection.getInputStream()).trim();
   }

   @Test(dependsOnMethods = "testCreateDestroySecurityGroup")
   public void testCreateIngress() throws Exception {
      if (!securityGroupsSupported)
         return;
      String cidr = getCurrentCIDR();
      ImmutableSet<String> cidrs = ImmutableSet.of(cidr);
      assert jobComplete.apply(client.getSecurityGroupClient().authorizeIngressICMPToCIDRs(group.getId(), 0, 8, cidrs)) : group;
      assert jobComplete.apply(client.getSecurityGroupClient().authorizeIngressPortsToCIDRs(group.getId(), "TCP", 22,
               22, cidrs)) : group;

      AccountInDomainOptions.Builder.accountInDomain(group.getAccount(), group.getDomainId());

      // replace with get once bug is fixed where getGroup returns only one ingress rule
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

      assert ICMPPingRule.getId() > 0 : ICMPPingRule;
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

      assert SSHRule.getId() > 0 : SSHRule;
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
      if (!securityGroupsSupported)
         return;
      for (SecurityGroup securityGroup : client.getSecurityGroupClient().listSecurityGroups())
         checkGroup(securityGroup);
   }

   @Test(dependsOnMethods = "testCreateIngress")
   public void testCreateVMInSecurityGroup() throws Exception {
      if (!securityGroupsSupported)
         return;
      vm = VirtualMachineClientLiveTest.createVirtualMachineWithSecurityGroupInZone(zone.getId(), group.getId(),
               client, jobComplete, virtualMachineRunning);
      if (vm.getPassword() != null)
         password = vm.getPassword();
      // ingress port 22
      checkSSH(new IPSocket(vm.getIPAddress(), 22));
      // ingress icmp disabled as this is platform dependent and may actually just try tcp port 7
      // assert InetAddress.getByName(vm.getIPAddress()).isReachable(1000) : vm;
   }

   protected void checkGroup(SecurityGroup group) {
      // http://bugs.cloud.com/show_bug.cgi?id=8968
      if (group.getIngressRules().size() <= 1)
         assertEquals(group, client.getSecurityGroupClient().getSecurityGroup(group.getId()));
      assert group.getId() > 0 : group;
      assert group.getName() != null : group;
      assert group.getAccount() != null : group;
      assert group.getDomain() != null : group;
      assert group.getDomainId() >= 0 : group;
      assert group.getIngressRules() != null : group;
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (vm != null) {
         assert jobComplete.apply(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
      }
      if (group != null) {
         for (IngressRule rule : group.getIngressRules())
            assert this.jobComplete.apply(client.getSecurityGroupClient().revokeIngressRule(rule.getId())) : rule;
         client.getSecurityGroupClient().deleteSecurityGroup(group.getId());
         assertEquals(client.getSecurityGroupClient().getSecurityGroup(group.getId()), null);
      }
      super.tearDown();
   }

}
