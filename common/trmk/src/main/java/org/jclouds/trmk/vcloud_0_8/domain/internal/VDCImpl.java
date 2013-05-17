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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;

import com.google.common.collect.ImmutableMap;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VDCImpl extends ReferenceTypeImpl implements VDC {

   @Nullable
   private final String description;
   private final ReferenceType catalog;
   private final ReferenceType publicIps;
   private final ReferenceType internetServices;
   private final Map<String, ReferenceType> resourceEntities;
   private final Map<String, ReferenceType> availableNetworks;

   public VDCImpl(String name, String type, URI href, @Nullable String description, ReferenceType catalog,
         ReferenceType publicIps, ReferenceType internetServices, Map<String, ReferenceType> resourceEntities,
         Map<String, ReferenceType> availableNetworks) {
      super(name, type, href);
      this.description = description;
      this.catalog = checkNotNull(catalog, "catalog");
      this.publicIps = checkNotNull(publicIps, "publicIps");
      this.internetServices = checkNotNull(internetServices, "internetServices");
      this.resourceEntities = ImmutableMap.copyOf(checkNotNull(resourceEntities, "resourceEntities"));
      this.availableNetworks = ImmutableMap.copyOf(checkNotNull(availableNetworks, "availableNetworks"));
   }

   /**
    * @InheritDoc
    */
   @Override
   public String getDescription() {
      return description;
   }

   /**
    * @InheritDoc
    */
   @Override
   public ReferenceType getCatalog() {
      return catalog;
   }

   /**
    * @InheritDoc
    */
   @Override
   public ReferenceType getPublicIps() {
      return publicIps;
   }

   /**
    * @InheritDoc
    */
   @Override
   public ReferenceType getInternetServices() {
      return internetServices;
   }

   /**
    * @InheritDoc
    */
   @Override
   public Map<String, ReferenceType> getResourceEntities() {
      return resourceEntities;
   }

   /**
    * @InheritDoc
    */
   @Override
   public Map<String, ReferenceType> getAvailableNetworks() {
      return availableNetworks;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((availableNetworks == null) ? 0 : availableNetworks.hashCode());
      result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((internetServices == null) ? 0 : internetServices.hashCode());
      result = prime * result + ((publicIps == null) ? 0 : publicIps.hashCode());
      result = prime * result + ((resourceEntities == null) ? 0 : resourceEntities.hashCode());
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
      if (availableNetworks == null) {
         if (other.availableNetworks != null)
            return false;
      } else if (!availableNetworks.equals(other.availableNetworks))
         return false;
      if (catalog == null) {
         if (other.catalog != null)
            return false;
      } else if (!catalog.equals(other.catalog))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
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
      if (resourceEntities == null) {
         if (other.resourceEntities != null)
            return false;
      } else if (!resourceEntities.equals(other.resourceEntities))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[name=" + getName() + ", href=" + getHref() + ", description=" + description + ", catalog=" + catalog
            + ", publicIps=" + publicIps + ", internetServices=" + internetServices + ", resourceEntities="
            + resourceEntities + ", availableNetworks=" + availableNetworks + "]";
   }

}
