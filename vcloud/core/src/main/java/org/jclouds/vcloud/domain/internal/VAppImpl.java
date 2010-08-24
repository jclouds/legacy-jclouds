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

package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class VAppImpl extends NamedResourceImpl implements VApp {
   private final Status status;
   @Nullable
   private final NamedResource vdc;
   @Nullable
   private final String description;
   private final List<Task> tasks = Lists.newArrayList();

   public VAppImpl(String name, String type, URI id, Status status, @Nullable NamedResource vdc,
            @Nullable String description, Iterable<Task> tasks) {
      super(name, type, id);
      this.status = checkNotNull(status, "status");
      this.vdc = vdc;
      this.description = description;
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
   }

   @Override
   public Status getStatus() {
      return status;
   }

   @Override
   public NamedResource getVDC() {
      return vdc;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public List<Task> getTasks() {
      return tasks;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
      result = prime * result + ((vdc == null) ? 0 : vdc.hashCode());
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
      VAppImpl other = (VAppImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (tasks == null) {
         if (other.tasks != null)
            return false;
      } else if (!tasks.equals(other.tasks))
         return false;
      if (vdc == null) {
         if (other.vdc != null)
            return false;
      } else if (!vdc.equals(other.vdc))
         return false;
      return true;
   }

   @Override
   public int compareTo(NamedResource o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", name=" + getName() + ", type=" + getType() + ", status=" + getStatus() + ", vdc="
               + getVDC() + ", description=" + getDescription() + "]";
   }

}