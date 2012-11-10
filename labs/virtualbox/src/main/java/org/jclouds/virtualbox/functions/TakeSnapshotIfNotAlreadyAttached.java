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

import java.util.concurrent.TimeUnit;

import org.jclouds.logging.Logger;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.ISnapshot;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * @author Andrea Turli
 */
public class TakeSnapshotIfNotAlreadyAttached implements Function<IMachine, ISnapshot> {

   private Supplier<VirtualBoxManager> manager;
   private String snapshotName;
   private String snapshotDesc;
   private Logger logger;

   public TakeSnapshotIfNotAlreadyAttached(Supplier<VirtualBoxManager> manager, String snapshotName,
            String snapshotDesc, Logger logger) {
      this.manager = manager;
      this.snapshotName = snapshotName;
      this.snapshotDesc = snapshotDesc;
      this.logger = logger;
   }

   @Override
   public ISnapshot apply(IMachine machine) {
      // Snapshot a machine
      ISession session = null;
      ISnapshot snap = machine.getCurrentSnapshot();

      if (snap == null) {
         try {
            session = manager.get().openMachineSession(machine);
            int retries = 10;
            while (true) {
               try {

                  // running machines need to be pause before a snapshot can be taken
                  // due to a vbox bug see https://www.virtualbox.org/ticket/9255
                  boolean paused = false;
                  if (machine.getState() == MachineState.Running) {
                     session.getConsole().pause();
                     paused = true;
                  }

                  IProgress progress = session.getConsole().takeSnapshot(snapshotName, snapshotDesc);
                  progress.waitForCompletion(-1);

                  if (paused) {
                     session.getConsole().resume();
                  }

                  snap = machine.getCurrentSnapshot();
                  logger.debug("<< snapshot(%s) with description(%s) taken from master(%s)", snapshotName, snapshotDesc,
                           machine.getName());
                  break;
               } catch (Exception e) {
                  if (e.getMessage().contains("VirtualBox error: The object is not ready")
                           || e.getMessage().contains("This machine does not have any snapshots")) {
                     retries--;
                     if (retries == 0) {
                        logger.error(e,
                                 "Problem creating snapshot (too many retries) %s (description: %s) from machine %s",
                                 snapshotName, snapshotDesc, machine.getName());
                        throw Throwables.propagate(e);
                     }
                     Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                     continue;
                  }
                  logger.error(e, "Problem creating snapshot %s (description: %s) from machine %s", snapshotName,
                           snapshotDesc, machine.getName());
                  throw Throwables.propagate(e);
               }
            }
         } catch (Exception e) {
            Throwables.propagate(e);
         } finally {
            if (session != null) {
               manager.get().closeMachineSession(session);
            }
         }

      }
      return snap;
   }
}
