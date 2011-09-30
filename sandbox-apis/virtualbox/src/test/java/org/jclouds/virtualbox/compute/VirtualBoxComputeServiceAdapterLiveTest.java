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

package org.jclouds.virtualbox.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.net.URI;
import java.util.Map;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.location.suppliers.JustProvider;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.functions.IMachineToImage;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

@Test(groups = "live", singleThreaded = true, testName = "VirtualBoxComputeServiceAdapterLiveTest")
public class VirtualBoxComputeServiceAdapterLiveTest extends BaseVirtualBoxClientLiveTest {

   private VirtualBoxComputeServiceAdapter adapter;
   private IMachine machine;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      adapter = new VirtualBoxComputeServiceAdapter(getManager(),
            new JustProvider(ImmutableSet.<String> of(), provider, URI.create(endpoint)));
   }

   protected VirtualBoxManager getManager() {
      return (VirtualBoxManager) context.getProviderSpecificContext().getApi();
   }

   @Test
   public void testListLocations() {
      assertFalse(Iterables.isEmpty(adapter.listLocations()));
   }

   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String group = "foo";
      String name = "foo-ef4";
      Template template = context.getComputeService().templateBuilder().build();
      Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();
      machine = adapter.createNodeWithGroupEncodedIntoNameThenStoreCredentials(group, name, template, credentialStore);
      assertEquals(machine.getName(), name);
      // is there a place for group?
      // check other things, like cpu correct, mem correct, image/os is correct
      // (as possible)
      assert credentialStore.containsKey("node#" + machine.getId()) : "credentials to log into machine not found "
            + machine;
//      TODO: what's the IP address?
//      assert InetAddresses.isInetAddress(machine.getPrimaryBackendIpAddress()) : machine;
      doConnectViaSsh(machine, credentialStore.get("node#" + machine.getId()));
   }

   protected void doConnectViaSsh(IMachine machine, Credentials creds) {
      SshClient ssh = context.utils().sshFactory()
            .create(new IPSocket("//TODO", 22), creds);
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
      Iterable<IMachine> profiles = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(profiles));
      // check state;
   }
   @Test
   public void testListImages() {
      IMachineToImage iMachineToImage = new IMachineToImage(getManager());

      Iterable<IMachine> iMachineIterable = adapter.listImages();
      for (IMachine iMachine : iMachineIterable) {
         Image image = iMachineToImage.apply(iMachine);
         System.out.println(image);
      }
      // check state;
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (machine != null)
         adapter.destroyNode(machine.getId() + "");
      super.tearDown();
   }
}
