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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Container for ReferenceType elements that reference catalogs.
 * <p/>
 * <pre>
 * &lt;complexType name="CatalogsListType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "CatalogsList")
public class CatalogsList {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.CATALOG_ITEMS;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

      private Set<Reference> catalogReferences = Sets.newLinkedHashSet();

      /**
       * @see CatalogsList#getCatalogItems()
       */
      public Builder catalogs(Set<Reference> catalogReferences) {
         this.catalogReferences = checkNotNull(catalogReferences, "catalogReferences");
         return this;
      }

      public CatalogsList build() {
         return new CatalogsList(catalogReferences);
      }

      public Builder fromCatalogsList(CatalogsList in) {
         return catalogs(in.getCatalogItems());
      }
   }

   private CatalogsList() {
      // for JAXB
   }

   private CatalogsList(Set<Reference> tasks) {
      this.catalogReferences = ImmutableSet.copyOf(checkNotNull(catalogReferences, "catalogReferences"));
   }

   @XmlElement(name = "CatalogReference")
   private Set<Reference> catalogReferences = Sets.newLinkedHashSet();

   /**
    * Gets the value of the catalogReferences property.
    */
   public Set<Reference> getCatalogItems() {
      return Collections.unmodifiableSet(this.catalogReferences);
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CatalogsList that = CatalogsList.class.cast(o);
      return equal(this.catalogReferences, that.catalogReferences);
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(catalogReferences);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("catalogReferences", catalogReferences);
   }
}
