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
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.testng.annotations.Test;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.VBoxException;

/**
 * @author Mattias Holmqvist
 */
@Test(groups = "unit", testName = "ApplyMemoryToMachineTest")
public class ApplyMemoryToMachineTest {

   @Test
   public void testSetRAMSizeSuccessful() throws Exception {
      long memorySize = 1024l;
      IMachine machine = createMock(IMachine.class);

      machine.setMemorySize(memorySize);
      machine.saveSettings();

      replay(machine);

      new ApplyMemoryToMachine(memorySize).apply(machine);

      verify(machine);
   }

   @Test(expectedExceptions = VBoxException.class)
   public void testRethrowInvalidRamSizeError() throws Exception {
      // Mainly here for documentation purposes
      final String error = "VirtualBox error: Invalid RAM size: "
            + "3567587327 MB (must be in range [4, 2097152] MB) (0x80070057)";

      long memorySize = 1024l;
      IMachine machine = createMock(IMachine.class);

      VBoxException invalidRamSizeException = new VBoxException(createNiceMock(Throwable.class), error);
      machine.setMemorySize(memorySize);
      expectLastCall().andThrow(invalidRamSizeException);
      machine.saveSettings();

      replay(machine);

      new ApplyMemoryToMachine(memorySize).apply(machine);
   }

}
