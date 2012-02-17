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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.*;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.*;
import static org.testng.Assert.*;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TasksList;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link TaskClient}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "apitests" }, testName = "TaskClientLiveTest")
public class TaskClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private OrgClient orgClient;
   private TaskClient taskClient;

   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      orgClient = context.getApi().getOrgClient();
      taskClient = context.getApi().getTaskClient();
   }

   /*
    * Shared state between dependant tests.
    */

   private OrgList orgList;
   private Reference orgRef;
   private TasksList taskList;
   private Task task;
   private URI taskUri;

   @Test(testName = "GET /tasksList/{id}")
   public void testGetTaskList() {
      orgList = orgClient.getOrgList();
      orgRef = Iterables.getFirst(orgList.getOrgs(), null);
      
      // Call the method being tested
      taskList = taskClient.getTaskList(orgRef);
      
      // NOTE The environment MUST have ...
      
      // Check required elements and attributes
      assertFalse(Iterables.isEmpty(taskList.getTasks()), String.format(NOT_EMPTY_OBJECT_FMT, "Task", "TaskList"));
      
      for (Task task : taskList.getTasks()) {
         checkTask(task);
      }
   }

   @Test(testName = "GET /task/{id}", dependsOnMethods = { "testGetTaskList" })
   public void testGetTask() {
      taskUri = Iterables.getFirst(taskList.getTasks(), null).getHref();

      // Call the method being tested
      task = taskClient.getTask(taskUri);

      // Check required elements and attributes
      checkTask(task);
   }
   
   @Test(testName = "GET /task/{id}/metadata/", dependsOnMethods = { "testGetTask" })
   public void testCancelTask() {
      // Call the method being tested
      taskClient.cancelTask(taskUri);
   }
}
