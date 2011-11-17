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

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.ISnapshot;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * @author Andrea Turli
 */
public class TakeSnapshotIfNotAlreadyAttached implements Function<IMachine, ISnapshot> {

   
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private VirtualBoxManager manager;
   private String snapshotName;
   private String snapshotDesc;


   public TakeSnapshotIfNotAlreadyAttached(VirtualBoxManager manager, String snapshotName, String snapshotDesc) {
      this.manager = manager;
      this.snapshotName = snapshotName;
      this.snapshotDesc = snapshotDesc;
   }

   @Override
   public ISnapshot apply(@Nullable IMachine machine) {
      // Snapshot a machine
      ISession session = null;
      if(machine.getCurrentSnapshot() == null ) {
         try {
            session = manager.openMachineSession(machine);
            IProgress progress = session.getConsole().takeSnapshot(snapshotName, snapshotDesc);
            if (progress.getCompleted())
               logger.debug("Snapshot %s (description: %s) taken from %s", snapshotName, snapshotDesc, machine.getName());
         } catch (Exception e) {
            logger.error(e, "Problem creating snapshot %s (descripton: %s) from machine %s", snapshotName, snapshotDesc, machine.getName());
            propogate(e);
         } finally {
            session.unlockMachine();
         }
      }
      return machine.getCurrentSnapshot();
   }
   
   protected <T> T propogate(Exception e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }

}
