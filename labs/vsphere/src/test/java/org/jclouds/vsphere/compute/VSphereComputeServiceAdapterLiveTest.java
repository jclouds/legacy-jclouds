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

package org.jclouds.vsphere.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.inject.Inject;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshClient;
import org.jclouds.vsphere.BaseVSphereClientLiveTest;
import org.jclouds.vsphere.functions.VirtualMachineToSshClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.vmware.vim25.mo.VirtualMachine;

@Test(groups = "live", singleThreaded = true, testName = "VSphereComputeServiceAdapterLiveTest")
public class VSphereComputeServiceAdapterLiveTest extends BaseVSphereClientLiveTest {

   private NodeAndInitialCredentials<VirtualMachine> machine;

   @Inject
   protected VSphereComputeServiceAdapter adapter;

   @Test
   public void testCreatedNodeHasExpectedNameAndWeCanConnectViaSsh() {
      String group = "foo";
      String name = "foo-ef4";
      Template template = view.getComputeService().templateBuilder().build();
      machine = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertNotNull(machine);
      assertTrue(machine.getNode().getName().contains(group));
      assertTrue(machine.getNode().getName().contains(name));
      doConnectViaSsh(machine.getNode(), prioritizeCredentialsFromTemplate.apply(template, machine.getCredentials()));
   }

   protected void doConnectViaSsh(VirtualMachine machine, LoginCredentials creds) {
      SshClient ssh = view.utils().injector().getInstance(VirtualMachineToSshClient.class).apply(machine);
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
      Iterable<Hardware> profiles = adapter.listHardwareProfiles();
      assertTrue(!Iterables.isEmpty(profiles));
   }

   @Test
   public void testListImages() {
      Iterable<Image> iMageIterable = adapter.listImages();
      assertTrue(!Iterables.isEmpty(iMageIterable));
   }

   @AfterClass
   @Override
   protected void tearDown() throws Exception {
      if (machine != null) {
         adapter.suspendNode(machine.getNodeId() + "");
         adapter.destroyNode(machine.getNodeId() + "");
      }
      super.tearDown();
   }
}
