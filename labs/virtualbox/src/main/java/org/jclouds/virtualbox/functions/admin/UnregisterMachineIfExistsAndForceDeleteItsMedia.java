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
/*
 U * Licensed to jclouds, Inc. (jclouds) under one or more
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

package org.jclouds.virtualbox.functions.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.ErrorCode;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.VBoxException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Singleton
public class UnregisterMachineIfExistsAndForceDeleteItsMedia implements Function<IMachine, Void> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public Void apply(IMachine machine) {
      List<IMedium> mediaToBeDeleted = ImmutableList.of();
      try {
         mediaToBeDeleted = machine.unregister(CleanupMode.Full);
      } catch (VBoxException e) {
         ErrorCode errorCode = ErrorCode.valueOf(e);
         switch (errorCode) {
            case VBOX_E_OBJECT_NOT_FOUND:
               logger.debug("Machine %s does not exists, cannot unregister", machine.getName());
               break;
            default:
               throw e;
         }
      }

      List<IMedium> filteredMediaToBeDeleted = Lists.newArrayList(transform(mediaToBeDeleted,
               new DeleteChildrenOfMedium()));
      if (!filteredMediaToBeDeleted.isEmpty()) {
         try {
            IProgress deletion = machine.delete(filteredMediaToBeDeleted);
            deletion.waitForCompletion(100);
         } catch (Exception e) {
            logger.error(e, "Problem in deleting the media attached to %s", machine.getName());
            Throwables.propagate(e);
         }
      }

      return null;
   }

   private class DeleteChildrenOfMedium implements Function<IMedium, IMedium> {
      @Override
      public IMedium apply(IMedium medium) {
         checkNotNull(medium.getChildren());
         if (medium.getDeviceType().equals(DeviceType.HardDisk)) {
            for (IMedium child : medium.getChildren()) {
               try {
                  IProgress deletion = child.deleteStorage();
                  deletion.waitForCompletion(-1);
               } catch (Exception e) {
                  // work around media that are still attached to other vm's. this can happen when a
                  // running node is used to create a new image and then an attempt at deleting it
                  // is made
                  if (e.getMessage().contains("is still attached to the following")) {
                     logger.warn("Media could not be deleted. Ignoring... [Message %s]", e.getMessage());
                  }
               }
            }
         }
         return medium;
      }

   };

}
