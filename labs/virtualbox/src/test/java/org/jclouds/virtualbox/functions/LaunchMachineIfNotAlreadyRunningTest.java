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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.jclouds.virtualbox.domain.ExecutionType;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.SessionState;
import org.virtualbox_4_2.VirtualBoxManager;

@Test(groups = "unit", testName = "LaunchMachineIfNotAlreadyRunningTest")
public class LaunchMachineIfNotAlreadyRunningTest {

   @Test
   public void testDoNotLaunchIfAlreadyRunning() throws Exception {

   }

   // VirtualBox error: The given session is busy (0x80BB0007)
   // VirtualBox error: The machine
   // 'jclouds-image-virtualbox-iso-to-machine-test' is not registered
   // (0x8000FFFF)

   @Test
   public void testLaunchIfNotStarted() throws Exception {

      final String type = "gui";
      final String environment = "";
      ISession session = createMock(ISession.class);
      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IProgress progress = createMock(IProgress.class);

      expect(manager.getSessionObject()).andReturn(session).anyTimes();
      expect(machine.launchVMProcess(session, type, environment)).andReturn(progress);
      progress.waitForCompletion(-1);
      expect(session.getState()).andReturn(SessionState.Locked);
      session.unlockMachine();

      replay(manager, machine, session, progress);

      new LaunchMachineIfNotAlreadyRunning(manager, ExecutionType.GUI, "").apply(machine);

      verify(manager, machine, session, progress);

   }
}
