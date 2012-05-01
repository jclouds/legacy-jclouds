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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.VDCStatus;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VDCImpl extends ReferenceTypeImpl implements VDC {

   private final VDCStatus status;
   private final ReferenceType org;
   @Nullable
   private final String description;
   private final List<Task> tasks = Lists.newArrayList();
   private final AllocationModel allocationModel;
   private final Capacity storageCapacity;
   private final Capacity cpuCapacity;
   private final Capacity memoryCapacity;
   private final Map<String, ReferenceType> resourceEntities = Maps.newLinkedHashMap();
   private final Map<String, ReferenceType> availableNetworks = Maps.newLinkedHashMap();
   private final int nicQuota;
   private final int networkQuota;
   private final int vmQuota;
   private final boolean isEnabled;

   public VDCImpl(String name, String type, URI id, VDCStatus status, ReferenceType org, @Nullable String description,
            Iterable<Task> tasks, AllocationModel allocationModel, @Nullable Capacity storageCapacity,
            @Nullable Capacity cpuCapacity, @Nullable Capacity memoryCapacity,
            Map<String, ReferenceType> resourceEntities, Map<String, ReferenceType> availableNetworks, int nicQuota,
            int networkQuota, int vmQuota, boolean isEnabled) {
      super(name, type, id);
      this.status = checkNotNull(status, "status");
      this.org = org;// TODO: once <1.0 is killed check not null
      this.description = description;
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
      this.allocationModel = checkNotNull(allocationModel, "allocationModel");
      this.storageCapacity = storageCapacity;// TODO: once <1.0 is killed check not null
      this.cpuCapacity = cpuCapacity;// TODO: once <1.0 is killed check not null
      this.memoryCapacity = memoryCapacity;// TODO: once <1.0 is killed check not null
      this.resourceEntities.putAll(checkNotNull(resourceEntities, "resourceEntities"));
      this.availableNetworks.putAll(checkNotNull(availableNetworks, "availableNetworks"));
      this.nicQuota = nicQuota;
      this.networkQuota = networkQuota;
      this.vmQuota = vmQuota;
      this.isEnabled = isEnabled;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VDCStatus getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ReferenceType getOrg() {
      return org;
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
   public AllocationModel getAllocationModel() {
      return allocationModel;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Capacity getStorageCapacity() {
      return storageCapacity;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Capacity getCpuCapacity() {
      return cpuCapacity;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Capacity getMemoryCapacity() {
      return memoryCapacity;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, ReferenceType> getResourceEntities() {
      return resourceEntities;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, ReferenceType> getAvailableNetworks() {
      return availableNetworks;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getNicQuota() {
      return nicQuota;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getNetworkQuota() {
      return networkQuota;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getVmQuota() {
      return vmQuota;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isEnabled() {
      return isEnabled;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((allocationModel == null) ? 0 : allocationModel.hashCode());
      result = prime * result + ((availableNetworks == null) ? 0 : availableNetworks.hashCode());
      result = prime * result + ((cpuCapacity == null) ? 0 : cpuCapacity.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + (isEnabled ? 1231 : 1237);
      result = prime * result + ((memoryCapacity == null) ? 0 : memoryCapacity.hashCode());
      result = prime * result + networkQuota;
      result = prime * result + nicQuota;
      result = prime * result + ((org == null) ? 0 : org.hashCode());
      result = prime * result + ((resourceEntities == null) ? 0 : resourceEntities.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((storageCapacity == null) ? 0 : storageCapacity.hashCode());
      result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
      result = prime * result + vmQuota;
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
      VDCImpl other = (VDCImpl) obj;
      if (allocationModel == null) {
         if (other.allocationModel != null)
            return false;
      } else if (!allocationModel.equals(other.allocationModel))
         return false;
      if (availableNetworks == null) {
         if (other.availableNetworks != null)
            return false;
      } else if (!availableNetworks.equals(other.availableNetworks))
         return false;
      if (cpuCapacity == null) {
         if (other.cpuCapacity != null)
            return false;
      } else if (!cpuCapacity.equals(other.cpuCapacity))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (isEnabled != other.isEnabled)
         return false;
      if (memoryCapacity == null) {
         if (other.memoryCapacity != null)
            return false;
      } else if (!memoryCapacity.equals(other.memoryCapacity))
         return false;
      if (networkQuota != other.networkQuota)
         return false;
      if (nicQuota != other.nicQuota)
         return false;
      if (org == null) {
         if (other.org != null)
            return false;
      } else if (!org.equals(other.org))
         return false;
      if (resourceEntities == null) {
         if (other.resourceEntities != null)
            return false;
      } else if (!resourceEntities.equals(other.resourceEntities))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (storageCapacity == null) {
         if (other.storageCapacity != null)
            return false;
      } else if (!storageCapacity.equals(other.storageCapacity))
         return false;
      if (tasks == null) {
         if (other.tasks != null)
            return false;
      } else if (!tasks.equals(other.tasks))
         return false;
      if (vmQuota != other.vmQuota)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getHref() + ", name=" + getName() + ", org=" + org + ", description=" + description + ", status="
               + status + ", isEnabled=" + isEnabled + "]";
   }

}