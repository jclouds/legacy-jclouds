/*
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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.collect.Sets;

/**
 * Represents a list of catalog item references.
 * <p/>
 * <pre>
 * &lt;complexType name="CatalogItemsType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "CatalogItems")
public class CatalogItems {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.CATALOG_ITEMS;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

      private Set<Reference> catalogItems = Sets.newLinkedHashSet();

      /**
       * @see CatalogItems#getCatalogItems()
       */
      public Builder items(Collection<Reference> catalogItems) {
         this.catalogItems = Sets.newLinkedHashSet(checkNotNull(catalogItems, "catalogItems"));
         return this;
      }

      /**
       * @see CatalogItems#getCatalogItems()
       */
      public Builder item(Reference catalogItem) {
         this.catalogItems.add(checkNotNull(catalogItem, "catalogItem"));
         return this;
      }

      public CatalogItems build() {
         return new CatalogItems(catalogItems);
      }

      public Builder fromCatalogItems(CatalogItems in) {
         return items(in.getCatalogItems());
      }
   }

   private CatalogItems() {
      // For JAXB and builder use
   }

   private CatalogItems(Collection<Reference> tasks) {
      this.catalogItems = catalogItems;
   }

   @XmlElement(name = "CatalogItem")
   private Set<Reference> catalogItems = Sets.newLinkedHashSet();

   /**
    * Gets the value of the catalogItems property.
    */
   public Set<Reference> getCatalogItems() {
      return Collections.unmodifiableSet(this.catalogItems);
   }
}
