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
import org.jclouds.abiquo.domain.cloud.Conversion;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.server.core.appslibrary.ConversionDto;
import com.google.common.base.Function;

/**
 * Unit tests for the {@link ConversionStatusMonitor} function.
 * 
 * @author Sergi Castro
 */
@Test(groups = "unit", testName = "ConversionStatusMonitorTest")
public class ConversionStatusMonitorTest {

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullArgument() {
      Function<Conversion, MonitorStatus> function = new ConversionStatusMonitor();
      function.apply(null);
   }

   public void testReturnDone() {
      ConversionState[] states = { ConversionState.FINISHED };

      checkStatesReturn(new MockConversion(), new ConversionStatusMonitor(), states, MonitorStatus.DONE);
   }

   public void testReturnFail() {
      ConversionState[] states = { ConversionState.FAILED };

      checkStatesReturn(new MockConversion(), new ConversionStatusMonitor(), states, MonitorStatus.FAILED);
   }

   public void testReturnContinue() {
      ConversionState[] states = { ConversionState.ENQUEUED };

      checkStatesReturn(new MockConversion(), new ConversionStatusMonitor(), states, MonitorStatus.CONTINUE);

      checkStatesReturn(new MockConversionFailing(), new ConversionStatusMonitor(), states, MonitorStatus.CONTINUE);
   }

   private void checkStatesReturn(final MockConversion task, final Function<Conversion, MonitorStatus> function,
         final ConversionState[] states, final MonitorStatus expectedStatus) {
      for (ConversionState state : states) {
         task.setState(state);
         assertEquals(function.apply(task), expectedStatus);
      }
   }

   private static class MockConversion extends Conversion {
      @SuppressWarnings("unchecked")
      public MockConversion() {
         super(EasyMock.createMock(RestContext.class), new ConversionDto());
      }

      @Override
      public void refresh() {
         // Do not perform any API call
      }

      public void setState(final ConversionState state) {
         target.setState(state);
      }
   }

   private static class MockConversionFailing extends MockConversion {
      @Override
      public void refresh() {
         throw new RuntimeException("This mock class always fails to refresh");
      }

   }

}
