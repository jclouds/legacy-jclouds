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

package org.jclouds.abiquo.domain.task;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.rest.RestContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.enums.TaskState;
import com.abiquo.server.core.task.enums.TaskType;

/**
 * Adds generic high level functionality to {TaskDto}.
 * 
 * @author Francesc Montserrat
 */
public class AsyncTask extends DomainWrapper<TaskDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected AsyncTask(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final TaskDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Refresh the state of the task.
    */
   public void refresh() {
      RESTLink self = checkNotNull(target.searchLink("self"), ValidationErrors.MISSING_REQUIRED_LINK + "self");

      target = context.getApi().getTaskApi().getTask(self);
   }

   // Children access

   /**
    * Get the individual jobs that compose the current task.
    */
   public List<AsyncJob> getJobs() {
      return wrap(context, AsyncJob.class, target.getJobs().getCollection());
   }

   // Delegate methods

   public String getOwnerId() {
      return target.getOwnerId();
   }

   public TaskState getState() {
      return target.getState();
   }

   public String getTaskId() {
      return target.getTaskId();
   }

   public long getTimestamp() {
      return target.getTimestamp();
   }

   public TaskType getType() {
      return target.getType();
   }

   public String getUserId() {
      return target.getUserId();
   }

   @Override
   public String toString() {
      return "AsyncTask [taskId=" + getTaskId() + ", ownerId=" + getOwnerId() + ", timestamp=" + getTimestamp()
            + ", userId=" + getUserId() + ", state=" + getState() + ", type=" + getType() + "]";
   }
}
