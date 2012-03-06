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

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.functions.IMachineToSshClient;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;

import com.google.common.collect.Iterables;

@Test(groups = "plive", singleThreaded = true, testName = "VirtualBoxComputeServiceAdapterLiveTest")
public class VirtualBoxComputeServiceAdapterLiveTest extends BaseVirtualBoxClientLiveTest {

   private VirtualBoxComputeServiceAdapter adapter;
   private NodeAndInitialCredentials<IMachine> machine;

   @Override
   public void setupClient() {
      super.setupClient();
      adapter = context.utils().injector().getInstance(VirtualBoxComputeServiceAdapter.class);
   }

   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String group = "foo";
      String name = "foo-ef4";
      String machineName = VIRTUALBOX_NODE_PREFIX + "myTestId-" + group + "-" + name;
      // get the image from
      Image image = Iterables.get(adapter.listImages(), 0);
      System.out.println(context.getComputeService().templateBuilder());
      Template template = context.getComputeService().templateBuilder().fromImage(image).build();
      machine = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertEquals(machine.getNode().getName(), machineName);
      // is there a place for group?
      // check other things, like cpu correct, mem correct, image/os is correct
      // (as possible)
      // TODO: what's the IP address?
      // assert
      // InetAddresses.isInetAddress(machine.getPrimaryBackendIpAddress()) :
      // machine;
      doConnectViaSsh(machine.getNode(), prioritizeCredentialsFromTemplate.apply(template, machine.getCredentials()));
   }

   protected void doConnectViaSsh(IMachine machine, LoginCredentials creds) {
      SshClient ssh = context.utils().injector().getInstance(IMachineToSshClient.class).apply(machine);
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
      assertEquals(1, Iterables.size(profiles));
   }

   @Test
   public void testListImages() {
      Iterable<Image> iMageIterable = adapter.listImages();
      for (Image image : iMageIterable) {
         System.out.println(image);
      }
      // check state;
   }

   @Override
   protected void tearDown() throws Exception {
      if (machine != null)
         adapter.destroyNode(machine.getNodeId() + "");
      super.tearDown();
   }
}
