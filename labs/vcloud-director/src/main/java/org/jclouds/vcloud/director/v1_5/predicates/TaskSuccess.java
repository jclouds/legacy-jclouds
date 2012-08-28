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

import java.util.EnumSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.features.TaskApi;

import com.google.common.base.Predicate;

/**
 * Test a {@link Task} to see if it has {@link Task.Status#SUCCESS succeeded}.
 * 
 * @author grkvlt@apache.org
 */
@Singleton
public class TaskSuccess implements Predicate<Task> {

   private final TaskApi taskApi;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TaskSuccess(TaskApi taskApi) {
      this.taskApi = taskApi;
   }

   /** @see Predicate#apply(Object) */
   @Override
   public boolean apply(Task task) {
      checkNotNull(task, "task");
      logger.trace("looking for status on task %s", task.getOperationName());

      // TODO shouldn't we see if it's already done before getting it from API server?
      task = taskApi.get(task.getHref());
      
      // perhaps task isn't available, yet
      if (task == null) return false;

      logger.trace("%s: looking for status %s: currently: %s", task.getOperationName(), Task.Status.SUCCESS, task.getStatus());
      if (EnumSet.of(Task.Status.ERROR, Task.Status.CANCELED, Task.Status.ABORTED).contains(task.getStatus())) {
         throw new VCloudDirectorException(task);
      } else return task.getStatus().equals(Task.Status.SUCCESS);
   }

   @Override
   public String toString() {
      return "checkTaskSuccess()";
   }
}
