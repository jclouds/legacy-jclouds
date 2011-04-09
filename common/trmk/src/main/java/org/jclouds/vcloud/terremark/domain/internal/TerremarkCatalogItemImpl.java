/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.internal.CatalogItemImpl;
import org.jclouds.vcloud.terremark.domain.TerremarkCatalogItem;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkCatalogItemImpl extends CatalogItemImpl implements TerremarkCatalogItem {

   private final ReferenceType computeOptions;
   private final ReferenceType customizationOptions;

   public TerremarkCatalogItemImpl(String name, URI id, String description, ReferenceType computeOptions,
         ReferenceType customizationOptions, ReferenceType entity, Map<String, String> properties) {
      super(name, id, description, entity, properties);
      this.computeOptions = computeOptions;
      this.customizationOptions = customizationOptions;
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
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
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
      TerremarkCatalogItemImpl other = (TerremarkCatalogItemImpl) obj;
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