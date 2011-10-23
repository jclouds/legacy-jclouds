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
import org.virtualbox_4_1.*;

import javax.annotation.Nullable;

/**
 * @author Mattias Holmqvist
 */
public class CreateMediumIfNotAlreadyExists implements Function<String, IMedium> {

   private final VirtualBoxManager manager;
   private final String diskFormat;
   private boolean overwriteIfExists;

   public CreateMediumIfNotAlreadyExists(VirtualBoxManager manager, String diskFormat, boolean overwriteIfExists) {
      this.manager = manager;
      this.diskFormat = diskFormat;
      this.overwriteIfExists = overwriteIfExists;
   }

   @Override
   public IMedium apply(@Nullable String path) {
      IVirtualBox vBox = manager.getVBox();
      try {
         final IMedium medium = vBox.findMedium(path, DeviceType.HardDisk);
         if (overwriteIfExists) {
            final IProgress progress = medium.deleteStorage();
            progress.waitForCompletion(-1);
            return createNewMedium(vBox, path);
         } else {
            throw new IllegalStateException("Medium for path " + path + " already exists.");
         }
      } catch (VBoxException e) {
         if (notFoundException(e))
            return createNewMedium(vBox, path);
         throw e;
      }
   }

   private IMedium createNewMedium(IVirtualBox vBox, String path) {
      IMedium hardDisk = vBox.createHardDisk(diskFormat, path);
      createBaseStorage(hardDisk);
      return hardDisk;
   }

   private boolean notFoundException(VBoxException e) {
      return e.getMessage().indexOf("Could not find an open hard disk with location ") != -1;
   }

   private void createBaseStorage(IMedium hardDisk) {
      try {
         long size = 4L * 1024L * 1024L * 1024L - 4L;
         IProgress storageCreation = hardDisk.createBaseStorage(size, (long) org.virtualbox_4_1.jaxws.MediumVariant.STANDARD.ordinal());
         storageCreation.waitForCompletion(-1);
      } catch (VBoxException e) {
         if (fileNotFoundException(e)) {
            // File for medium could not be found. Something wrong with creation.
            hardDisk.deleteStorage();
         }
         if (!storageAlreadyExists(e)) {
            // Hard disk file was created but the storage had been created before that.
            throw e;
         }
      }
   }

   private boolean fileNotFoundException(VBoxException e) {
      return e.getMessage().indexOf("VERR_FILE_NOT_FOUND") != -1;
   }

   private boolean storageAlreadyExists(VBoxException e) {
      return e.getMessage().indexOf("VirtualBox error: Storage for the medium ") != -1 &&
              e.getMessage().indexOf("is already created") != -1;
   }

}
