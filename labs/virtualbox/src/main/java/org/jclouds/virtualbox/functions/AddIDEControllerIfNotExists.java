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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.virtualbox.domain.StorageController;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.VBoxException;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class AddIDEControllerIfNotExists implements Function<IMachine, Void> {
   private final StorageController storageController;

   public AddIDEControllerIfNotExists(StorageController storageController) {
      this.storageController = checkNotNull(storageController, "storageController can't be null");
   }

   @Override
   public Void apply(IMachine machine) {
      try {
         machine.addStorageController(storageController.getName(), storageController.getBus());
         machine.saveSettings();
      } catch (VBoxException e) {
         if (!e.getMessage().contains("already exists"))
            throw e;
      }
      return null;
   }

   @Override
   public String toString() {
      return String.format("addStorageController(%s)", storageController);
   }
}
