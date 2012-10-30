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
import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkSection;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VAppImpl extends ReferenceTypeImpl implements VApp {

   private final Status status;
   private final ReferenceType vdc;
   @Nullable
   private final String description;
   private final List<Task> tasks = Lists.newArrayList();
   private final boolean ovfDescriptorUploaded;
   private final Set<Vm> children = Sets.newLinkedHashSet();
   @Nullable
   private final VCloudNetworkSection networkSection;
   
   public VAppImpl(String name, String type, URI id, Status status, ReferenceType vdc, @Nullable String description,
            Iterable<Task> tasks, boolean ovfDescriptorUploaded, Iterable<Vm> children,
            @Nullable VCloudNetworkSection networkSection) {
      super(name, type, id);
      this.status = checkNotNull(status, "status");
      this.vdc = vdc;// TODO: once <1.0 is killed check not null
      this.description = description;
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
      this.ovfDescriptorUploaded = ovfDescriptorUploaded;
      Iterables.addAll(this.children, checkNotNull(children, "children"));
      this.networkSection = networkSection; // can be null when copying
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
   public boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Vm> getChildren() {
      return children;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public VCloudNetworkSection getNetworkSection() {
      return networkSection;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (ovfDescriptorUploaded ? 1231 : 1237);
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
      if (ovfDescriptorUploaded != other.ovfDescriptorUploaded)
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
