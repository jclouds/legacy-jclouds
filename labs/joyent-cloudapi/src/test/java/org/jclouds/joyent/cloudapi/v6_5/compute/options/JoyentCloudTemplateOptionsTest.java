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
package org.jclouds.joyent.cloudapi.v6_5.compute.options;

import static org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions.Builder.blockOnPort;
import static org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions.Builder.generateKey;
import static org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions.Builder.inboundPorts;
import static org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions.Builder.installPrivateKey;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of JoyentCloudTemplateOptions and JoyentCloudTemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(testName = "JoyentCloudTemplateOptionsTest")
public class JoyentCloudTemplateOptionsTest {

   public void testAs() {
      TemplateOptions options = new JoyentCloudTemplateOptions();
      assertEquals(options.as(JoyentCloudTemplateOptions.class), options);
   }

   @Test
   public void testGenerateKeyDefault() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      assert !options.shouldGenerateKey().isPresent();
   }

   @Test
   public void testGenerateKey() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions().generateKey(true);
      assert options.shouldGenerateKey().get();
   }

   @Test
   public void testGenerateKeyStatic() {
      JoyentCloudTemplateOptions options = generateKey(true);
      assert options.shouldGenerateKey().get();
   }

   // superclass tests
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinstallPrivateKeyBadFormat() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.installPrivateKey("whompy");
   }

   @Test
   public void testinstallPrivateKey() throws IOException {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      JoyentCloudTemplateOptions options = installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testauthorizePublicKeyBadFormat() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.authorizePublicKey("whompy");
   }

   @Test
   public void testauthorizePublicKey() throws IOException {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      JoyentCloudTemplateOptions options = authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      JoyentCloudTemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      JoyentCloudTemplateOptions options = new JoyentCloudTemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      JoyentCloudTemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }
}
