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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.concurrent.Timeout;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.Tasks;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Task.
 * <p/>
 * 
 * @see TaskAsyncClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface TaskClient {

   /**
    * The Get Tasks call returns information regarding the tasks in an
    * environment. The task list is a history of changes to the environment.
    * 
    * @return a history of changes to the environment.
    */
   Tasks getTasksInEnvironment(URI environmentUri);

   /**
    * The Get Tasks by ID call returns information regarding a specified task in
    * an environment.
    * 
    * @return the task or null if not found
    */
   Task getTask(URI taskUri);

   /**
    * The Get Tasks by Virtual Machine call returns information regarding tasks
    * for a specified virtual machine in an environment.
    * @param uri The uri corresponding to the tasks. e.g. /cloudapi/ecloud/tasks/virtualmachines/{id}
    * @return Tasks
    */
   Tasks getTasksByVirtualMachine(URI uri);

}
