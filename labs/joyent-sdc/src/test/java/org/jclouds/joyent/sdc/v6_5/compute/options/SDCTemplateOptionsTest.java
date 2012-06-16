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
package org.jclouds.joyent.sdc.v6_5.compute.options;

import static org.jclouds.joyent.sdc.v6_5.compute.options.SDCTemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.joyent.sdc.v6_5.compute.options.SDCTemplateOptions.Builder.blockOnPort;
import static org.jclouds.joyent.sdc.v6_5.compute.options.SDCTemplateOptions.Builder.generateKey;
import static org.jclouds.joyent.sdc.v6_5.compute.options.SDCTemplateOptions.Builder.inboundPorts;
import static org.jclouds.joyent.sdc.v6_5.compute.options.SDCTemplateOptions.Builder.installPrivateKey;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of SDCTemplateOptions and SDCTemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
@Test(testName = "SDCTemplateOptionsTest")
public class SDCTemplateOptionsTest {

   public void testAs() {
      TemplateOptions options = new SDCTemplateOptions();
      assertEquals(options.as(SDCTemplateOptions.class), options);
   }

   @Test
   public void testGenerateKeyDefault() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      assert !options.shouldGenerateKey();
   }

   @Test
   public void testGenerateKey() {
      SDCTemplateOptions options = new SDCTemplateOptions().generateKey(true);
      assert options.shouldGenerateKey();
   }

   @Test
   public void testGenerateKeyStatic() {
      SDCTemplateOptions options = generateKey(true);
      assert options.shouldGenerateKey();
   }

   // superclass tests
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinstallPrivateKeyBadFormat() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.installPrivateKey("whompy");
   }

   @Test
   public void testinstallPrivateKey() throws IOException {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      SDCTemplateOptions options = installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testauthorizePublicKeyBadFormat() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.authorizePublicKey("whompy");
   }

   @Test
   public void testauthorizePublicKey() throws IOException {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      SDCTemplateOptions options = authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      SDCTemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      SDCTemplateOptions options = new SDCTemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      SDCTemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }
}
