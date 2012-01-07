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
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.NatAdapter;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.Function;
import com.google.inject.Guice;

/**
 * @author Andrea Turli, Mattias Holmqvist
 */
@Test(groups = "live", singleThreaded = true, testName = "CreateAndInstallVmLiveTest")
public class CreateAndInstallVmLiveTest extends BaseVirtualBoxClientLiveTest {

   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
           .getInstance(Json.class));

   private VmSpec vmSpecification;

   @Override
   @BeforeClass(groups = "live")
   public void setupClient() {
      super.setupClient();
      String vmName = "jclouds-image-virtualbox-iso-to-machine-test";

      HardDisk hardDisk = HardDisk.builder().diskpath(adminDisk).autoDelete(true)
            .controllerPort(0).deviceSlot(1).build();
      StorageController ideController = StorageController.builder().name("IDE Controller").bus(StorageBus.IDE)
              .attachISO(0, 0, operatingSystemIso)
              .attachHardDisk(hardDisk)
              .attachISO(1, 1, guestAdditionsIso).build();
      vmSpecification = VmSpec.builder().id("jclouds-image-iso-1").name(vmName).memoryMB(512).osTypeId("")
              .controller(ideController)
              .forceOverwrite(true)
              .cleanUpMode(CleanupMode.Full)
              .natNetworkAdapter(0, NatAdapter.builder().tcpRedirectRule("127.0.0.1", 2222, "", 22).build()).build();
      undoVm(vmSpecification);
   }

   public void testCreateImageMachineFromIso() throws Exception {

      IMachine imageMachine = context.utils().injector().getInstance(CreateAndInstallVm.class).apply(vmSpecification);

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
   @Override
   @AfterClass(groups = "live")
   protected void tearDown() throws Exception {
      undoVm(vmSpecification);
      super.tearDown();
   }
}
