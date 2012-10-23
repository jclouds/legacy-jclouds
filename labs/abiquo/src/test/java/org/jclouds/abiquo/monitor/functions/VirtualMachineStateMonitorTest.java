/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.monitor.functions;

import static org.testng.Assert.assertEquals;

import org.easymock.EasyMock;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.google.common.base.Function;

/**
 * Unit tests for the {@link VirtualMachineStateMonitor} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "VirtualMachineStateMonitorTest")
public class VirtualMachineStateMonitorTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullState() {
      new VirtualMachineStateMonitor(null);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullArgument() {
      Function<VirtualMachine, MonitorStatus> function = new VirtualMachineStateMonitor(VirtualMachineState.ON);
      function.apply(null);
   }

   public void testReturnDone() {
      VirtualMachineState[] states = { VirtualMachineState.ON };

      checkStatesReturn(new MockVirtualMachine(), new VirtualMachineStateMonitor(VirtualMachineState.ON), states,
            MonitorStatus.DONE);
   }

   public void testReturnContinue() {
      VirtualMachineState[] states = { VirtualMachineState.ALLOCATED, VirtualMachineState.CONFIGURED,
            VirtualMachineState.LOCKED, VirtualMachineState.OFF, VirtualMachineState.PAUSED,
            VirtualMachineState.NOT_ALLOCATED, VirtualMachineState.UNKNOWN };

      checkStatesReturn(new MockVirtualMachine(), new VirtualMachineStateMonitor(VirtualMachineState.ON), states,
            MonitorStatus.CONTINUE);

      checkStatesReturn(new MockVirtualMachineFailing(), new VirtualMachineStateMonitor(VirtualMachineState.ON),
            states, MonitorStatus.CONTINUE);
   }

   private void checkStatesReturn(final MockVirtualMachine vm, final Function<VirtualMachine, MonitorStatus> function,
         final VirtualMachineState[] states, final MonitorStatus expectedStatus) {
      for (VirtualMachineState state : states) {
         vm.setState(state);
         assertEquals(function.apply(vm), expectedStatus);
      }
   }

   private static class MockVirtualMachine extends VirtualMachine {
      private VirtualMachineState state;

      @SuppressWarnings("unchecked")
      public MockVirtualMachine() {
         super(EasyMock.createMock(RestContext.class), new VirtualMachineWithNodeExtendedDto());
      }

      @Override
      public VirtualMachineState getState() {
         return state;
      }

      public void setState(final VirtualMachineState state) {
         this.state = state;
      }
   }

   private static class MockVirtualMachineFailing extends MockVirtualMachine {
      @Override
      public VirtualMachineState getState() {
         throw new RuntimeException("This mock class always fails to get the state");
      }

   }
}
