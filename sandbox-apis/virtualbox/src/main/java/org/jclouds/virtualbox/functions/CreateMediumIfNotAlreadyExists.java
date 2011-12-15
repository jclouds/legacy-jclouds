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

import com.google.common.base.Function;
import org.jclouds.virtualbox.domain.HardDisk;
import org.virtualbox_4_1.*;

import javax.annotation.Nullable;

/**
 * @author Mattias Holmqvist
 */
public class CreateMediumIfNotAlreadyExists implements Function<HardDisk, IMedium> {

   private final VirtualBoxManager manager;
   private boolean overwriteIfExists;

   public CreateMediumIfNotAlreadyExists(VirtualBoxManager manager, boolean overwriteIfExists) {
      this.manager = manager;
      this.overwriteIfExists = overwriteIfExists;
   }

   @Override
   public IMedium apply(@Nullable HardDisk hardDisk) {
      IVirtualBox vBox = manager.getVBox();
      try {
         String diskPath = hardDisk.getDiskPath();
         final IMedium medium = vBox.findMedium(diskPath, DeviceType.HardDisk);
         if (overwriteIfExists) {
            final IProgress progress = medium.deleteStorage();
            progress.waitForCompletion(-1);
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

   private IMedium createNewMedium(IVirtualBox vBox, HardDisk hardDisk) {
      IMedium medium = vBox.createHardDisk(hardDisk.getDiskFormat(), hardDisk.getDiskPath());
      createBaseStorage(medium);
      return medium;
   }

   private boolean notFoundException(VBoxException e) {
      return e.getMessage().contains("Could not find an open hard disk with location ");
   }

   private void createBaseStorage(IMedium hardDisk) {
      try {
         long size = 4L * 1024L * 1024L * 1024L - 4L;
         IProgress storageCreation = hardDisk.createBaseStorage(size,
                 (long) org.virtualbox_4_1.jaxws.MediumVariant.STANDARD.ordinal());
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
