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
package org.jclouds.vcloud.terremark.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Quota;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;

/**
 * Locations of resources in Terremark vDC
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkVDCImpl extends VDCImpl implements TerremarkVDC {

   private final NamedResource catalog;
   private final NamedResource publicIps;
   private final NamedResource internetServices;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public TerremarkVDCImpl(String id, String name, URI location, String description,
            Capacity storageCapacity, Capacity cpuCapacity, Capacity memoryCapacity,
            Quota instantiatedVmsQuota, Quota deployedVmsQuota,
            Map<String, NamedResource> availableNetworks,
            Map<String, NamedResource> resourceEntities, NamedResource catalog, NamedResource publicIps,
            NamedResource internetServices) {
      super(id, name, location, description, storageCapacity, cpuCapacity, memoryCapacity,
               instantiatedVmsQuota, deployedVmsQuota, availableNetworks, resourceEntities);
      this.catalog = checkNotNull(catalog, "catalog");
      this.publicIps = checkNotNull(publicIps, "publicIps");
      this.internetServices = checkNotNull(internetServices, "internetServices");
   }

   public NamedResource getCatalog() {
      return catalog;
   }

   public NamedResource getPublicIps() {
      return publicIps;
   }

   public NamedResource getInternetServices() {
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