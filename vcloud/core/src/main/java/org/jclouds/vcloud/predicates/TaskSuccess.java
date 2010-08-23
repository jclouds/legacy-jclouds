/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.predicates;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.CommonVCloudClient;
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

   private final CommonVCloudClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TaskSuccess(CommonVCloudClient client) {
      this.client = client;
   }

   public boolean apply(URI taskId) {
      logger.trace("looking for status on task %s", taskId);

      Task task = client.getTask(taskId);
      logger.trace("%s: looking for status %s: currently: %s", task, TaskStatus.SUCCESS, task
               .getStatus());
      if (task.getStatus() == TaskStatus.ERROR)
         throw new RuntimeException("error on task: " + task.getLocation() + " error: " + task.getError());
      return task.getStatus() == TaskStatus.SUCCESS;
   }

}
