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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.internal.Nullable;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class CatalogImpl extends LinkedHashMap<String, NamedResource> implements Catalog {

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;
   private final String name;
   private final String type;
   private final URI id;
   private final NamedResource org;
   @Nullable
   private final String description;
   private final List<Task> tasks = Lists.newArrayList();
   private final boolean published;

   public CatalogImpl(String name, String type, URI id, NamedResource org, @Nullable String description,
            Map<String, NamedResource> contents, Iterable<Task> tasks, boolean published) {
      this.name = checkNotNull(name, "name");
      this.type = checkNotNull(type, "type");
      this.org = org;// TODO: once <1.0 is killed check not null
      this.description = description;
      this.id = checkNotNull(id, "id");
      putAll(checkNotNull(contents, "contents"));
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
      this.published = published;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getId() {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NamedResource getOrg() {
      return org;
   }

   /**
    * {@inheritDoc}
    */
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<Task> getTasks() {
      return tasks;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isPublished() {
      return published;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((org == null) ? 0 : org.hashCode());
      result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      CatalogImpl other = (CatalogImpl) obj;
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
      if (org == null) {
         if (other.org != null)
            return false;
      } else if (!org.equals(other.org))
         return false;
      if (tasks == null) {
         if (other.tasks != null)
            return false;
      } else if (!tasks.equals(other.tasks))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public int compareTo(NamedResource o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }

}