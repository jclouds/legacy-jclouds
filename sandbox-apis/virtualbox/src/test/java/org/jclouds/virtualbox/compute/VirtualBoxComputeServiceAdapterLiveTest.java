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

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;

@Test(groups = "live", singleThreaded = true, testName = "VirtualBoxComputeServiceAdapterLiveTest")
public class VirtualBoxComputeServiceAdapterLiveTest extends BaseVirtualBoxClientLiveTest {

   private VirtualBoxComputeServiceAdapter adapter;
   private NodeAndInitialCredentials<IMachine> machine;
   private final Map<OsFamily, Map<String, String>> osVersionMap = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
         .getInstance(Json.class));

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      final VirtualBoxManager manager = getManager();
      Function<IMachine, Image> iMachineToImage = new IMachineToImage(manager, osVersionMap);
      adapter = new VirtualBoxComputeServiceAdapter(manager, new JustProvider(provider, URI.create(endpoint),
            ImmutableSet.<String> of()), iMachineToImage);
   }

   protected VirtualBoxManager getManager() {
      return (VirtualBoxManager) context.getProviderSpecificContext().getApi();
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
      String name = "foo-ef4";
      Template template = context.getComputeService().templateBuilder().build();
      machine = adapter.createNodeWithGroupEncodedIntoName(group, name, template);
      assertEquals(machine.getNode().getName(), name);
      assertEquals(machine.getNodeId(), machine.getNode().getId());
      // is there a place for group?
      // check other things, like cpu correct, mem correct, image/os is correct
      // (as possible)
      // TODO: what's the IP address?
      // assert
      // InetAddresses.isInetAddress(machine.getPrimaryBackendIpAddress()) :
      // machine;
      doConnectViaSsh(machine.getNode(), prioritizeCredentialsFromTemplate.apply(template, machine.getCredentials()));

   }

   protected void doConnectViaSsh(IMachine machine, Credentials creds) {
      SshClient ssh = context.utils().sshFactory().create(new IPSocket("//TODO", 22), creds);
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
      Iterable<Image> iMageIterable = adapter.listImages();
      for (Image image : iMageIterable) {
         System.out.println(image);
      }
      // check state;
   }

   @AfterGroups(groups = "live")
   protected void tearDown() throws Exception {
      if (machine != null)
         adapter.destroyNode(machine.getNodeId() + "");
      super.tearDown();
   }
}
