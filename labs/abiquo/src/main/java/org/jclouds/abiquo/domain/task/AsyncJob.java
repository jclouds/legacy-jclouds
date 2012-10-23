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

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.task.Job.JobState;
import com.abiquo.server.core.task.Job.JobType;
import com.abiquo.server.core.task.JobDto;

/**
 * Adds generic high level functionality to {JobDto}.
 * 
 * @author Francesc Montserrat
 */
public class AsyncJob extends DomainWrapper<JobDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected AsyncJob(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final JobDto target) {
      super(context, target);
   }

   // Delegate methods

   public String getDescription() {
      return target.getDescription();
   }

   public String getId() {
      return target.getId();
   }

   public JobState getRollbackState() {
      return target.getRollbackState();
   }

   public JobState getState() {
      return target.getState();
   }

   public long getTimestamp() {
      return target.getTimestamp();
   }

   public JobType getType() {
      return target.getType();
   }

   @Override
   public String toString() {
      return "AsyncJob [id=" + getId() + ", description=" + getDescription() + ", rollbackState=" + getRollbackState()
            + ", state=" + getState() + ", timestamp=" + getTimestamp() + ", type=" + getType() + "]";
   }

}
