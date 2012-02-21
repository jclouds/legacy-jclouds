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
package org.jclouds.vcloud.director.v1_5.predicates;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TaskInErrorStateException;
import org.jclouds.vcloud.director.v1_5.domain.TaskStatus;
import org.jclouds.vcloud.director.v1_5.features.TaskClient;

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

   private final TaskClient taskClient;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TaskSuccess(RestContext<VCloudDirectorClient, VCloudDirectorAsyncClient> context) {
      this.taskClient = context.getApi().getTaskClient();
   }

   public boolean apply(URI taskUri) {
      logger.trace("looking for status on task %s", taskUri);

      Task task = taskClient.getTask(taskUri);
      // perhaps task isn't available, yet
      if (task == null)
         return false;
      logger.trace("%s: looking for status %s: currently: %s", task, TaskStatus.SUCCESS, task.getStatus());
      if (task.getStatus() == TaskStatus.ERROR)
         throw new TaskInErrorStateException(task);
      return task.getStatus() == TaskStatus.SUCCESS;
   }

}
