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
 * Container for ReferenceType elements that reference catalogs.
 * 
 * <pre>
 * &lt;complexType name="CatalogsListType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "CatalogsList")
public class CatalogsList {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.CATALOG_ITEMS;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

      private List<Reference> catalogReferences = Lists.newArrayList();

      /**
       * @see CatalogsList#getCatalogItems()
       */
      public Builder catalogs(List<Reference> catalogReferences) {
         this.catalogReferences = Lists.newArrayList(checkNotNull(catalogReferences, "catalogReferences"));
         return this;
      }

      /**
       * @see CatalogsList#getCatalogItems()
       */
      public Builder catalog(Reference catalog) {
         this.catalogReferences.add(checkNotNull(catalog, "catalog"));
         return this;
      }

      public CatalogsList build() {
         return new CatalogsList(catalogReferences);
      }

      public Builder fromCatalogsList(CatalogsList in) {
         return catalogs(in.getCatalogsList());
      }
   }

   private CatalogsList() {
      // For JAXB and builder use
   }

   private CatalogsList(Collection<Reference> tasks) {
      this.catalogReferences = Lists.newArrayList(checkNotNull(catalogReferences, "catalogReferences"));
   }

   @XmlElement(name = "CatalogReference")
   private List<Reference> catalogReferences = Lists.newArrayList();

   /**
    * Gets the value of the catalogReferences property.
    */
   public List<Reference> getCatalogsList() {
      return this.catalogReferences;
   }

   public void setCatalogsList(List<Reference> catalogReferences) {
      this.catalogReferences = Lists.newArrayList(checkNotNull(catalogReferences, "catalogReferences"));
   }

   public void addCatalog(Reference catalog) {
      this.catalogReferences.add(checkNotNull(catalog, "catalog"));
   }
}
