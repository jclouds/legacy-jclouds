/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkTask;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TasksList;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgList;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link TaskApi}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "TaskApiLiveTest")
public class TaskApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   /*
    * Convenience references to API apis.
    */

   private OrgApi orgApi;
   private TaskApi taskApi;
   
   /*
    * Shared state between dependant tests.
    */

   private OrgList orgList;
   private URI orgURI;
   private TasksList taskList;
   private Task task;
   private URI taskURI;
   
   private VApp vApp;

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      orgApi = context.getApi().getOrgApi();
      taskApi = context.getApi().getTaskApi();
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (vApp != null) cleanUpVApp(vApp);
   }

   @Test(description = "GET /tasksList/{id}")
   public void testGetTaskList() {
      orgList = orgApi.list();
      Reference orgRef = Iterables.getFirst(orgList, null);
      assertNotNull(orgRef);
      orgURI = orgRef.getHref();
      
      Org org = orgApi.get(orgURI);
      
      URI taskListHref = Iterables.find(org.getLinks(), LinkPredicates.typeEquals(VCloudDirectorMediaType.TASKS_LIST)).getHref();

      // Call the method being tested
      taskList = taskApi.getTasksList(taskListHref);
      
      // NOTE The environment MUST have ...
      
      // Check required elements and attributes
      assertFalse(Iterables.isEmpty(taskList), String.format(NOT_EMPTY_OBJECT_FMT, "Task", "TaskList"));
      
      for (Task task : taskList) {
         checkTask(task);
      }
   }

   @Test(description = "GET /task/{id}", dependsOnMethods = { "testGetTaskList" })
   public void testGetTask() {
      //TODO: upload media or something so you can get a fresh cancellable task?
      
      Task taskRef = Iterables.getFirst(taskList, null);
      taskURI = taskRef.getHref();

      // Call the method being tested
      task = taskApi.get(taskURI);

      // Check required elements and attributes
      checkTask(task);
   }

   // FIXME cancelTask complains "This task can not be canceled"
   // However, when I do this through the UI, I can cancel the task for instantiating a vApp.
   @Test(description = "POST /task/{id}/action/cancel", dependsOnMethods = { "testGetTask" })
   public void testCancelTask() {
      vApp = instantiateVApp();
      
      Task task = Iterables.getFirst(vApp.getTasks(), null);
      assertNotNull(task, "instantiateVApp should contain one long-running task");
      assertTaskStatusEventually(task, Task.Status.RUNNING, ImmutableSet.of(Task.Status.ERROR, Task.Status.ABORTED));

      // Call the method being tested
      taskApi.cancel(taskURI);
      assertTaskStatusEventually(task, Task.Status.CANCELED, ImmutableSet.of(Task.Status.ERROR, Task.Status.ABORTED, Task.Status.SUCCESS));
   }
}
