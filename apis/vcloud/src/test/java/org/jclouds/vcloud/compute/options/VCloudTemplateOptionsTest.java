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
package org.jclouds.vcloud.compute.options;

import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.blockOnPort;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.customizationScript;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.description;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.inboundPorts;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.ipAddressAllocationMode;
import static org.jclouds.vcloud.compute.options.VCloudTemplateOptions.Builder.parentNetwork;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;
import org.testng.annotations.Test;

/**
 * Tests possible uses of VCloudTemplateOptions and
 * VCloudTemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class VCloudTemplateOptionsTest {
   @Test
   public void testipAddressAllocationMode() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.ipAddressAllocationMode(IpAddressAllocationMode.NONE);
      assertEquals(options.getIpAddressAllocationMode(), IpAddressAllocationMode.NONE);
   }

   @Test
   public void testipAddressAllocationModeStatic() {
      VCloudTemplateOptions options = ipAddressAllocationMode(IpAddressAllocationMode.NONE);
      assertEquals(options.getIpAddressAllocationMode(), IpAddressAllocationMode.NONE);
   }

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
   public void testNullcustomizationScript() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.getCustomizationScript(), null);
   }

   @Test
   public void testcustomizationScript() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.customizationScript("mykeypair");
      assertEquals(options.getCustomizationScript(), "mykeypair");
   }

   @Test
   public void testcustomizationScriptStatic() {
      VCloudTemplateOptions options = customizationScript("mykeypair");
      assertEquals(options.getCustomizationScript(), "mykeypair");
   }

   @Test
   public void testNullparentNetwork() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      assertEquals(options.getParentNetwork(), null);
   }

   @Test
   public void testparentNetwork() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.parentNetwork(URI.create("https://network"));
      assertEquals(options.getParentNetwork(), URI.create("https://network"));
   }

   @Test
   public void testparentNetworkStatic() {
      VCloudTemplateOptions options = parentNetwork(URI.create("https://network"));
      assertEquals(options.getParentNetwork(), URI.create("https://network"));
   }

   @Test
   public void testdescription() {
      VCloudTemplateOptions options = new VCloudTemplateOptions();
      options.description("mykeypair");
      assertEquals(options.getDescription(), "mykeypair");
   }

   @Test
   public void testdescriptionStatic() {
      VCloudTemplateOptions options = description("mykeypair");
      assertEquals(options.getDescription(), "mykeypair");
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
