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
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

import com.google.common.collect.ImmutableMap;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class OrgImpl extends ReferenceTypeImpl implements Org {
   @Nullable
   private final String description;
   private final Map<String, ReferenceType> catalogs;
   private final Map<String, ReferenceType> vdcs;
   private final ReferenceType keys;
   private final ImmutableMap<String, ReferenceType> tasksLists;

   public OrgImpl(String name, String type, URI id, String description, Map<String, ReferenceType> catalogs,
         Map<String, ReferenceType> vdcs, Map<String, ReferenceType> tasksLists, ReferenceType keys) {
      super(name, type, id);
      this.description = description;
      this.catalogs = ImmutableMap.copyOf(checkNotNull(catalogs, "catalogs"));
      this.vdcs = ImmutableMap.copyOf(checkNotNull(vdcs, "vdcs"));
      this.tasksLists = ImmutableMap.copyOf(checkNotNull(tasksLists, "tasksLists"));
      this.keys = checkNotNull(keys, "keys");
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
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((catalogs == null) ? 0 : catalogs.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((keys == null) ? 0 : keys.hashCode());
      result = prime * result + ((tasksLists == null) ? 0 : tasksLists.hashCode());
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
      if (keys == null) {
         if (other.keys != null)
            return false;
      } else if (!keys.equals(other.keys))
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
   public int compareTo(ReferenceType o) {
      return (this == o) ? 0 : getHref().compareTo(o.getHref());
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", description=" + description
            + ", catalogs=" + catalogs + ", tasksLists=" + tasksLists + ", vdcs=" + vdcs + ", keys=" + keys + "]";
   }

   @Override
   public Map<String, ReferenceType> getTasksLists() {
      return tasksLists;
   }

   @Override
   public ReferenceType getKeys() {
      return keys;
   }

}
