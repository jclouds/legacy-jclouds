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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_2.AccessMode;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.IMediumAttachment;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * @author Mattias Holmqvist
 */
@Singleton
public class CreateMediumIfNotAlreadyExists implements Function<HardDisk, IMedium> {

   private final Supplier<VirtualBoxManager> manager;
   private final MachineUtils machineUtils;
   private final boolean overwriteIfExists;

   @Inject
   public CreateMediumIfNotAlreadyExists(Supplier<VirtualBoxManager> manager, MachineUtils machineUtils, boolean overwriteIfExists) {
      this.manager = manager;
      this.machineUtils = machineUtils;
      this.overwriteIfExists = overwriteIfExists;
   }

   public static final Pattern ATTACHED_PATTERN = Pattern.compile(".*is still attached.*: ([-0-9a-f]+) .*");

   @Override
   public IMedium apply(HardDisk hardDisk) {
      IVirtualBox vBox = manager.get().getVBox();
      try {
         String diskPath = hardDisk.getDiskPath();
         final IMedium medium = vBox.openMedium(diskPath, DeviceType.HardDisk, AccessMode.ReadWrite, false);
         if (overwriteIfExists) {
            try {
               deleteMediumAndBlockUntilComplete(medium);
            } catch (VBoxException e) {
               onAlreadyAttachedExceptionDetachOrPropagate(vBox, medium, e);
            }
            return createNewMedium(vBox, hardDisk);
         } else {
            throw new IllegalStateException("Medium for path " + diskPath + " already exists.");
         }
      } catch (VBoxException e) {
         if (notFoundException(e))
            return createNewMedium(vBox, hardDisk);
         throw e;
      }
   }

   private void onAlreadyAttachedExceptionDetachOrPropagate(IVirtualBox vBox, final IMedium medium, VBoxException e) {
      Matcher matcher = ATTACHED_PATTERN.matcher(e.getMessage());
      if (matcher.find()) {
         String machineId = matcher.group(1);
         IMachine immutableMachine = vBox.findMachine(machineId);
         IMediumAttachment mediumAttachment = Iterables.find(immutableMachine.getMediumAttachments(),
                  new Predicate<IMediumAttachment>() {
                     public boolean apply(IMediumAttachment in) {
                        return in.getMedium().getId().equals(medium.getId());
                     }
                  });
         machineUtils.writeLockMachineAndApply(immutableMachine.getName(), new DetachDistroMediumFromMachine(
               mediumAttachment.getController(), mediumAttachment.getPort(), mediumAttachment.getDevice()));
         deleteMediumAndBlockUntilComplete(medium);
      } else {
         throw e;
      }
   }

   void deleteMediumAndBlockUntilComplete(IMedium medium) {
      final IProgress progress = medium.deleteStorage();
      progress.waitForCompletion(-1);
   }

   private IMedium createNewMedium(IVirtualBox vBox, HardDisk hardDisk) {
      IMedium medium = vBox.createHardDisk(hardDisk.getDiskFormat(), hardDisk.getDiskPath());
      createBaseStorage(medium);
      return medium;
   }

   private boolean notFoundException(VBoxException e) {
      return e.getMessage().contains("VirtualBox error: Could not find file for the medium ");
   }

   private void createBaseStorage(IMedium hardDisk) {
      try {
         long size = 4L * 1024L * 1024L * 1024L - 4L;
         IProgress storageCreation = hardDisk.createBaseStorage(size,
                  (long) org.virtualbox_4_2.jaxws.MediumVariant.STANDARD.ordinal());
         storageCreation.waitForCompletion(-1);
      } catch (VBoxException e) {
         if (fileNotFoundException(e)) {
            // File for medium could not be found. Something wrong with
            // creation.
            hardDisk.deleteStorage();
         }
         if (!storageAlreadyExists(e)) {
            // Hard disk file was created but the storage had been created
            // before that.
            throw e;
         }
      }
   }

   private boolean fileNotFoundException(VBoxException e) {
      return e.getMessage().contains("VERR_FILE_NOT_FOUND");
   }

   private boolean storageAlreadyExists(VBoxException e) {
      return e.getMessage().contains("VirtualBox error: Storage for the medium ")
               && e.getMessage().contains("is already created");
   }

}
