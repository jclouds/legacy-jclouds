/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.rest.domain.Link;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.joda.time.DateTime;

import com.google.inject.internal.Nullable;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TaskImpl implements Task {
   private final String type;
   private final URI location;
   private final TaskStatus status;
   private final DateTime startTime;
   @Nullable
   private final DateTime endTime;
   private final Link owner;
   @Nullable
   private final Link result;

   public TaskImpl(String type, URI location, TaskStatus status, DateTime startTime,
            @Nullable DateTime endTime, Link owner, @Nullable Link result) {
      this.type = checkNotNull(type, "type");
      this.location = checkNotNull(location, "location");
      this.status = checkNotNull(status, "status");
      this.startTime = checkNotNull(startTime, "startTime");
      this.endTime = endTime;
      this.owner = checkNotNull(owner, "owner");
      this.result = result;
   }

   public TaskStatus getStatus() {
      return status;
   }

   public DateTime getStartTime() {
      return startTime;
   }

   public Link getOwner() {
      return owner;
   }

   public Link getResult() {
      return result;
   }

   public DateTime getEndTime() {
      return endTime;
   }

   public int compareTo(Task o) {
      return (this == o) ? 0 : getStartTime().compareTo(o.getStartTime());
   }

   public String getType() {
      return type;
   }

   public URI getLocation() {
      return location;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TaskImpl other = (TaskImpl) obj;
      if (endTime == null) {
         if (other.endTime != null)
            return false;
      } else if (!endTime.equals(other.endTime))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (result == null) {
         if (other.result != null)
            return false;
      } else if (!result.equals(other.result))
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
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

}