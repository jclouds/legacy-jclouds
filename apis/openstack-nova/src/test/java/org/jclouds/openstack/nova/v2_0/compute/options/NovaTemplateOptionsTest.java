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
package org.jclouds.openstack.nova.v2_0.compute.options;

import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.autoAssignFloatingIp;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.blockOnPort;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.generateKeyPair;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.inboundPorts;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.installPrivateKey;
import static org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions.Builder.securityGroupNames;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests possible uses of NovaTemplateOptions and NovaTemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(testName = "NovaTemplateOptionsTest")
public class NovaTemplateOptionsTest {

   public void testAs() {
      TemplateOptions options = new NovaTemplateOptions();
      assertEquals(options.as(NovaTemplateOptions.class), options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupNamesIterableBadFormat() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.securityGroupNames(ImmutableSet.of("group1", ""));
   }

   @Test
   public void testsecurityGroupNamesIterable() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.securityGroupNames(ImmutableSet.of("group1", "group2"));
      assertEquals(options.getSecurityGroupNames(), ImmutableSet.of("group1", "group2"));

   }

   @Test
   public void testsecurityGroupNamesIterableStatic() {
      NovaTemplateOptions options = securityGroupNames(ImmutableSet.of("group1", "group2"));
      assertEquals(options.getSecurityGroupNames(), ImmutableSet.of("group1", "group2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupNamesVarArgsBadFormat() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.securityGroupNames("mygroup", "");
   }

   @Test
   public void testsecurityGroupNamesVarArgs() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.securityGroupNames("group1", "group2");
      assertEquals(options.getSecurityGroupNames(), ImmutableSet.of("group1", "group2"));

   }

   @Test
   public void testDefaultGroupsVarArgsEmpty() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      assertEquals(options.getSecurityGroupNames(), ImmutableSet.of());
   }

   @Test
   public void testsecurityGroupNamesVarArgsStatic() {
      NovaTemplateOptions options = securityGroupNames("group1", "group2");
      assertEquals(options.getSecurityGroupNames(), ImmutableSet.of("group1", "group2"));
   }

   @Test
   public void testautoAssignFloatingIpDefault() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      assert !options.shouldAutoAssignFloatingIp();
   }

   @Test
   public void testautoAssignFloatingIp() {
      NovaTemplateOptions options = new NovaTemplateOptions().autoAssignFloatingIp(true);
      assert options.shouldAutoAssignFloatingIp();
   }

   @Test
   public void testautoAssignFloatingIpStatic() {
      NovaTemplateOptions options = autoAssignFloatingIp(true);
      assert options.shouldAutoAssignFloatingIp();
   }

   @Test
   public void testGenerateKeyPairDefault() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      assert !options.shouldGenerateKeyPair();
   }

   @Test
   public void testGenerateKeyPair() {
      NovaTemplateOptions options = new NovaTemplateOptions().generateKeyPair(true);
      assert options.shouldGenerateKeyPair();
   }

   @Test
   public void testGenerateKeyPairStatic() {
      NovaTemplateOptions options = generateKeyPair(true);
      assert options.shouldGenerateKeyPair();
   }

   // superclass tests
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinstallPrivateKeyBadFormat() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.installPrivateKey("whompy");
   }

   @Test
   public void testinstallPrivateKey() throws IOException {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      NovaTemplateOptions options = installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testauthorizePublicKeyBadFormat() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.authorizePublicKey("whompy");
   }

   @Test
   public void testauthorizePublicKey() throws IOException {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      NovaTemplateOptions options = authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey(null);
   }

   @Test
   public void testUserData() {
       NovaTemplateOptions options = new NovaTemplateOptions();
       options.userData("test".getBytes());
       assertEquals(new String(options.getUserData()), "test");
   }
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      NovaTemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      NovaTemplateOptions options = new NovaTemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      NovaTemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }
}
