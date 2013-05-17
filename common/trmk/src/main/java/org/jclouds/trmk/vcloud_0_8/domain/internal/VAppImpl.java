/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;

import com.google.common.collect.ListMultimap;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class VAppImpl implements VApp {
   private final String name;
   private final URI href;
   private final ReferenceType vDC;
   private final Set<ReferenceType> extendedInfo;
   private final Status status;
   private final Long size;
   private final ListMultimap<String, String> networkToAddresses;
   private final String operatingSystemDescription;
   private final VirtualSystemSettingData system;
   private final Set<ResourceAllocationSettingData> resourceAllocations;
   private final Integer osType;

   public VAppImpl(String name, URI href, Status status, Long size, ReferenceType vDC,
            ListMultimap<String, String> networkToAddresses, Integer osType, String operatingSystemDescription,
            VirtualSystemSettingData system, Set<ResourceAllocationSettingData> resourceAllocations) {
       this(name, href, status, size, vDC, networkToAddresses, osType, operatingSystemDescription, system,
               resourceAllocations, new HashSet<ReferenceType>());
   }

   public VAppImpl(String name, URI href, Status status, Long size, ReferenceType vDC,
            ListMultimap<String, String> networkToAddresses, Integer osType, String operatingSystemDescription,
            VirtualSystemSettingData system, Set<ResourceAllocationSettingData> resourceAllocations,
            Set<ReferenceType> extendedInfo) {
      this.name = checkNotNull(name, "name");
      this.href = checkNotNull(href, "href");
      this.status = checkNotNull(status, "status");
      this.size = size;
      this.vDC = vDC;
      this.networkToAddresses = checkNotNull(networkToAddresses, "networkToAddresses");
      this.osType = osType;
      this.operatingSystemDescription = operatingSystemDescription;
      this.system = system;
      this.resourceAllocations = checkNotNull(resourceAllocations, "resourceAllocations");
      this.extendedInfo = extendedInfo;
   }

   @Override
   public Status getStatus() {
      return status;
   }

   @Override
   public ListMultimap<String, String> getNetworkToAddresses() {
      return networkToAddresses;
   }

   @Override
   public Integer getOsType() {
      return osType;
   }

   @Override
   public String getOperatingSystemDescription() {
      return operatingSystemDescription;
   }

   @Override
   public VirtualSystemSettingData getSystem() {
      return system;
   }

   @Override
   public Set<ResourceAllocationSettingData> getResourceAllocations() {
      return resourceAllocations;
   }

   @Override
   public ReferenceType getVDC() {
      return vDC;
   }

   @Override
   public Set<ReferenceType> getExtendedInfo() {
      return extendedInfo;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((networkToAddresses == null) ? 0 : networkToAddresses.hashCode());
      result = prime * result + ((operatingSystemDescription == null) ? 0 : operatingSystemDescription.hashCode());
      result = prime * result + ((resourceAllocations == null) ? 0 : resourceAllocations.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      result = prime * result + ((osType == null) ? 0 : osType.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((system == null) ? 0 : system.hashCode());
      result = prime * result + ((vDC == null) ? 0 : vDC.hashCode());
      result = prime * result + ((extendedInfo == null) ? 0 : extendedInfo.hashCode());
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
      VAppImpl other = (VAppImpl) obj;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (osType == null) {
         if (other.osType != null)
            return false;
      } else if (!osType.equals(other.osType))
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
      if (resourceAllocations == null) {
         if (other.resourceAllocations != null)
            return false;
      } else if (!resourceAllocations.equals(other.resourceAllocations))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
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
      if (extendedInfo == null) {
         if (other.extendedInfo != null)
            return false;
      } else if (!extendedInfo.equals(other.extendedInfo))
         return false;
      return true;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public URI getHref() {
      return href;
   }

   @Override
   public Long getSize() {
      return size;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", name=" + name + ", networkToAddresses=" + networkToAddresses + ", osType=" + osType
               + ", operatingSystemDescription=" + operatingSystemDescription + ", resourceAllocationByType="
               + resourceAllocations + ", size=" + size + ", status=" + status + ", system=" + system + ", vDC=" + vDC
               + ", extendedInfo=" + extendedInfo + "]";
   }

   @Override
   public String getType() {
      return TerremarkVCloudMediaType.VAPP_XML;
   }

   @Override
   public int compareTo(ReferenceType o) {
      return (this == o) ? 0 : getHref().compareTo(o.getHref());
   }

}
