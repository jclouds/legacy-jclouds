/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.util.Map;

import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Quota;
import org.jclouds.vcloud.domain.VDC;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VDCImpl implements VDC {

   private final String id;
   private final String name;
   private final URI location;
   private final String description;
   private final Capacity storageCapacity;
   private final Capacity cpuCapacity;
   private final Capacity memoryCapacity;
   private final Quota instantiatedVmsQuota;
   private final Quota deployedVmsQuota;
   private final Map<String, NamedResource> availableNetworks;
   private final Map<String, NamedResource> resourceEntities;

   public VDCImpl(String id, String name, URI location, String description,
            Capacity storageCapacity, Capacity cpuCapacity, Capacity memoryCapacity,
            Quota instantiatedVmsQuota, Quota deployedVmsQuota,
            Map<String, NamedResource> resourceEntities,
            Map<String, NamedResource> availableNetworks) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.location = checkNotNull(location, "location");
      this.description = description;
      this.storageCapacity = storageCapacity;
      this.cpuCapacity = cpuCapacity;
      this.memoryCapacity = memoryCapacity;
      this.instantiatedVmsQuota = instantiatedVmsQuota;
      this.deployedVmsQuota = deployedVmsQuota;
      this.availableNetworks = checkNotNull(availableNetworks, "availableNetworks");
      this.resourceEntities = checkNotNull(resourceEntities, "resourceEntities");
   }

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public URI getLocation() {
      return location;
   }

   public Map<String, NamedResource> getAvailableNetworks() {
      return availableNetworks;
   }

   public Map<String, NamedResource> getResourceEntities() {
      return resourceEntities;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((availableNetworks == null) ? 0 : availableNetworks.hashCode());
      result = prime * result + ((cpuCapacity == null) ? 0 : cpuCapacity.hashCode());
      result = prime * result + ((deployedVmsQuota == null) ? 0 : deployedVmsQuota.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result
               + ((instantiatedVmsQuota == null) ? 0 : instantiatedVmsQuota.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((memoryCapacity == null) ? 0 : memoryCapacity.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((resourceEntities == null) ? 0 : resourceEntities.hashCode());
      result = prime * result + ((storageCapacity == null) ? 0 : storageCapacity.hashCode());
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
      VDCImpl other = (VDCImpl) obj;
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
      if (deployedVmsQuota == null) {
         if (other.deployedVmsQuota != null)
            return false;
      } else if (!deployedVmsQuota.equals(other.deployedVmsQuota))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (instantiatedVmsQuota == null) {
         if (other.instantiatedVmsQuota != null)
            return false;
      } else if (!instantiatedVmsQuota.equals(other.instantiatedVmsQuota))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (memoryCapacity == null) {
         if (other.memoryCapacity != null)
            return false;
      } else if (!memoryCapacity.equals(other.memoryCapacity))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (resourceEntities == null) {
         if (other.resourceEntities != null)
            return false;
      } else if (!resourceEntities.equals(other.resourceEntities))
         return false;
      if (storageCapacity == null) {
         if (other.storageCapacity != null)
            return false;
      } else if (!storageCapacity.equals(other.storageCapacity))
         return false;
      return true;
   }

   public String getDescription() {
      return description;
   }

   public Capacity getStorageCapacity() {
      return storageCapacity;
   }

   public Capacity getCpuCapacity() {
      return cpuCapacity;
   }

   public Capacity getMemoryCapacity() {
      return memoryCapacity;
   }

   public Quota getInstantiatedVmsQuota() {
      return instantiatedVmsQuota;
   }

   public Quota getDeployedVmsQuota() {
      return deployedVmsQuota;
   }

   @Override
   public String getType() {
      return VCloudMediaType.VDC_XML;
   }

   @Override
   public int compareTo(NamedResource o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", description=" + description + "]";
   }
}