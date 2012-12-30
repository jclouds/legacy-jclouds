/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;

/**
 * Provides synchronous access to Abiquo Task API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see TaskAsyncApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public interface TaskApi {
   /*********************** Task ***********************/

   /**
    * Get a task from its link.
    * 
    * @param link
    *           The link of the task.
    * @return The task.
    */
   TaskDto getTask(final RESTLink link);

   /**
    * Get the list of tasks of the given object.
    * 
    * @param dto
    *           The object.
    * @return The list of tasks for the given object.
    */
   <T extends SingleResourceTransportDto> TasksDto listTasks(T dto);
}
