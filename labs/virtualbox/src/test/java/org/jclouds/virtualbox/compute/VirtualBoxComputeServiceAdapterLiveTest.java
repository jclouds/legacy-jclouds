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

import javax.inject.Inject;

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

@Test(groups = "live", singleThreaded = true, testName = "VirtualBoxComputeServiceAdapterLiveTest")
public class VirtualBoxComputeServiceAdapterLiveTest extends BaseVirtualBoxClientLiveTest {
   @Inject
   private VirtualBoxComputeServiceAdapter adapter;

   private NodeAndInitialCredentials<IMachine> machine;

   @Test
   public void testCreatedNodeHasExpectedNameAndWeCanConnectViaSsh() {
      String group = "foo";
      String name = "foo-ef4";
      String machineName = VIRTUALBOX_NODE_PREFIX + "myTestId-" + group + "-" + name;

      Template template = context.getComputeService().templateBuilder().build();
      machine = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      
      assertEquals(machine.getNode().getName(), machineName);
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
      //TODO: check state;
   }

   @Test
   public void testListImages() {
      Iterable<Image> iMageIterable = adapter.listImages();
      assertEquals(1, Iterables.size(iMageIterable));
      //TODO: check state;
   }

   @Override
   protected void tearDown() throws Exception {
      if (machine != null)
         adapter.destroyNode(machine.getNodeId() + "");
      super.tearDown();
   }
}
