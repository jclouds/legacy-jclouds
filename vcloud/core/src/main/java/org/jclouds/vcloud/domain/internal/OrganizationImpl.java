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

import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.endpoints.VDC;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class OrganizationImpl implements Organization {
   private final String id;
   private final String name;
   private final URI location;
   private final Map<String, NamedResource> catalogs;
   private final Map<String, NamedResource> vdcs;
   private final Map<String, NamedResource> tasksLists;

   public OrganizationImpl(String id, String name, URI location, Map<String, NamedResource> catalogs,
         Map<String, NamedResource> vdcs, Map<String, NamedResource> tasksLists) {
      this.id = id;
      this.name = name;
      this.location = location;
      this.catalogs = catalogs;
      this.vdcs = vdcs;
      this.tasksLists = tasksLists;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public URI getLocation() {
      return location;
   }

   @Catalog
   public Map<String, NamedResource> getCatalogs() {
      return catalogs;
   }

   @VDC
   public Map<String, NamedResource> getVDCs() {
      return vdcs;
   }

   @TasksList
   public Map<String, NamedResource> getTasksLists() {
      return tasksLists;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((catalogs == null) ? 0 : catalogs.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((tasksLists == null) ? 0 : tasksLists.hashCode());
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
      OrganizationImpl other = (OrganizationImpl) obj;
      if (catalogs == null) {
         if (other.catalogs != null)
            return false;
      } else if (!catalogs.equals(other.catalogs))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (tasksLists == null) {
         if (other.tasksLists != null)
            return false;
      } else if (!tasksLists.equals(other.tasksLists))
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

   public int compareTo(NamedResource o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", type=" + getType() + ", location=" + location + "]";
   }
}