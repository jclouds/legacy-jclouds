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
package org.jclouds.ec2.compute.options;

import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.blockOnPort;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.inboundPorts;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.installPrivateKey;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.keyPair;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.noKeyPair;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.securityGroups;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests possible uses of EC2TemplateOptions and EC2TemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class EC2TemplateOptionsTest {

   public void testAs() {
      TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.as(EC2TemplateOptions.class), options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsIterableBadFormat() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.securityGroups(ImmutableSet.of("group1", ""));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsIterableEmptyNotOk() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.securityGroups(ImmutableSet.<String> of());
   }

   @Test
   public void testsecurityGroupsIterable() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.securityGroups(ImmutableSet.of("group1", "group2"));
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));

   }

   @Test
   public void testsecurityGroupsIterableStatic() {
      EC2TemplateOptions options = securityGroups(ImmutableSet.of("group1", "group2"));
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsVarArgsBadFormat() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.securityGroups("mygroup", "");
   }

   @Test
   public void testsecurityGroupsVarArgs() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.securityGroups("group1", "group2");
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsVarArgsEmptyNotOk() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.securityGroups();
   }

   @Test
   public void testDefaultGroupsVarArgsEmpty() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.getGroups(), ImmutableSet.of());
   }

   @Test
   public void testsecurityGroupsVarArgsStatic() {
      EC2TemplateOptions options = securityGroups("group1", "group2");
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testkeyPairBadFormat() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.keyPair("");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testkeyPairAndNoKeyPair() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.keyPair("mykeypair");
      options.noKeyPair();
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNoKeyPairAndKeyPair() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.noKeyPair();
      options.keyPair("mykeypair");
   }

   @Test
   public void testkeyPair() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.keyPair("mykeypair");
      assertEquals(options.getKeyPair(), "mykeypair");
   }

   @Test
   public void testNullkeyPair() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.getKeyPair(), null);
   }

   @Test
   public void testkeyPairStatic() {
      EC2TemplateOptions options = keyPair("mykeypair");
      assertEquals(options.getKeyPair(), "mykeypair");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testkeyPairNPE() {
      keyPair(null);
   }

   @Test
   public void testnoKeyPair() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.noKeyPair();
      assertEquals(options.getKeyPair(), null);
      assert !options.shouldAutomaticallyCreateKeyPair();
   }

   @Test
   public void testFalsenoKeyPair() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.getKeyPair(), null);
      assert options.shouldAutomaticallyCreateKeyPair();
   }

   @Test
   public void testnoKeyPairStatic() {
      EC2TemplateOptions options = noKeyPair();
      assertEquals(options.getKeyPair(), null);
      assert !options.shouldAutomaticallyCreateKeyPair();
   }

   // superclass tests
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinstallPrivateKeyBadFormat() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.installPrivateKey("whompy");
   }

   @Test
   public void testinstallPrivateKey() throws IOException {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      EC2TemplateOptions options = installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testauthorizePublicKeyBadFormat() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.authorizePublicKey("whompy");
   }

   @Test
   public void testauthorizePublicKey() throws IOException {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      EC2TemplateOptions options = authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      EC2TemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      EC2TemplateOptions options = new EC2TemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      EC2TemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }
}
