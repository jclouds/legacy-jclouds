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

package org.jclouds.vcloud.terremark.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;

/**
 * Locations of resources in Terremark vDC
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkVDCImpl extends VDCImpl implements TerremarkVDC {

   private final ReferenceType catalog;
   private final ReferenceType publicIps;
   private final ReferenceType internetServices;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public TerremarkVDCImpl(String name, String type, URI id, VDCStatus status, ReferenceType org,
            @Nullable String description, Iterable<Task> tasks, AllocationModel allocationModel,
            @Nullable Capacity storageCapacity, @Nullable Capacity cpuCapacity, @Nullable Capacity memoryCapacity,
            Map<String, ReferenceType> resourceEntities, Map<String, ReferenceType> availableNetworks, int nicQuota,
            int networkQuota, int vmQuota, boolean isEnabled, ReferenceType catalog, ReferenceType publicIps,
            ReferenceType internetServices) {
      super(name, type, id, status, org, description, tasks, allocationModel, storageCapacity, cpuCapacity,
               memoryCapacity, resourceEntities, availableNetworks, nicQuota, networkQuota, vmQuota, isEnabled);
      this.catalog = checkNotNull(catalog, "catalog");
      this.publicIps = checkNotNull(publicIps, "publicIps");
      this.internetServices = checkNotNull(internetServices, "internetServices");
   }

   public ReferenceType getCatalog() {
      return catalog;
   }

   public ReferenceType getPublicIps() {
      return publicIps;
   }

   public ReferenceType getInternetServices() {
      return internetServices;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
      result = prime * result + ((internetServices == null) ? 0 : internetServices.hashCode());
      result = prime * result + ((publicIps == null) ? 0 : publicIps.hashCode());
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
      TerremarkVDCImpl other = (TerremarkVDCImpl) obj;
      if (catalog == null) {
         if (other.catalog != null)
            return false;
      } else if (!catalog.equals(other.catalog))
         return false;
      if (internetServices == null) {
         if (other.internetServices != null)
            return false;
      } else if (!internetServices.equals(other.internetServices))
         return false;
      if (publicIps == null) {
         if (other.publicIps != null)
            return false;
      } else if (!publicIps.equals(other.publicIps))
         return false;
      return true;
   }

}