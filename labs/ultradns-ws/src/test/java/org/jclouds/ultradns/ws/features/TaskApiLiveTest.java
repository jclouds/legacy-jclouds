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
package org.jclouds.ultradns.ws.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.ultradns.ws.domain.Task;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "TaskApiLiveTest")
public class TaskApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   private void checkTask(Task task) {
      checkNotNull(task.getGuid(), "Guid cannot be null for a Task %s", task);
      checkNotNull(task.getStatusCode(), "StatusCode cannot be null for a Task %s", task);
      checkNotNull(task.getMessage(), "While Message can be null for a Task, its Optional wrapper cannot %s", task);
      checkNotNull(task.getResultUrl(), "While ResultUrl can be null for a Task, its Optional wrapper cannot %s", task);
   }

   @Test
   public void testListTasks() {
      for (Task task : api().list()) {
         checkTask(task);
      }
   }

   @Test
   public void testGetTask() {
      for (Task task : api().list()) {
         Task got = api().get(task.getGuid());
         assertEquals(got.getGuid(), task.getGuid());
         assertEquals(got.getStatusCode(), task.getStatusCode());
         assertEquals(got.getMessage(), task.getMessage());
         assertEquals(got.getResultUrl(), task.getMessage());
      }
   }

   @Test
   public void testClearTask() {
      String guid = api().runTest("foo");
      checkTask(api().get(guid));
      api().clear(guid);
      assertNull(api().get(guid));
   }

   @Test
   public void testClearTaskWhenNotFound() {
      api().clear("AAAAAAAAAAAAAAAA");
   }

   @Test
   public void testGetTaskWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   protected TaskApi api() {
      return context.getApi().getTaskApi();
   }
}
