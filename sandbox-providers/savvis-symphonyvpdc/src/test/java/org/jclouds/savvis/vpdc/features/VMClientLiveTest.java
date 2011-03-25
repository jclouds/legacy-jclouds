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

package org.jclouds.savvis.vpdc.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.domain.VMSpec;
import org.jclouds.savvis.vpdc.predicates.TaskSuccess;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.util.InetAddresses2;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Test(groups = "live")
public class VMClientLiveTest extends BaseVPDCClientLiveTest {

   private VMClient client;
   private Factory sshFactory;
   private VM vm;
   private RetryablePredicate<IPSocket> socketTester;
   private RetryablePredicate<String> taskTester;

   private String email = checkNotNull(System.getProperty("test." + provider + ".email"), "test." + provider + ".email");
   private String username = checkNotNull(System.getProperty("test." + provider + ".loginUser"), "test." + provider
         + ".loginUser");
   private String password = checkNotNull(System.getProperty("test." + provider + ".loginPassword"), "test." + provider
         + ".loginPassword");

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getVMClient();
      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<IPSocket>(injector.getInstance(SocketOpen.class), 130, 10, TimeUnit.SECONDS);// make
      taskTester = new RetryablePredicate<String>(injector.getInstance(TaskSuccess.class), 650, 10, TimeUnit.SECONDS);
   }

   protected String prefix = System.getProperty("user.name");
   private String billingSiteId;
   private String vpdcId;

   public void testCreateVirtualMachine() throws Exception {
      billingSiteId = context.getApi().getBrowsingClient().getOrg(null).getId();// default
      vpdcId = Iterables.find(context.getApi().getBrowsingClient().getOrg(billingSiteId).getVDCs(),
            new Predicate<Resource>() {

               // try to find the first VDC owned by the current user
               // check here for what the email property might be, or in
               // the jclouds-wire.log
               @Override
               public boolean apply(Resource arg0) {
                  String description = context.getApi().getBrowsingClient().getVDCInOrg(billingSiteId, arg0.getId())
                        .getDescription();
                  return description.indexOf(email) != -1;
               }

            }).getId();
      String networkTierName = Iterables.get(
            context.getApi().getBrowsingClient().getVDCInOrg(billingSiteId, vpdcId).getAvailableNetworks(), 0)
            .getName();
      String name = prefix;
      CIMOperatingSystem os = Iterables.find(injector.getInstance(Key.get(new TypeLiteral<Set<CIMOperatingSystem>>() {
      })), new Predicate<CIMOperatingSystem>() {

         @Override
         public boolean apply(CIMOperatingSystem arg0) {
            return arg0.getOsType() == OSType.RHEL_64;
         }

      });
      System.out.printf("vpdcId %s, networkName %s, name %s, os %s%n", vpdcId, networkTierName, name, os);

      // TODO: determine the sizes available in the VDC, for example there's
      // a minimum size of boot disk, and also a preset combination of cpu count vs ram
      Task task = client.addVMIntoVDC(billingSiteId, vpdcId, networkTierName, name, VMSpec.builder()
            .operatingSystem(os).memoryInGig(2).addDataDrive("/data01", 25).build());

      // make sure there's no error
      assert task.getId() != null && task.getError() != null : task;

      assert this.taskTester.apply(task.getId());
      vm = context.getApi().getBrowsingClient().getVMInVDC(billingSiteId, vpdcId, task.getOwner().getId());
      conditionallyCheckSSH();
   }

   private void conditionallyCheckSSH() {
      assert HostSpecifier.isValid(vm.getIpAddress());
      if (!InetAddresses2.isPrivateIPAddress(vm.getIpAddress())) {
         // not sure if the network is public or not, so we have to test
         IPSocket socket = new IPSocket(vm.getIpAddress(), 22);
         System.err.printf("testing socket %s%n", socket);
         System.err.printf("testing ssh %s%n", socket);
         checkSSH(socket);
      } else {
         System.err.printf("skipping ssh %s, as private%n", vm.getIpAddress());
      }
   }

   protected void checkSSH(IPSocket socket) {
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials(username, password));
      try {
         client.connect();
         ExecResponse exec = client.exec("echo hello");
         System.out.println(exec);
         assertEquals(exec.getOutput().trim(), "hello");
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (vm != null) {
         assert taskTester.apply(client.removeVMFromVDC(billingSiteId, vpdcId, vm.getId()).getId()) : vm;
      }
      super.tearDown();
   }
}