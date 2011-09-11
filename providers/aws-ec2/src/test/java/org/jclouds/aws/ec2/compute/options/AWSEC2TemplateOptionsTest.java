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
package org.jclouds.aws.ec2.compute.options;

import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.blockOnPort;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.enableMonitoring;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.inboundPorts;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.installPrivateKey;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.keyPair;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.noKeyPair;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.securityGroupIds;
import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.securityGroups;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests possible uses of AWSEC2TemplateOptions and
 * AWSEC2TemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class AWSEC2TemplateOptionsTest {

   public void testAs() {
      TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.as(AWSEC2TemplateOptions.class), options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupIdsIterableBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroupIds(ImmutableSet.of("groupId1", ""));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupIdsIterableEmptyNotOk() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroupIds(ImmutableSet.<String> of());
   }

   @Test
   public void testsecurityGroupIdsIterable() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroupIds(ImmutableSet.of("groupId1", "groupId2"));
      assertEquals(options.getGroupIds(), ImmutableSet.of("groupId1", "groupId2"));

   }

   @Test
   public void testsecurityGroupIdsIterableStatic() {
      AWSEC2TemplateOptions options = securityGroupIds(ImmutableSet.of("groupId1", "groupId2"));
      assertEquals(options.getGroupIds(), ImmutableSet.of("groupId1", "groupId2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupIdsVarArgsBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroupIds("mygroupId", "");
   }

   @Test
   public void testsecurityGroupIdsVarArgs() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroupIds("groupId1", "groupId2");
      assertEquals(options.getGroupIds(), ImmutableSet.of("groupId1", "groupId2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupIdsVarArgsEmptyNotOk() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroupIds();
   }

   @Test
   public void testDefaultGroupIdsVarArgsEmpty() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getGroupIds(), ImmutableSet.of());
   }

   @Test
   public void testsecurityGroupIdsVarArgsStatic() {
      AWSEC2TemplateOptions options = securityGroupIds("groupId1", "groupId2");
      assertEquals(options.getGroupIds(), ImmutableSet.of("groupId1", "groupId2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsIterableBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroups(ImmutableSet.of("group1", ""));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsIterableEmptyNotOk() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroups(ImmutableSet.<String> of());
   }

   @Test
   public void testsecurityGroupsIterable() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroups(ImmutableSet.of("group1", "group2"));
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));

   }

   @Test
   public void testsecurityGroupsIterableStatic() {
      AWSEC2TemplateOptions options = securityGroups(ImmutableSet.of("group1", "group2"));
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsVarArgsBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroups("mygroup", "");
   }

   @Test
   public void testsecurityGroupsVarArgs() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroups("group1", "group2");
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testsecurityGroupsVarArgsEmptyNotOk() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.securityGroups();
   }

   @Test
   public void testDefaultGroupsVarArgsEmpty() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getGroups(), ImmutableSet.of());
   }

   @Test
   public void testsecurityGroupsVarArgsStatic() {
      AWSEC2TemplateOptions options = securityGroups("group1", "group2");
      assertEquals(options.getGroups(), ImmutableSet.of("group1", "group2"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testkeyPairBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.keyPair("");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testkeyPairAndNoKeyPair() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.keyPair("mykeypair");
      options.noKeyPair();
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNoKeyPairAndKeyPair() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.noKeyPair();
      options.keyPair("mykeypair");
   }

   @Test
   public void testkeyPair() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.keyPair("mykeypair");
      assertEquals(options.getKeyPair(), "mykeypair");
   }

   @Test
   public void testNullkeyPair() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getKeyPair(), null);
   }

   @Test
   public void testkeyPairStatic() {
      AWSEC2TemplateOptions options = keyPair("mykeypair");
      assertEquals(options.getKeyPair(), "mykeypair");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testkeyPairNPE() {
      keyPair(null);
   }

   @Test
   public void testnoKeyPair() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.noKeyPair();
      assertEquals(options.getKeyPair(), null);
      assert !options.shouldAutomaticallyCreateKeyPair();
   }

   @Test
   public void testFalsenoKeyPair() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getKeyPair(), null);
      assert options.shouldAutomaticallyCreateKeyPair();
   }

   @Test
   public void testnoKeyPairStatic() {
      AWSEC2TemplateOptions options = noKeyPair();
      assertEquals(options.getKeyPair(), null);
      assert !options.shouldAutomaticallyCreateKeyPair();
   }

   @Test
   public void testMonitoringEnabledDefault() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assert !options.isMonitoringEnabled();
   }

   @Test
   public void testMonitoringEnabled() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.enableMonitoring();
      assert options.isMonitoringEnabled();
   }

   @Test
   public void testEnableMonitoringStatic() {
      AWSEC2TemplateOptions options = enableMonitoring();
      assertEquals(options.getKeyPair(), null);
      assert options.isMonitoringEnabled();
   }

   // superclass tests
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinstallPrivateKeyBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.installPrivateKey("whompy");
   }

   @Test
   public void testinstallPrivateKey() throws IOException {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      AWSEC2TemplateOptions options = installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testauthorizePublicKeyBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.authorizePublicKey("whompy");
   }

   @Test
   public void testauthorizePublicKey() throws IOException {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      AWSEC2TemplateOptions options = authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      AWSEC2TemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      AWSEC2TemplateOptions options = new AWSEC2TemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      AWSEC2TemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }
}
