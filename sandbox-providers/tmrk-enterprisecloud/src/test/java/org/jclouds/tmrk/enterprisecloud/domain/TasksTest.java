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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Jason King
 */
@Test(groups = "unit", testName = "TasksTest")
public class TasksTest {

   private Task task;
   private Tasks tasks;

   @BeforeMethod()
   public void setUp() throws URISyntaxException {
      task = Task
         .builder()
         .href(URI.create("/tasks/1002"))
         .type("application/vnd.tmrk.cloud.task")
         .operation("test task")
         .status(Task.Status.ERROR)
         .impactedItem(
               NamedResource.builder().href(URI.create("/item/1")).name("sample item 1")
                     .type("application/vnd.tmrk.cloud.nodeService").build())
         .startTime(new Date())
         .completedTime(new Date())
         .notes("Some notes about the operation.")
         .errorMessage("sample error message 1 here")
         .initiatedBy(
               NamedResource.builder().href(URI.create("/users/1")).name("User 1")
                     .type("application/vnd.tmrk.cloud.admin.user").build()).build();

      tasks = Tasks.builder().addTask(task).build();
   }

   @Test
   public void testAddAction() throws URISyntaxException {
      Task task2 = Task
         .builder()
         .href(URI.create("/tasks/1003"))
         .type("application/vnd.tmrk.cloud.task")
         .operation("test task 2")
         .status(Task.Status.ERROR)
         .impactedItem(
               NamedResource.builder().href(URI.create("/item/2")).name("sample item 2")
                     .type("application/vnd.tmrk.cloud.nodeService").build())
         .startTime(new Date())
         .completedTime(new Date())
         .notes("Some notes about the operation.")
         .errorMessage("sample error message 2 here")
         .initiatedBy(
               NamedResource.builder().href(URI.create("/users/2")).name("User 2")
                     .type("application/vnd.tmrk.cloud.admin.user").build()).build();

      Tasks twoTasks = tasks.toBuilder().addTask(task2).build();
      Set<Task> taskSet = twoTasks.getTasks();

      assertEquals(2,taskSet.size());
      assertTrue(taskSet.contains(task));
      assertTrue(taskSet.contains(task2));
   }
}
