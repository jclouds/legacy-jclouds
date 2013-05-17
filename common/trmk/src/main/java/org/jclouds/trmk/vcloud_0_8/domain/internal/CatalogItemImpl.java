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

import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CatalogItemImpl extends ReferenceTypeImpl implements CatalogItem {

   protected final String description;
   protected final ReferenceType entity;
   protected final Map<String, String> properties = Maps.newLinkedHashMap();
   private final ReferenceType computeOptions;
   private final ReferenceType customizationOptions;
   
   public CatalogItemImpl(String name, URI id, String description, ReferenceType computeOptions,
         ReferenceType customizationOptions, ReferenceType entity, Map<String, String> properties)  {
      super(name, TerremarkVCloudMediaType.CATALOGITEM_XML, id);
      this.description = description;
      this.entity = checkNotNull(entity, "entity");
      this.properties.putAll(checkNotNull(properties, "properties"));
      this.computeOptions = computeOptions;
      this.customizationOptions = customizationOptions;
   }

   @Override
   public String getType() {
      return TerremarkVCloudMediaType.CATALOGITEM_XML;
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
   public ReferenceType getComputeOptions() {
      return computeOptions;
   }

   @Override
   public ReferenceType getCustomizationOptions() {
      return customizationOptions;
   }

   @Override
   public String toString() {
      return "[id=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", description=" + getDescription()
            + ", entity=" + entity + ", computeOptions=" + computeOptions + ", customizationOptions="
            + customizationOptions + ", properties=" + properties + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((entity == null) ? 0 : entity.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
      result = prime * result + ((computeOptions == null) ? 0 : computeOptions.hashCode());
      result = prime * result + ((customizationOptions == null) ? 0 : customizationOptions.hashCode());
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
      if (computeOptions == null) {
         if (other.computeOptions != null)
            return false;
      } else if (!computeOptions.equals(other.computeOptions))
         return false;
      if (customizationOptions == null) {
         if (other.customizationOptions != null)
            return false;
      } else if (!customizationOptions.equals(other.customizationOptions))
         return false;
      return true;
   }

}
