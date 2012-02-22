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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.collect.Lists;

/**
 * Represents a list of catalog item references.
 * 
 * <pre>
 * &lt;complexType name="CatalogItemsType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "CatalogItems")
public class CatalogItems {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.CATALOG_ITEMS;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

      private List<Reference> catalogItems = Lists.newArrayList();

      /**
       * @see CatalogItems#getCatalogItems()
       */
      public Builder items(List<Reference> catalogItems) {
         this.catalogItems = Lists.newArrayList(checkNotNull(catalogItems, "catalogItems"));
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
      this.catalogItems = Lists.newArrayList(checkNotNull(catalogItems, "catalogItems"));
   }

   @XmlElement(name = "CatalogItem")
   private List<Reference> catalogItems = Lists.newArrayList();

   /**
    * Gets the value of the catalogItems property.
    */
   public List<Reference> getCatalogItems() {
      return this.catalogItems;
   }

   public void setCatalogItems(List<Reference> catalogItems) {
      this.catalogItems = Lists.newArrayList(checkNotNull(catalogItems, "catalogItems"));
   }

   public void addCatalogItem(Reference catalogItem) {
      this.catalogItems.add(checkNotNull(catalogItem, "catalogItem"));
   }
}
