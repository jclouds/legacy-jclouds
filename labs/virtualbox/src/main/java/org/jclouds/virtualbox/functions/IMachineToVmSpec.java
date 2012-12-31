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

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.StorageController.Builder;
import org.jclouds.virtualbox.domain.VmSpec;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.IMediumAttachment;
import org.virtualbox_4_2.IStorageController;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Get a VmSpec from an IMachine
 * 
 * @author Andrea Turli
 */
public class IMachineToVmSpec implements Function<IMachine, VmSpec> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public VmSpec apply(IMachine machine) {
      List<StorageController> controllers = buildControllers(machine);

      // TODO some parameters are predefined cause the IMachine doesn't have the
      // concept i.e.: cleanUpMode
      org.jclouds.virtualbox.domain.VmSpec.Builder vmSpecBuilder = VmSpec.builder();

      vmSpecBuilder.id(machine.getId()).name(machine.getName()).memoryMB(machine.getMemorySize().intValue())
               .osTypeId(machine.getOSTypeId()).forceOverwrite(true).cleanUpMode(CleanupMode.Full);

      for (StorageController storageController : controllers) {
         vmSpecBuilder.controller(storageController);
      }

      return vmSpecBuilder.build();
   }

   private List<StorageController> buildControllers(IMachine machine) {

      List<StorageController> controllers = Lists.newArrayList();
      for (IStorageController iStorageController : machine.getStorageControllers()) {

         Builder storageControllerBuilder = StorageController.builder();
         for (IMediumAttachment iMediumAttachment : machine.getMediumAttachmentsOfController(iStorageController
                  .getName())) {
            IMedium iMedium = iMediumAttachment.getMedium();
            if(iMedium != null) {
               if (iMedium.getDeviceType().equals(DeviceType.HardDisk)) {
                  storageControllerBuilder.attachHardDisk(HardDisk.builder().diskpath(iMedium.getLocation())
                           .autoDelete(true).controllerPort(iMediumAttachment.getPort())
                           .deviceSlot(iMediumAttachment.getDevice().intValue()).build());
               } else if (iMedium.getDeviceType().equals(DeviceType.DVD)) {
                  storageControllerBuilder.attachISO(iMediumAttachment.getPort(), iMediumAttachment.getDevice().intValue(),
                           iMedium.getLocation());
               }
            }
         }
         controllers.add(storageControllerBuilder.name(iStorageController.getName()).bus(iStorageController.getBus())
                  .build());
      }
      return controllers;
   }
}
