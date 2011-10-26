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
package org.jclouds.softlayer.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Map;
import java.util.Random;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.compute.strategy.SoftLayerComputeServiceAdapter;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.VirtualGuest;
import org.jclouds.softlayer.features.BaseSoftLayerClientLiveTest;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;
import com.google.inject.Guice;

@Test(groups = "live", singleThreaded = true, testName = "SoftLayerComputeServiceAdapterLiveTest")
public class SoftLayerComputeServiceAdapterLiveTest extends BaseSoftLayerClientLiveTest {

   private SoftLayerComputeServiceAdapter adapter;
   private VirtualGuest guest;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      adapter = Guice.createInjector(module, new Log4JLoggingModule())
               .getInstance(SoftLayerComputeServiceAdapter.class);
   }

   @Test
   public void testListLocations() {
      assertFalse(Iterables.isEmpty(adapter.listLocations()));
   }

   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String group = "foo";
      String name = "node" + new Random().nextInt();
      Template template = computeContext.getComputeService().templateBuilder().build();

      // test passing custom options
      template.getOptions().as(SoftLayerTemplateOptions.class).domainName("me.org");

      Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();
      guest = adapter.createNodeWithGroupEncodedIntoNameThenStoreCredentials(group, name, template, credentialStore);
      assertEquals(guest.getHostname(), name);
      assertEquals(guest.getDomain(), template.getOptions().as(SoftLayerTemplateOptions.class).getDomainName());
      // check other things, like cpu correct, mem correct, image/os is correct
      // (as possible)
      assert credentialStore.containsKey("node#" + guest.getId()) : "credentials to log into guest not found " + guest;
      assert InetAddresses.isInetAddress(guest.getPrimaryBackendIpAddress()) : guest;
      doConnectViaSsh(guest, credentialStore.get("node#" + guest.getId()));
   }

   protected void doConnectViaSsh(VirtualGuest guest, Credentials creds) {
      SshClient ssh = computeContext.utils().sshFactory().create(new IPSocket(guest.getPrimaryIpAddress(), 22), creds);
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
         adapter.destroyNode(guest.getId() + "");
      super.tearDown();
   }
}
