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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Random;

import javax.inject.Inject;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.functions.IMachineToSshClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IMachine;

import com.google.common.collect.Iterables;

@Test(groups = "live", singleThreaded = true, testName = "VirtualBoxComputeServiceAdapterLiveTest")
public class VirtualBoxComputeServiceAdapterLiveTest extends BaseVirtualBoxClientLiveTest {

   private NodeAndInitialCredentials<IMachine> ubuntu;
   private NodeAndInitialCredentials<IMachine> centos;

   @Inject
   protected VirtualBoxComputeServiceAdapter adapter;


   @Test
   public void testCreatedNodeHasExpectedNameAndWeCanConnectViaSsh() {
      String group = "foo";
      String name = "foo-ef9";
      Template template = view.getComputeService().templateBuilder().build();
      ubuntu = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertTrue(ubuntu.getNode().getName().contains(group));
      assertTrue(ubuntu.getNode().getName().contains(name));
      assertTrue(ubuntu.getNode().getName().startsWith(VIRTUALBOX_NODE_PREFIX));
      doConnectViaSsh(ubuntu.getNode(), prioritizeCredentialsFromTemplate.apply(template, ubuntu.getCredentials()));
   }

   @Test
   public void testCreatedCentosNodeHasExpectedNameAndWeCanConnectViaSsh() {
      String group = "foo";
      String name = "centos6-" + new Random(100).nextInt();
      Template template = view.getComputeService().templateBuilder()
            .imageId("centos-6.3-amd64")
            .build();
      centos = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertTrue(centos.getNode().getName().contains(group));
      assertTrue(centos.getNode().getName().contains(name));
      assertTrue(centos.getNode().getName().startsWith(VIRTUALBOX_NODE_PREFIX));
      doConnectViaSsh(centos.getNode(), prioritizeCredentialsFromTemplate.apply(template, centos.getCredentials()));
   }

   protected void doConnectViaSsh(IMachine machine, LoginCredentials creds) {
      SshClient ssh = view.utils().injector().getInstance(IMachineToSshClient.class).apply(machine);
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
      assertFalse(Iterables.isEmpty(profiles));
   }

   @Test
   public void testListImages() {
      Iterable<Image> iMageIterable = adapter.listImages();
      assertFalse(Iterables.isEmpty(iMageIterable));
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (ubuntu != null)
         adapter.destroyNode(ubuntu.getNodeId() + "");
      if (centos != null)
         adapter.destroyNode(centos.getNodeId() + "");
      super.tearDownContext();
   }
}
