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

package org.jclouds.vcloud.domain.internal;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Org;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class OrgImpl implements Org {
   private final String name;
   private final URI id;
   private final String description;
   private final Map<String, NamedResource> catalogs;
   private final Map<String, NamedResource> vdcs;
   private final Map<String, NamedResource> networks;
   private final NamedResource tasksList;

   public OrgImpl(String name, URI id, String description, Map<String, NamedResource> catalogs,
         Map<String, NamedResource> vdcs, Map<String, NamedResource> networks, @Nullable NamedResource tasksList) {
      this.name = name;
      this.id = id;
      this.description = description;
      this.catalogs = catalogs;
      this.vdcs = vdcs;
      this.networks = networks;
      this.tasksList = tasksList;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public URI getId() {
      return id;
   }

   @Override
   public Map<String, NamedResource> getCatalogs() {
      return catalogs;
   }

   @Override
   public Map<String, NamedResource> getVDCs() {
      return vdcs;
   }

   @Override
   public NamedResource getTasksList() {
      return tasksList;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((catalogs == null) ? 0 : catalogs.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((networks == null) ? 0 : networks.hashCode());
      result = prime * result + ((tasksList == null) ? 0 : tasksList.hashCode());
      result = prime * result + ((vdcs == null) ? 0 : vdcs.hashCode());
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
      OrgImpl other = (OrgImpl) obj;
      if (catalogs == null) {
         if (other.catalogs != null)
            return false;
      } else if (!catalogs.equals(other.catalogs))
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
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (networks == null) {
         if (other.networks != null)
            return false;
      } else if (!networks.equals(other.networks))
         return false;
      if (tasksList == null) {
         if (other.tasksList != null)
            return false;
      } else if (!tasksList.equals(other.tasksList))
         return false;
      if (vdcs == null) {
         if (other.vdcs != null)
            return false;
      } else if (!vdcs.equals(other.vdcs))
         return false;
      return true;
   }

   @Override
   public String getType() {
      return VCloudMediaType.ORG_XML;
   }

   @Override
   public int compareTo(NamedResource o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", type=" + getType() + "]";
   }

   @Override
   public Map<String, NamedResource> getNetworks() {
      return networks;
   }

   @Override
   public String getDescription() {
      return description;
   }
}