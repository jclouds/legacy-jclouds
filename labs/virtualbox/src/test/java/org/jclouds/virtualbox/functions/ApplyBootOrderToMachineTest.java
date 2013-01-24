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

import com.google.common.collect.ImmutableMap;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.VBoxException;

import java.util.Map;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "ApplyBootOrderToMachineTest")
public class ApplyBootOrderToMachineTest {

   @Test
   public void testSetBootOrderSuccessful() throws Exception {
      Map<Long, DeviceType> positionAndDeviceType = ImmutableMap.of(1l, DeviceType.HardDisk);
      IMachine machine = createMock(IMachine.class);
      for(long position : positionAndDeviceType.keySet()) {
         machine.setBootOrder(position, positionAndDeviceType.get(position));
      }
      machine.saveSettings();
      replay(machine);
      new ApplyBootOrderToMachine(positionAndDeviceType).apply(machine);
      verify(machine);
   }

}
