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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Task.Status;
import org.jclouds.vcloud.director.v1_5.features.TaskApi;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Test a {@link Task} status is in a particular set of {@link Task.Status statuses}.
 * 
 * @author grkvlt@apache.org
 */
public class TaskStatusEquals implements Predicate<Task> {

   private final TaskApi taskApi;

   @Resource
   protected Logger logger = Logger.NULL;

   private Collection<Status> expectedStatuses;
   private Collection<Status> failingStatuses;

   public TaskStatusEquals(TaskApi taskApi, Status expectedStatus, Set<Status> failingStatuses) {
      this(taskApi, ImmutableSet.of(expectedStatus), failingStatuses);
   }

   public TaskStatusEquals(TaskApi taskApi, Set<Status> expectedStatuses, Set<Status> failingStatuses) {
      this.taskApi = taskApi;
      this.expectedStatuses = expectedStatuses;
      this.failingStatuses = failingStatuses;
   }

   /** @see Predicate#apply(Object) */
   @Override
   public boolean apply(Task task) {
      checkNotNull(task, "task");
      logger.trace("looking for status on task %s", task);

      // TODO shouldn't we see if it's already done before getting it from API server?
      task = taskApi.get(task.getHref());
      
      // perhaps task isn't available, yet
      if (task == null) return false;
      logger.trace("%s: looking for status %s: currently: %s", task, expectedStatuses, task.getStatus());
      
      if (failingStatuses.contains(task.getStatus())) {
         throw new VCloudDirectorException(task);
      }
      if (expectedStatuses.contains(task.getStatus())) {
         return true;
      }
      return false;
   }

   @Override
   public String toString() {
      return "taskStatusEquals(" + Iterables.toString(expectedStatuses) + ")";
   }
}
