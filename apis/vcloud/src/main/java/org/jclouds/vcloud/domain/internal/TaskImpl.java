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
package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.VCloudError;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class TaskImpl extends ReferenceTypeImpl implements Task {

   private final String operation;
   private final TaskStatus status;
   private final Date startTime;
   @Nullable
   private final Date endTime;
   @Nullable
   private final Date expiryTime;
   private final ReferenceType owner;
   @Nullable
   private final VCloudError error;

   public TaskImpl(URI id, String operation, TaskStatus status, Date startTime, @Nullable Date endTime,
            @Nullable Date expiryTime, ReferenceType owner, VCloudError error) {
      super(null, VCloudMediaType.TASK_XML, id);
      this.operation = operation;
      this.status = checkNotNull(status, "status");
      this.startTime = startTime;
      this.endTime = endTime;
      this.expiryTime = expiryTime;
      this.owner = owner;
      this.error = error;
   }

   @Override
   public TaskStatus getStatus() {
      return status;
   }

   @Override
   public Date getStartTime() {
      return startTime;
   }

   @Override
   public ReferenceType getOwner() {
      return owner;
   }

   @Override
   public Date getEndTime() {
      return endTime;
   }

   @Override
   public VCloudError getError() {
      return error;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("href", getHref()).add("name", getName()).add("owner", owner).add(
               "operation", operation).add("startTime", startTime).add("endTime", endTime)
               .add("expiryTime", expiryTime).add("error", error).toString();
   }

   public Date getExpiryTime() {
      return expiryTime;
   }

   @Override
   public String getOperation() {
      return operation;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
      result = prime * result + ((error == null) ? 0 : error.hashCode());
      result = prime * result + ((expiryTime == null) ? 0 : expiryTime.hashCode());
      result = prime * result + ((operation == null) ? 0 : operation.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      TaskImpl other = (TaskImpl) obj;
      if (endTime == null) {
         if (other.endTime != null)
            return false;
      } else if (!endTime.equals(other.endTime))
         return false;
      if (error == null) {
         if (other.error != null)
            return false;
      } else if (!error.equals(other.error))
         return false;
      if (expiryTime == null) {
         if (other.expiryTime != null)
            return false;
      } else if (!expiryTime.equals(other.expiryTime))
         return false;
      if (operation == null) {
         if (other.operation != null)
            return false;
      } else if (!operation.equals(other.operation))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (startTime == null) {
         if (other.startTime != null)
            return false;
      } else if (!startTime.equals(other.startTime))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

}