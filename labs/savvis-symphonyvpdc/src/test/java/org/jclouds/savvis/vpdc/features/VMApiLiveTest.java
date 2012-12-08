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
package org.jclouds.savvis.vpdc.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.domain.VMSpec;
import org.jclouds.savvis.vpdc.options.GetVMOptions;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;

@Test(groups = "live")
public class VMApiLiveTest extends BaseVPDCApiLiveTest {

   private VMApi api;
   private VM vm;
   private Predicate<HostAndPort> socketTester;

   private String username = checkNotNull(System.getProperty("test." + provider + ".loginUser"), "test." + provider
            + ".loginUser");
   private String password = checkNotNull(System.getProperty("test." + provider + ".loginPassword"), "test." + provider
            + ".loginPassword");

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupContext() {
      super.setupContext();
      api = restContext.getApi().getVMApi();
      SocketOpen socketOpen = context.utils().injector().getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 130, 10, SECONDS);// make
   }

   private String billingSiteId;
   private String vpdcId;

   public void testCreateVirtualMachine() throws Exception {
      billingSiteId = restContext.getApi().getBrowsingApi().getOrg(null).getId();// default
      vpdcId = Iterables.find(restContext.getApi().getBrowsingApi().getOrg(billingSiteId).getVDCs(),
               new Predicate<Resource>() {

                  // try to find the first VDC owned by the current user
                  // check here for what the email property might be, or in
                  // the jclouds-wire.log
                  @Override
                  public boolean apply(Resource arg0) {
                     String description = restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId,
                              arg0.getId()).getDescription();
                     return description.indexOf(email) != -1;
                  }

               }).getId();

      String networkTierName = Iterables.get(
               restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId, vpdcId).getAvailableNetworks(), 0)
               .getName();
      String name = prefix;

      VDC vpdc = restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId, vpdcId);
      
      CIMOperatingSystem os = Iterables.find(restContext.getApi().listPredefinedOperatingSystems(),
               new Predicate<CIMOperatingSystem>() {

                  @Override
                  public boolean apply(CIMOperatingSystem arg0) {
                     return arg0.getOsType() == OSType.RHEL_64;
                  }

               });
      System.out.printf("Creating vm - vpdcId %s, vpdcName %s, networkName %s, name %s, os %s%n", vpdcId, vpdc.getName(), networkTierName, name, os);

      // TODO: determine the sizes available in the VDC, for example there's
      // a minimum size of boot disk, and also a preset combination of cpu count vs ram
      Task task = api.addVMIntoVDC(billingSiteId, vpdcId, VMSpec.builder().name(name).networkTierName(
               networkTierName).operatingSystem(os).memoryInGig(2).addDataDrive("/data01", 25).build());
      
      // make sure there's no error
      assert task.getId() != null && task.getError() == null : task;

      assert this.taskTester.apply(task.getId());
      
      // fetch the task again, in savvis, task.getOwner is populated with vApp after task has finished
      task = restContext.getApi().getBrowsingApi().getTask(task.getId());
      
      vm = restContext.getApi().getBrowsingApi().getVM(task.getOwner().getHref(), GetVMOptions.NONE);
      assert vm.getHref() != null : vm;
      
      // cannot ssh in savvis, as no public ip is assigned by default
