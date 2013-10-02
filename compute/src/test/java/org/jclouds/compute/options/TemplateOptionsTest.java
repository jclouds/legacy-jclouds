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
package org.jclouds.compute.options;

import static org.jclouds.compute.options.TemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.compute.options.TemplateOptions.Builder.blockOnPort;
import static org.jclouds.compute.options.TemplateOptions.Builder.blockUntilRunning;
import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
import static org.jclouds.compute.options.TemplateOptions.Builder.installPrivateKey;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.Test;

/**
 * Tests possible uses of TemplateOptions and TemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class TemplateOptionsTest {
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinstallPrivateKeyBadFormat() {
      TemplateOptions options = new TemplateOptions();
      options.installPrivateKey("whompy");
   }

   @Test
   public void testinstallPrivateKey() throws IOException {
      TemplateOptions options = new TemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.toString(), "{privateKeyPresent=true}");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      TemplateOptions options = new TemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      TemplateOptions options = installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey((String) null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testauthorizePublicKeyBadFormat() {
      TemplateOptions options = new TemplateOptions();
      options.authorizePublicKey("whompy");
   }

   @Test
   public void testauthorizePublicKey() throws IOException {
      TemplateOptions options = new TemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(options.toString(), "{publicKeyPresent=true}");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      TemplateOptions options = new TemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      TemplateOptions options = authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey((String) null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      TemplateOptions options = new TemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      TemplateOptions options = new TemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.toString(), "{blockOnPort:seconds=22:30}");
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      TemplateOptions options = new TemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      TemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      TemplateOptions options = new TemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      TemplateOptions options = new TemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      TemplateOptions options = new TemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      TemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.toString(), "{inboundPorts=[22, 30]}");
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }

   @Test
   public void testblockUntilRunningDefault() {
      TemplateOptions options = new TemplateOptions();
      assertEquals(options.toString(), "{}");
      assertEquals(options.shouldBlockUntilRunning(), true);
   }

   @Test
   public void testblockUntilRunning() {
      TemplateOptions options = new TemplateOptions();
      options.blockUntilRunning(false);
      assertEquals(options.toString(), "{blockUntilRunning=false}");
      assertEquals(options.shouldBlockUntilRunning(), false);
   }

   @Test
   public void testBlockUntilRunningUnsetsBlockOnPort() {
      TemplateOptions options = new TemplateOptions();
      options.blockOnPort(22, 30);
      options.blockUntilRunning(false);
      assertEquals(options.shouldBlockUntilRunning(), false);
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockUntilRunningStatic() {
      TemplateOptions options = blockUntilRunning(false);
      assertEquals(options.shouldBlockUntilRunning(), false);
   }
}
