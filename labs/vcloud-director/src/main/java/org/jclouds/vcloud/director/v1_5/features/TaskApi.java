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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TasksList;

/**
 * Provides synchronous access to {@link Task} objects.
 * 
 * @see TaskAsyncApi
 * @author grkvlt@apache.org, Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface TaskApi {

   /**
    * Retrieves a list of tasks.
    * 
    * <pre>
    * GET /tasksList/{id}
    * </pre>
    * 
    * @param tasksListUrn
    *           from {@link Org#getLinks()} where {@link Link#getType} is
    *           {@link VCloudDirectorMediaType#TASKS_LIST}
    * @return a list of tasks
    */
   TasksList getTasksList(URI tasksListHref);

   /**
    * Retrieves a task.
    * 
    * <pre>
    * GET /task/{id}
    * </pre>
    * 
    * @return the task or null if not found
    */
   Task get(String taskUrn);

   Task get(URI taskHref);

   /**
    * Cancels a task.
    * 
    * <pre>
    * POST /task/{id}/action/cancel
    * </pre>
    */
   void cancel(String taskUrn);

   void cancel(URI taskHref);
}
