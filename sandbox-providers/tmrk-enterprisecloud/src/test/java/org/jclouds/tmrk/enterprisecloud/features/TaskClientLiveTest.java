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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.Tasks;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code TaskClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "TaskClientLiveTest")
public class TaskClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getTaskClient();
   }

   private TaskClient client;

   @Test
   public void testGetTasks() {
      // TODO: don't hard-code id
      // TODO: docs say don't parse the href, yet no xml includes "identifier",
      // I suspect we may need to change to URI args as opposed to long
      Tasks response = client.getTasksInEnvironment(77);
      assert null != response;

      assertTrue(response.getTasks().size() >= 0);
      for (Task task : response.getTasks()) {
         assertEquals(client.getTask(task.getHref()), task);
         assert task.getStatus() != Task.Status.UNRECOGNIZED : response;
      }
   }
}
