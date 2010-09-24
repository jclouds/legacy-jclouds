/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud.compute.options;

import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.blockOnPort;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.customizationScript;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.inboundPorts;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.installPrivateKey;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

/**
 * Tests possible uses of VCloudTemplateOptions and VCloudTemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class VCloudTemplateOptionsTest {

   public void testAs() {
      TemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.as(VCloudTemplateOptions.class), options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testcustomizationScriptBadFormat() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.customizationScript("");
   }

   @Test
   public void testcustomizationScript() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.customizationScript("mykeypair");
      assertEquals(options.getCustomizationScript(), "mykeypair");
   }

   @Test
   public void testNullcustomizationScript() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.getCustomizationScript(), null);
   }

   @Test
   public void testcustomizationScriptStatic() {
      VCloudTemplateOptions options = customizationScript("mykeypair");
      assertEquals(options.getCustomizationScript(), "mykeypair");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testcustomizationScriptNPE() {
      customizationScript(null);
   }

   @Test
   public void testinstallPrivateKey() throws IOException {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      VCloudTemplateOptions options = installPrivateKey(Payloads.newPayload("-----BEGIN RSA PRIVATE KEY-----"));
      assertEquals(options.getPrivateKey(), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey(null);
   }

   @Test
   public void testauthorizePublicKey() throws IOException {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      VCloudTemplateOptions options = authorizePublicKey(Payloads.newPayload("ssh-rsa"));
      assertEquals(options.getPublicKey(), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      VCloudTemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      VCloudTemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }
}
