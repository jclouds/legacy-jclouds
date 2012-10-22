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
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.enums.TaskState;
import com.google.common.base.Function;

/**
 * Unit tests for the {@link AsyncTaskStatusMonitor} function.
 * 
 * @author Serafin Sedano
 */
@Test(groups = "unit", testName = "AsyncTaskStatusMonitorTest")
public class AsyncTaskStatusMonitorTest {

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullArgument() {
      Function<AsyncTask, MonitorStatus> function = new AsyncTaskStatusMonitor();
      function.apply(null);
   }

   public void testReturnDone() {
      TaskState[] states = { TaskState.FINISHED_SUCCESSFULLY };

      checkStatesReturn(new MockAsyncTask(), new AsyncTaskStatusMonitor(), states, MonitorStatus.DONE);
   }

   public void testReturnFail() {
      TaskState[] states = { TaskState.ABORTED, TaskState.FINISHED_UNSUCCESSFULLY };

      checkStatesReturn(new MockAsyncTask(), new AsyncTaskStatusMonitor(), states, MonitorStatus.FAILED);
   }

   public void testReturnContinue() {
      TaskState[] states = { TaskState.STARTED, TaskState.PENDING };

      checkStatesReturn(new MockAsyncTask(), new AsyncTaskStatusMonitor(), states, MonitorStatus.CONTINUE);

      checkStatesReturn(new MockAsyncTaskFailing(), new AsyncTaskStatusMonitor(), states, MonitorStatus.CONTINUE);
   }

   private void checkStatesReturn(final MockAsyncTask task, final Function<AsyncTask, MonitorStatus> function,
         final TaskState[] states, final MonitorStatus expectedStatus) {
      for (TaskState state : states) {
         task.setState(state);
         assertEquals(function.apply(task), expectedStatus);
      }
   }

   private static class MockAsyncTask extends AsyncTask {
      @SuppressWarnings("unchecked")
      public MockAsyncTask() {
         super(EasyMock.createMock(RestContext.class), new TaskDto());
      }

      @Override
      public void refresh() {
         // Do not perform any API call
      }

      public void setState(final TaskState state) {
         target.setState(state);
      }
   }

   private static class MockAsyncTaskFailing extends MockAsyncTask {
      @Override
      public void refresh() {
         throw new RuntimeException("This mock class always fails to refresh");
      }

   }

}
