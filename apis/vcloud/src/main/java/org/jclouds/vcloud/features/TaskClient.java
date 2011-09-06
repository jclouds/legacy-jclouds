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
package org.jclouds.vcloud.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;

/**
 * Provides access to Task functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface TaskClient {

   TasksList getTasksList(URI tasksListId);

   TasksList findTasksListInOrgNamed(String orgName);

   /**
    * Whenever the result of a request cannot be returned immediately, the server creates a Task
    * object and includes it in the response, as a member of the Tasks container in the response
    * body. Each Task has an href value, which is a URL that the client can use to retrieve the Task
    * element alone, without the rest of the response in which it was contained. All information
    * about the task is included in the Task element when it is returned in the response’s Tasks
    * container, so a client does not need to make an additional request to the Task URL unless it
    * wants to follow the progress of a task that was incomplete.
    */
   Task getTask(URI taskId);

   void cancelTask(URI taskId);

}
