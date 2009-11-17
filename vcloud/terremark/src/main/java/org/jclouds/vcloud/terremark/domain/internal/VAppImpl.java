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
package org.jclouds.vcloud.terremark.domain.internal;

import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.SortedSet;

import org.jclouds.rest.domain.Link;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.terremark.domain.ResourceAllocation;
import org.jclouds.vcloud.terremark.domain.ResourceType;
import org.jclouds.vcloud.terremark.domain.VApp;
import org.jclouds.vcloud.terremark.domain.VirtualSystem;

import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VAppImpl extends NamedResourceImpl implements VApp {

   private final VAppStatus status;
   private final long size;
   private final Link vDC;
   private final Link computeOptions;
   private final Link customizationOptions;
   private final ListMultimap<String, InetAddress> networkToAddresses;
   private final String operatingSystemDescription;
   private final VirtualSystem system;
   private final SortedSet<ResourceAllocation> resourceAllocations;
   private final Map<ResourceType, ResourceAllocation> resourceAllocationByType;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public VAppImpl(String id, String name, String type, URI location, VAppStatus status, long size,
            Link vDC, Link computeOptions, Link customizationOptions,
            ListMultimap<String, InetAddress> networkToAddresses,
            String operatingSystemDescription, VirtualSystem system,
            SortedSet<ResourceAllocation> resourceAllocations) {
      super(id, name, type, location);
      this.status = status;
      this.size = size;
      this.vDC = vDC;
      this.computeOptions = computeOptions;
      this.customizationOptions = customizationOptions;
      this.networkToAddresses = networkToAddresses;
      this.operatingSystemDescription = operatingSystemDescription;
      this.system = system;
      this.resourceAllocations = resourceAllocations;
      resourceAllocationByType = Maps.uniqueIndex(resourceAllocations,
               new Function<ResourceAllocation, ResourceType>() {
                  @Override
                  public ResourceType apply(ResourceAllocation from) {
                     return from.getResourceType();
                  }
               });
   }

   public VAppStatus getStatus() {
      return status;
   }

   public long getSize() {
      return size;
   }

   public Link getVDC() {
      return vDC;
   }

   public Link getComputeOptions() {
      return computeOptions;
   }

   public Link getCustomizationOptions() {
      return customizationOptions;
   }

   public ListMultimap<String, InetAddress> getNetworkToAddresses() {
      return networkToAddresses;
   }

   public String getOperatingSystemDescription() {
      return operatingSystemDescription;
   }

   public VirtualSystem getSystem() {
      return system;
   }

   public SortedSet<ResourceAllocation> getResourceAllocations() {
      return resourceAllocations;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((computeOptions == null) ? 0 : computeOptions.hashCode());
      result = prime * result
               + ((customizationOptions == null) ? 0 : customizationOptions.hashCode());
      result = prime * result + ((networkToAddresses == null) ? 0 : networkToAddresses.hashCode());
      result = prime * result
               + ((operatingSystemDescription == null) ? 0 : operatingSystemDescription.hashCode());
      result = prime * result
               + ((resourceAllocationByType == null) ? 0 : resourceAllocationByType.hashCode());
      result = prime * result
               + ((resourceAllocations == null) ? 0 : resourceAllocations.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((system == null) ? 0 : system.hashCode());
      result = prime * result + ((vDC == null) ? 0 : vDC.hashCode());
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
      if (computeOptions == null) {
         if (other.computeOptions != null)
            return false;
      } else if (!computeOptions.equals(other.computeOptions))
         return false;
      if (customizationOptions == null) {
         if (other.customizationOptions != null)
            return false;
      } else if (!customizationOptions.equals(other.customizationOptions))
         return false;
      if (networkToAddresses == null) {
         if (other.networkToAddresses != null)
            return false;
      } else if (!networkToAddresses.equals(other.networkToAddresses))
         return false;
      if (operatingSystemDescription == null) {
         if (other.operatingSystemDescription != null)
            return false;
      } else if (!operatingSystemDescription.equals(other.operatingSystemDescription))
         return false;
      if (resourceAllocationByType == null) {
         if (other.resourceAllocationByType != null)
            return false;
      } else if (!resourceAllocationByType.equals(other.resourceAllocationByType))
         return false;
      if (resourceAllocations == null) {
         if (other.resourceAllocations != null)
            return false;
      } else if (!resourceAllocations.equals(other.resourceAllocations))
         return false;
      if (size != other.size)
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (system == null) {
         if (other.system != null)
            return false;
      } else if (!system.equals(other.system))
         return false;
      if (vDC == null) {
         if (other.vDC != null)
            return false;
      } else if (!vDC.equals(other.vDC))
         return false;
      return true;
   }

   public Map<ResourceType, ResourceAllocation> getResourceAllocationByType() {
      return resourceAllocationByType;
   }

}