/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.vdc/licenses/LICENSE-2.0
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
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VAppTemplateImpl extends ReferenceTypeImpl implements VAppTemplate {

   private final Status status;
   private final ReferenceType vdc;
   @Nullable
   private final String description;
   private final List<Task> tasks = Lists.newArrayList();
   private final boolean ovfDescriptorUploaded;
   private final String vAppScopedLocalId;
   private final Set<Vm> children = Sets.newLinkedHashSet();

   public VAppTemplateImpl(String name, String type, URI id, Status status, ReferenceType vdc,
            @Nullable String description, Iterable<Task> tasks, boolean ovfDescriptorUploaded,
            @Nullable String vAppScopedLocalId, Iterable<? extends Vm> children) {
      super(name, type, id);
      this.status = checkNotNull(status, "status");
      this.vdc = vdc;// TODO: once <1.0 is killed check not null
      this.description = description;
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
      this.vAppScopedLocalId = vAppScopedLocalId;
      this.ovfDescriptorUploaded = ovfDescriptorUploaded;
      Iterables.addAll(this.children, checkNotNull(children, "children"));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Status getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ReferenceType getVDC() {
      return vdc;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<Task> getTasks() {
      return tasks;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<? extends Vm> getChildren() {
      return children;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (ovfDescriptorUploaded ? 1231 : 1237);
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
      result = prime * result + ((vAppScopedLocalId == null) ? 0 : vAppScopedLocalId.hashCode());
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
      VAppTemplateImpl other = (VAppTemplateImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (ovfDescriptorUploaded != other.ovfDescriptorUploaded)
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
      if (vAppScopedLocalId == null) {
         if (other.vAppScopedLocalId != null)
            return false;
      } else if (!vAppScopedLocalId.equals(other.vAppScopedLocalId))
         return false;
      if (vdc == null) {
         if (other.vdc != null)
            return false;
      } else if (!vdc.equals(other.vdc))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getHref() + ", name=" + getName() + ", vdc=" + vdc + ", description=" + description + ", status="
               + status + "]";
   }

}