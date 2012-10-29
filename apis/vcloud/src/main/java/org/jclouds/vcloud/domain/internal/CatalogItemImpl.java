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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.ReferenceType;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CatalogItemImpl extends ReferenceTypeImpl implements CatalogItem {

   private final String description;
   private final ReferenceType entity;
   private final Map<String, String> properties = Maps.newLinkedHashMap();

   public CatalogItemImpl(String name, URI id, @Nullable String description, ReferenceType entity,
         Map<String, String> properties) {
      super(name, VCloudMediaType.CATALOGITEM_XML, id);
      this.description = description;
      this.entity = checkNotNull(entity, "entity");
      this.properties.putAll(checkNotNull(properties, "properties"));
   }

   @Override
   public String getType() {
      return VCloudMediaType.CATALOGITEM_XML;
   }

   public ReferenceType getEntity() {
      return entity;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   @Override
   public String toString() {
      return "CatalogItemImpl [id=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", description="
            + getDescription() + ", entity=" + entity + ", properties=" + properties + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((entity == null) ? 0 : entity.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
      CatalogItemImpl other = (CatalogItemImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (entity == null) {
         if (other.entity != null)
            return false;
      } else if (!entity.equals(other.entity))
         return false;
      if (properties == null) {
         if (other.properties != null)
            return false;
      } else if (!properties.equals(other.properties))
         return false;
      return true;
   }

}
