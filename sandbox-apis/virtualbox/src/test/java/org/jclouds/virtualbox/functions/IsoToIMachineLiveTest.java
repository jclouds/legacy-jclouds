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
import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExists;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.inject.Guice;

/**
 * @author Andrea Turli, Mattias Holmqvist
 */
@Test(groups = "live", singleThreaded = true, testName = "IsoToIMachineLiveTest")
public class IsoToIMachineLiveTest extends BaseVirtualBoxClientLiveTest {

   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
         .getInstance(Json.class));

   private String settingsFile = null;
   private boolean forceOverwrite = true;
   private String vmId = "jclouds-image-iso-1";
   private String osTypeId = "";
   private String controllerIDE = "IDE Controller";
   private String diskFormat = "";
   private String adminDisk = "testadmin.vdi";
   private String guestId = "guest";
   private String hostId = "host";

   private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";

   @BeforeGroups(groups = { "live" })
   public void setUp() throws Exception {
      identity = "toor";
      credential = "password";
      new UnregisterMachineIfExists(manager, CleanupMode.Full).apply(vmName);
   }

   public void testCreateImageMachineFromIso() throws Exception {

      VirtualBoxManager manager = (VirtualBoxManager) context.getProviderSpecificContext().getApi();
      ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest(hostId, "localhost", guestId,
            "localhost", new Credentials("toor", "password"));
      IMachine imageMachine = new IsoToIMachine(manager, adminDisk, diskFormat, settingsFile, vmName, osTypeId, vmId,
            forceOverwrite, controllerIDE, localHostContext, hostId, guestId)
            .apply("ubuntu-11.04-server-i386.iso");

      IMachineToImage iMachineToImage = new IMachineToImage(manager, map);
      Image newImage = iMachineToImage.apply(imageMachine);
      // TODO add the description to the cache of the images or serialize to
      // YAML the image desc
      Set<? extends Image> images = context.getComputeService().listImages();

      assertTrue(any(images, equalTo(newImage)));

   }

}
