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
package org.jclouds.vcloud.domain;

import java.util.Date;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.internal.TaskImpl;

import com.google.inject.ImplementedBy;

/**
 * Whenever the result of a request cannot be returned immediately, the server creates a Task
 * object. Tasks owned by an object such as a vApp or vDC are contained in the Tasks element of the
 * object’s XML representation. This element is read‐only.
 */
@ImplementedBy(TaskImpl.class)
public interface Task extends ReferenceType {
   /**
    * The current status of the task.
    */
   String getOperation();

   /**
    * The current status of the task.
    */
   TaskStatus getStatus();

   /**
    * date and time when the task was started.
    */
   Date getStartTime();

   /**
    * date and time when the task completed. Does not appear for running tasks.
    */
   Date getEndTime();

   /**
    * date and time at which the task expires. By default, tasks expire 24 hours after their start
    * time. Expired tasks cannot be queried.
    */
   Date getExpiryTime();

   /**
    * A link to the object that owns the task. For copy operations, the owner is the copy that is
    * being created. For delete operations, the owner is the deleted object, so this element is not
    * included. For all other operations, the owner is the object to which the request was made.
    */
   ReferenceType getOwner();

   /**
    * error message or related information returned by the task
    */
   @Nullable
   VCloudError getError();

}