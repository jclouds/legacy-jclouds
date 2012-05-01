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
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class OrgImpl extends ReferenceTypeImpl implements Org {
   private final String fullName;
   @Nullable
   private final String description;
   private final Map<String, ReferenceType> catalogs = Maps.newLinkedHashMap();
   private final Map<String, ReferenceType> vdcs = Maps.newLinkedHashMap();
   private final Map<String, ReferenceType> networks = Maps.newLinkedHashMap();
   private final ReferenceType tasksList;
   private final List<Task> tasks = Lists.newArrayList();

   public OrgImpl(String name, String type, URI id, String fullName, String description,
            Map<String, ReferenceType> catalogs, Map<String, ReferenceType> vdcs, Map<String, ReferenceType> networks,
            @Nullable ReferenceType tasksList, Iterable<Task> tasks) {
      super(name, type, id);
      this.fullName = checkNotNull(fullName, "fullName");
      this.description = description;
      this.catalogs.putAll(checkNotNull(catalogs, "catalogs"));
      this.vdcs.putAll(checkNotNull(vdcs, "vdcs"));
      this.networks.putAll(checkNotNull(networks, "networks"));
      this.tasksList = tasksList;
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
   }

   @Override
   public String getFullName() {
      return fullName;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public Map<String, ReferenceType> getCatalogs() {
      return catalogs;
   }

   @Override
   public Map<String, ReferenceType> getVDCs() {
      return vdcs;
   }

   @Override
   public Map<String, ReferenceType> getNetworks() {
      return networks;
   }

   @Override
   public ReferenceType getTasksList() {
      return tasksList;
   }

   @Override
   public List<Task> getTasks() {
      return tasks;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((catalogs == null) ? 0 : catalogs.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
      result = prime * result + ((networks == null) ? 0 : networks.hashCode());
      result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
      result = prime * result + ((tasksList == null) ? 0 : tasksList.hashCode());
      result = prime * result + ((vdcs == null) ? 0 : vdcs.hashCode());
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
      if (fullName == null) {
         if (other.fullName != null)
            return false;
      } else if (!fullName.equals(other.fullName))
         return false;
      if (networks == null) {
         if (other.networks != null)
            return false;
      } else if (!networks.equals(other.networks))
         return false;
      if (tasks == null) {
         if (other.tasks != null)
            return false;
      } else if (!tasks.equals(other.tasks))
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
   public int compareTo(ReferenceType o) {
      return (this == o) ? 0 : getHref().compareTo(o.getHref());
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", fullName=" + fullName
               + ", description=" + description + ", catalogs=" + catalogs + ", networks=" + networks + ", tasksList="
               + tasksList + ", vdcs=" + vdcs + ", tasks=" + tasks + "]";
   }

}