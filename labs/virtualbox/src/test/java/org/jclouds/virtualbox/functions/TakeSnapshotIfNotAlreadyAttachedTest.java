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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.jclouds.logging.Logger;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IConsole;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.ISnapshot;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Suppliers;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "TakeSnapshotIfNotAlreadyAttachedTest")
public class TakeSnapshotIfNotAlreadyAttachedTest {

   @Test
   public void testTakeSnapshotIfNotAlreadyAttached() throws Exception {

      String snapshotName = "snap";
      String snapshotDesc = "snapDesc";

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      ISession session = createMock(ISession.class);
      IConsole console = createNiceMock(IConsole.class);
      IProgress progress = createNiceMock(IProgress.class);
      ISnapshot snapshot = createNiceMock(ISnapshot.class);
      expect(machine.getCurrentSnapshot()).andReturn(snapshot).anyTimes();
      expect(machine.getState()).andReturn(MachineState.PoweredOff).anyTimes();

      expect(manager.openMachineSession(machine)).andReturn(session);

      expect(session.getConsole()).andReturn(console);
      expect(console.takeSnapshot(snapshotName, snapshotDesc)).andReturn(
            progress);
      expect(progress.getCompleted()).andReturn(true);

      session.unlockMachine();
      replay(manager, machine, vBox, session, console, progress);

      new TakeSnapshotIfNotAlreadyAttached(Suppliers.ofInstance(manager), snapshotName, snapshotDesc, Logger.CONSOLE)
            .apply(machine);

      verify(machine);
   }

   @Test
   public void testDoNothingIfAlreadyTakenSnapshot() throws Exception {
      String snapshotName = "snap";
      String snapshotDesc = "snapDesc";

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      ISession session = createMock(ISession.class);
      IConsole console = createNiceMock(IConsole.class);

      IProgress progress = createNiceMock(IProgress.class);
      expect(progress.getCompleted()).andReturn(true);
      expect(machine.getCurrentSnapshot()).andReturn(null).anyTimes();
      expect(manager.openMachineSession(machine)).andReturn(session);
      expect(machine.getState()).andReturn(MachineState.PoweredOff).anyTimes();

      expect(machine.getName()).andReturn("machine").anyTimes();
      expect(session.getConsole()).andReturn(console);
      expect(console.takeSnapshot(snapshotName, snapshotDesc)).andReturn(
            progress);

      session.unlockMachine();
      replay(manager, machine, vBox, session, console, progress);

      new TakeSnapshotIfNotAlreadyAttached(Suppliers.ofInstance(manager), snapshotName, snapshotDesc, Logger.CONSOLE)
            .apply(machine);

      verify(machine);

   }

}
