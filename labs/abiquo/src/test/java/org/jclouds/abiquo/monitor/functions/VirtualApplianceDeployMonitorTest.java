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
import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.google.common.base.Function;

/**
 * Unit tests for the {@link VirtualApplianceDeployMonitor} function.
 * 
 * @author Serafin Sedano
 */
@Test(groups = "unit", testName = "VirtualApplianceDeployMonitorTest")
public class VirtualApplianceDeployMonitorTest {

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullArgument() {
      Function<VirtualAppliance, MonitorStatus> function = new VirtualApplianceDeployMonitor();
      function.apply(null);
   }

   public void testReturnDone() {
      VirtualApplianceState[] states = { VirtualApplianceState.DEPLOYED };

      checkStatesReturn(new MockVirtualAppliance(), new VirtualApplianceDeployMonitor(), states, MonitorStatus.DONE);
   }

   public void testReturnFail() {
      VirtualApplianceState[] states = { VirtualApplianceState.NEEDS_SYNC, VirtualApplianceState.UNKNOWN,
            VirtualApplianceState.NOT_DEPLOYED };

      checkStatesReturn(new MockVirtualAppliance(), new VirtualApplianceDeployMonitor(), states, MonitorStatus.FAILED);
   }

   public void testReturnContinue() {
      VirtualApplianceState[] states = { VirtualApplianceState.LOCKED };

      checkStatesReturn(new MockVirtualAppliance(), new VirtualApplianceDeployMonitor(), states, MonitorStatus.CONTINUE);

      checkStatesReturn(new MockVirtualApplianceFailing(), new VirtualApplianceDeployMonitor(), states,
            MonitorStatus.CONTINUE);
   }

   private void checkStatesReturn(final MockVirtualAppliance vapp,
         final Function<VirtualAppliance, MonitorStatus> function, final VirtualApplianceState[] states,
         final MonitorStatus expectedStatus) {
      for (VirtualApplianceState state : states) {
         vapp.setState(state);
         assertEquals(function.apply(vapp), expectedStatus);
      }
   }

   private static class MockVirtualAppliance extends VirtualAppliance {
      private VirtualApplianceState state;

      @SuppressWarnings("unchecked")
      public MockVirtualAppliance() {
         super(EasyMock.createMock(RestContext.class), new VirtualApplianceDto());
      }

      @Override
      public VirtualApplianceState getState() {
         return state;
      }

      public void setState(final VirtualApplianceState state) {
         this.state = state;
      }
   }

   private static class MockVirtualApplianceFailing extends MockVirtualAppliance {
      @Override
      public VirtualApplianceState getState() {
         throw new RuntimeException("This mock class always fails to get the state");
      }

   }
}
