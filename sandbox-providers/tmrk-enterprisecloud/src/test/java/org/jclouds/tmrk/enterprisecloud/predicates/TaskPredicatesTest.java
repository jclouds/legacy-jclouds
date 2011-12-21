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
package org.jclouds.tmrk.enterprisecloud.predicates;

import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Date;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.jclouds.tmrk.enterprisecloud.predicates.TaskPredicates.completeOrSuccess;

/**
 * @author Jason King
 */
@Test(groups = "unit", testName = "TaskPredicatesTest")
public class TaskPredicatesTest  {
   
   private Task task;
   
   @BeforeMethod
   public void setUp() {
      task = Task.builder().href(URI.create("")).name("test")
         .operation("no-op")
         .impactedItem(NamedResource.builder().href(URI.create("")).build())
         .startTime(new Date())
         .initiatedBy(NamedResource.builder().href(URI.create("")).build())
         .status(Task.Status.UNRECOGNIZED)
         .build();
   }
   
   public void testCompleteOrSuccess() {
      assertTrue(completeOrSuccess(null).apply(task.toBuilder().status(Task.Status.COMPLETE).build()));
      assertTrue(completeOrSuccess(null).apply(task.toBuilder().status(Task.Status.SUCCESS).build()));
      assertFalse(completeOrSuccess(null).apply(task.toBuilder().status(Task.Status.RUNNING).build()));
      assertFalse(completeOrSuccess(null).apply(task.toBuilder().status(Task.Status.QUEUED).build()));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testCompleteOrSuccessWhenNull() {
      TaskPredicates.completeOrSuccess(null).apply(null);
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testCompleteOrSuccessWhenFailure() {
      TaskPredicates.completeOrSuccess(null).apply(task.toBuilder().status(Task.Status.FAILED).build());
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testCompleteOrSuccessWhenError() {
      TaskPredicates.completeOrSuccess(null).apply(task.toBuilder().status(Task.Status.ERROR).build());
   }
}
