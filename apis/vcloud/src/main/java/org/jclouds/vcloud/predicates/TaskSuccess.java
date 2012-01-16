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
package org.jclouds.vcloud.predicates;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.TaskInErrorStateException;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class TaskSuccess implements Predicate<URI> {

   private final VCloudClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TaskSuccess(VCloudClient client) {
      this.client = client;
   }

   public boolean apply(URI taskId) {
      logger.trace("looking for status on task %s", taskId);

      Task task = client.getTaskClient().getTask(taskId);
      // perhaps task isn't available, yet
      if (task == null)
         return false;
      logger.trace("%s: looking for status %s: currently: %s", task, TaskStatus.SUCCESS, task.getStatus());
      if (task.getStatus() == TaskStatus.ERROR)
         throw new TaskInErrorStateException(task);
      return task.getStatus() == TaskStatus.SUCCESS;
   }

}
