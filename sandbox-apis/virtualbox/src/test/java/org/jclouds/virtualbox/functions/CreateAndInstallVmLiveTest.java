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

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.virtualbox.domain.ExecutionType.HEADLESS;
import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.NatAdapter;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.admin.FindClonesOfMachine;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.jclouds.virtualbox.util.PropertyUtils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Guice;

/**
 * @author Andrea Turli, Mattias Holmqvist
 */
@Test(groups = "live", singleThreaded = true, testName = "CreateAndInstallVmLiveTest")
public class CreateAndInstallVmLiveTest extends BaseVirtualBoxClientLiveTest {

   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
           .getInstance(Json.class));

   private String vmId = "jclouds-image-iso-1";
   private String osTypeId = "";
   private String ideControllerName = "IDE Controller";
   private String guestId = "guest";
   private String hostId = "host";
   private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";
   private StorageController ideController;
   private VmSpec vmSpecification;

   @BeforeGroups(groups = {"live"})
   public void setUp() throws Exception {
      identity = "toor";
      credential = "password";
      
      String workingDir = PropertyUtils.getWorkingDirFromProperty();
      HardDisk hardDisk = HardDisk.builder().diskpath(workingDir + "/testadmin.vdi").autoDelete(true)
            .controllerPort(0).deviceSlot(1).build();
      ideController = StorageController.builder().name(ideControllerName).bus(StorageBus.IDE)
              .attachISO(0, 0, workingDir + "/ubuntu-11.04-server-i386.iso")
              .attachHardDisk(hardDisk)
              .attachISO(1, 1, workingDir + "/VBoxGuestAdditions_4.1.2.iso").build();
      vmSpecification = VmSpec.builder().id(vmId).name(vmName).memoryMB(512).osTypeId(osTypeId)
              .controller(ideController)
              .forceOverwrite(true)
              .cleanUpMode(CleanupMode.Full)
              .natNetworkAdapter(0, NatAdapter.builder().tcpRedirectRule("127.0.0.1", 2222, "", 22).build()).build();
      new FindClonesOfMachine(manager).apply(vmSpecification);
      new UnregisterMachineIfExistsAndDeleteItsMedia(manager).apply(vmSpecification);
   }

   public void testCreateImageMachineFromIso() throws Exception {

      VirtualBoxManager manager = (VirtualBoxManager) context.getProviderSpecificContext().getApi();
      ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest(hostId, "localhost", guestId,
              "localhost", new Credentials("toor", "password"));
      Predicate<IPSocket> socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 10, 1, TimeUnit.SECONDS);

      IMachine imageMachine = new CreateAndInstallVm(manager, guestId, localHostContext, hostId,
              socketTester, "127.0.0.1", 8080, HEADLESS)
              .apply(vmSpecification);

      IMachineToImage iMachineToImage = new IMachineToImage(manager, map);
      Image newImage = iMachineToImage.apply(imageMachine);
      // TODO add the description to the cache of the images or serialize to
      // YAML the image desc
      Set<? extends Image> images = context.getComputeService().listImages();
      Iterable<String> imageIds = transform(images, extractId());
      assertTrue(any(imageIds, equalTo(newImage.getId())));
   }

   private Function<Image, String> extractId() {
      return new Function<Image, String>() {

         @Override
         public String apply(@Nullable Image input) {
            return input.getId();
         }
      };
   }

}
