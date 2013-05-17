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
package org.jclouds.softlayer.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Properties;
import java.util.Random;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.compute.strategy.SoftLayerComputeServiceAdapter;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.features.BaseSoftLayerClientLiveTest;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.inject.Injector;
import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true, testName = "SoftLayerComputeServiceAdapterLiveTest")
public class SoftLayerComputeServiceAdapterLiveTest extends BaseSoftLayerClientLiveTest {

   private SoftLayerComputeServiceAdapter adapter;
   private TemplateBuilder templateBuilder;
   private Factory sshFactory;
   private NodeAndInitialCredentials<VirtualGuest> guest;

   @Override
   protected SoftLayerClient create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      adapter = injector.getInstance(SoftLayerComputeServiceAdapter.class);
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      return injector.getInstance(SoftLayerClient.class);
   }

   @Test
   public void testListLocations() {
      assertFalse(Iterables.isEmpty(adapter.listLocations()));
   }

   private static final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate = new PrioritizeCredentialsFromTemplate(
         new DefaultCredentialsFromImageOrOverridingCredentials());

   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String group = "foo";
      String name = "node" + new Random().nextInt();

      Template template = templateBuilder.build();
      // test passing custom options
      template.getOptions().as(SoftLayerTemplateOptions.class).domainName("me.org");

      guest = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertEquals(guest.getNode().getHostname(), name);
      assertEquals(guest.getNodeId(), guest.getNode().getId() + "");
      assertEquals(guest.getNode().getDomain(), template.getOptions().as(SoftLayerTemplateOptions.class)
            .getDomainName());
      assert InetAddresses.isInetAddress(guest.getNode().getPrimaryBackendIpAddress()) : guest;
      doConnectViaSsh(guest.getNode(), prioritizeCredentialsFromTemplate.apply(template, guest.getCredentials()));
   }

   protected void doConnectViaSsh(VirtualGuest guest, LoginCredentials creds) {
      SshClient ssh = sshFactory.create(HostAndPort.fromParts(guest.getPrimaryIpAddress(), 22), creds);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         System.err.println(ssh.exec("df -k").getOutput());
         System.err.println(ssh.exec("mount").getOutput());
         System.err.println(ssh.exec("uname -a").getOutput());
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @Test
   public void testListHardwareProfiles() {
      Iterable<Iterable<ProductItem>> profiles = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(profiles));

      for (Iterable<ProductItem> profile : profiles) {
         // CPU, RAM and Volume
         assertEquals(Iterables.size(profile), 3);
      }
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (guest != null)
         adapter.destroyNode(guest.getNodeId() + "");
      super.tearDown();
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), new SshjSshClientModule());
   }
}