//      conditionallyCheckSSH();
   }

   @Test(dependsOnMethods="testCloneVApp")
   public void testZCreateMultipleVMs() throws Exception {
      billingSiteId = restContext.getApi().getBrowsingApi().getOrg(null).getId();// default
      vpdcId = Iterables.find(restContext.getApi().getBrowsingApi().getOrg(billingSiteId).getVDCs(),
               new Predicate<Resource>() {

                  // try to find the first VDC owned by the current user
                  // check here for what the email property might be, or in
                  // the jclouds-wire.log
                  @Override
                  public boolean apply(Resource arg0) {
                     String description = restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId,
                              arg0.getId()).getDescription();
                     return description.indexOf(email) != -1;
                  }

               }).getId();

      String networkTierName = Iterables.get(
               restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId, vpdcId).getAvailableNetworks(), 0)
               .getId();
      Network networkTier = restContext.getApi().getBrowsingApi().getNetworkInVDC(billingSiteId, vpdcId,
               networkTierName);

      String name = prefix;

      // delete any old VM
      VDC vpdc = restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId, vpdcId);
      CIMOperatingSystem os = Iterables.find(restContext.getApi().listPredefinedOperatingSystems(),
               new Predicate<CIMOperatingSystem>() {

                  @Override
                  public boolean apply(CIMOperatingSystem arg0) {
                     return arg0.getOsType() == OSType.RHEL_64;
                  }

               });

      // TODO: Savvis returns network names with a - instead of space on getNetworkInVDC call,
      // fix this once savvis api starts returning correctly
      System.out.printf("Creating vm's - vpdcId %s, vpdcName %s, networkName %s, name %s, os %s%n", vpdcId, vpdc.getName(), networkTier
               .getName().replace("-", " "), name, os);

      Builder<VMSpec> vmSpecs = ImmutableSet.builder();
      int noOfVms = 2;
      for (int i = 0; i < noOfVms; i++) {
         // TODO: determine the sizes available in the VDC, for example there's
         // a minimum size of boot disk, and also a preset combination of cpu count vs ram
         VMSpec vmSpec = VMSpec.builder().name(name + i).operatingSystem(os).memoryInGig(2).networkTierName(
                  networkTierName).addDataDrive("/data01", 25).build();
         vmSpecs.add(vmSpec);
      }

      Set<Task> tasks = api.addMultipleVMsIntoVDC(vpdc.getHref(), vmSpecs.build());

      for (Task task : tasks) {
         // make sure there's no error
         assert task.getId() != null && task.getError() == null : task;
         assert this.taskTester.apply(task.getId());
         
         // fetch the task again, in savvis, task.getOwner is populated with vApp after task has finished
         task = restContext.getApi().getBrowsingApi().getTask(task.getId());
         
         VM newVM = restContext.getApi().getBrowsingApi().getVM(task.getOwner().getHref(), GetVMOptions.NONE);
         assert newVM.getHref() != null : newVM;
      }
   }
   
   /**
    * disabled because it not currently supported by savvis. Planned for august release by savvis. 
    * @throws Exception
    */
   @Test(enabled=false)
   public void testCaptureVAppTemplate() throws Exception {
      billingSiteId = restContext.getApi().getBrowsingApi().getOrg(null).getId();// default
      vpdcId = Iterables.find(restContext.getApi().getBrowsingApi().getOrg(billingSiteId).getVDCs(),
               new Predicate<Resource>() {

                  // try to find the first VDC owned by the current user
                  // check here for what the email property might be, or in
                  // the jclouds-wire.log
                  @Override
                  public boolean apply(Resource arg0) {
                     String description = restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId,
                              arg0.getId()).getDescription();
                     return description.indexOf(email) != -1;
                  }

               }).getId();

      VDC vpdc = restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId, vpdcId);

      for (Resource vApp : Iterables.filter(vpdc.getResourceEntities(), new Predicate<Resource>() {

         @Override
         public boolean apply(Resource arg0) {
            return VCloudMediaType.VAPP_XML.equals(arg0.getType());
         }

      })) {

         System.out.printf("Capturing VAppTemplate for vApp - %s%n", vApp.getName());
         Task task = api.captureVApp(billingSiteId, vpdcId, vApp.getHref());

         // make sure there's no error
         assert task.getId() != null && task.getError() == null : task;

         assert this.taskTester.apply(task.getId());
      }
   }

   @Test(dependsOnMethods="testCreateVirtualMachine")
   public void testCloneVApp() throws Exception {
	   billingSiteId = restContext.getApi().getBrowsingApi().getOrg(null).getId();// default
	   vpdcId = Iterables.find(restContext.getApi().getBrowsingApi().getOrg(billingSiteId).getVDCs(),
               new Predicate<Resource>() {

                  // try to find the first VDC owned by the current user
                  // check here for what the email property might be, or in
                  // the jclouds-wire.log
                  @Override
                  public boolean apply(Resource arg0) {
                     String description = restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId,
                              arg0.getId()).getDescription();
                     return description.indexOf(email) != -1;
                  }

               }).getId();
	      
	   String networkTierName = Iterables.get(
               restContext.getApi().getBrowsingApi().getVDCInOrg(billingSiteId, vpdcId).getAvailableNetworks(), 0)
               .getId();
	   
	   String clonedVMName = vm.getName() + "clone";
	   
	   System.out.printf("Cloning vm - name %s in vpdcId %s in network %s, newVM name is %s%n", vm.getName(), vpdcId, networkTierName, clonedVMName);
	   
       Task task = api.cloneVApp(vm.getHref(), clonedVMName, networkTierName);
       
       // make sure there's no error
       assert task.getId() != null && task.getError() == null : task;
       assert this.taskTester.apply(task.getId());
       
       // fetch the task again, in savvis, task.getOwner is populated with vApp after task has finished
       task = restContext.getApi().getBrowsingApi().getTask(task.getId());
       
       VM clonedVM = restContext.getApi().getBrowsingApi().getVM(task.getOwner().getHref(), GetVMOptions.NONE);
       assert clonedVM.getHref() != null : clonedVM;
   }

   protected void checkSSH(HostAndPort socket) {
      socketTester.apply(socket);
      SshClient api = view.utils().sshFactory()
            .create(socket, LoginCredentials.builder().user(username).password(password).build());
      try {
         api.connect();
         ExecResponse exec = api.exec("echo hello");
         System.out.println(exec);
         assertEquals(exec.getOutput().trim(), "hello");
      } finally {
         if (api != null)
            api.disconnect();
      }
   }

   @AfterGroups(groups = "live")
   protected void tearDownContext() {
      if (vm != null) {
         assert taskTester.apply(api.removeVMFromVDC(billingSiteId, vpdcId, vm.getId()).getId()) : vm;
      }
      super.tearDownContext();
   }
}
