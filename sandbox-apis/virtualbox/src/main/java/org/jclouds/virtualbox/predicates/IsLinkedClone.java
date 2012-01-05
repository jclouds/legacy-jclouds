/*
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

package org.jclouds.virtualbox.predicates;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IMediumAttachment;
import org.virtualbox_4_1.IStorageController;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Predicate;

/**
 * 
 * @author Andrea Turli
 */
public class IsLinkedClone implements Predicate<IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private VirtualBoxManager manager;

   public IsLinkedClone(VirtualBoxManager manager) {
      this.manager = manager;
   }

   @Override
   public boolean apply(@Nullable IMachine machine) {

      for (IStorageController iStorageController : machine
            .getStorageControllers()) {

         for (IMediumAttachment iMediumAttachment : machine
               .getMediumAttachmentsOfController(iStorageController.getName())) {
            IMedium iMedium = iMediumAttachment.getMedium();
            if (iMedium.getDeviceType().equals(DeviceType.HardDisk)) {
               if (iMedium.getParent() != null) {
                  // more than one machine is attached to this hd
                  for (IMedium child : iMedium.getParent().getChildren()) {
                     for (String machineId : child.getMachineIds()) {
                        IMachine iMachine = manager.getVBox().findMachine(machineId);
                        if (!iMachine.getName().equals(machine.getName())) {
                           logger.debug("Machine %s is a linked clone", machine.getName());
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return "IsLinkedClone()";
   }

}
