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

import java.util.Set;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * The InstantiateVAppTemplateParams element forms the body of an instantiateVappTemplate request.
 */
public class InstantiateVAppTemplateParams {

   protected final String info;
   protected final VirtualSystemSettingData virtualSystem;
   protected final Set<ResourceAllocationSettingData> resourceAllocations = Sets.newLinkedHashSet();

   public InstantiateVAppTemplateParams(String info, VirtualSystemSettingData virtualSystem, Iterable<ResourceAllocationSettingData> resourceAllocations) {
      this.info = info;
      this.virtualSystem = virtualSystem;
      Iterables.addAll(this.resourceAllocations, resourceAllocations);
   }

   public String getInfo() {
      return info;
   }

   public VirtualSystemSettingData getSystem() {
      return virtualSystem;
   }

   public Set<ResourceAllocationSettingData> getResourceAllocationSettingDatas() {
      return resourceAllocations;
   }

   @Override
   public String toString() {
      return "[info=" + getInfo() + ", virtualSystem=" + getSystem() + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result + ((resourceAllocations == null) ? 0 : resourceAllocations.hashCode());
      result = prime * result + ((virtualSystem == null) ? 0 : virtualSystem.hashCode());
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
      InstantiateVAppTemplateParams other = (InstantiateVAppTemplateParams) obj;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (resourceAllocations == null) {
         if (other.resourceAllocations != null)
            return false;
      } else if (!resourceAllocations.equals(other.resourceAllocations))
         return false;
      if (virtualSystem == null) {
         if (other.virtualSystem != null)
            return false;
      } else if (!virtualSystem.equals(other.virtualSystem))
         return false;
      return true;
   }

}