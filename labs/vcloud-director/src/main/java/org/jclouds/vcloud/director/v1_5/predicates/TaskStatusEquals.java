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
package org.jclouds.vcloud.director.v1_5.predicates;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Task.Status;
import org.jclouds.vcloud.director.v1_5.features.TaskClient;

import com.google.common.base.Predicate;

/**
 * Test a {@link Task} to see if it has succeeded.
 * 
 * @author grkvlt@apache.org
 */
public class TaskStatusEquals implements Predicate<Task> {

   private final TaskClient taskClient;

   @Resource
   protected Logger logger = Logger.NULL;

   private Collection<Status> expectedStatuses;
   private Collection<Status> failingStatuses;

   public TaskStatusEquals(TaskClient taskClient, Status expectedStatus, Set<Status> failingStatuses) {
      this(taskClient, Collections.singleton(expectedStatus), failingStatuses);
   }

   public TaskStatusEquals(TaskClient taskClient, Set<Status> expectedStatuses, Set<Status> failingStatuses) {
      this.taskClient = taskClient;
      this.expectedStatuses = expectedStatuses;
      this.failingStatuses = failingStatuses;
   }

   /** @see Predicate#apply(Object) */
   @Override
   public boolean apply(Task task) {
      logger.trace("looking for status on task %s", task);

      // TODO shouldn't we see if it's already done before getting it from API server?
      task = taskClient.getTask(task.getHref());
      
      // perhaps task isn't available, yet
      if (task == null) return false;
      logger.trace("%s: looking for status %s: currently: %s", task, expectedStatuses, task.getStatus());
      
      for (Status failingStatus : failingStatuses) {
         if (task.getStatus().equals(failingStatus)) {
            throw new VCloudDirectorException(task);
         }
      }
      
      for (Status expectedStatus : expectedStatuses) {
         if (task.getStatus().equals(expectedStatus)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return "checkTaskSuccess()";
   }
}
