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

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementView;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Container for references to {@link VAppTemplate} and {@link Media} objects.
 *
 * <pre>
 * &lt;complexType name="CatalogType" /&gt;
 * </pre>
 *
 * @author danikov
 */
@XmlSeeAlso({ AdminCatalog.class })
@XmlRootElement(name = "Catalog")
@XmlType(name = "CatalogType")
public class Catalog extends Entity {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromCatalogType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends Entity.Builder<B> {

      private Owner owner;
      private Set<Reference> catalogItems = Sets.newLinkedHashSet();
      private Boolean isPublished;

      /**
       * @see CatalogType#getOwner()
       */
      public B owner(Owner owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see CatalogItems#getCatalogItems()
       */
      public B items(Iterable<Reference> catalogItems) {
         this.catalogItems = Sets.newLinkedHashSet(checkNotNull(catalogItems, "catalogItems"));
         return self();
      }

      /**
       * @see CatalogItems#getCatalogItems()
       */
      public B item(Reference catalogItem) {
         this.catalogItems.add(checkNotNull(catalogItem, "catalogItem"));
         return self();
      }

      /**
       * @see CatalogType#isPublished()
       */
      public B isPublished(Boolean isPublished) {
         this.isPublished = isPublished;
         return self();
      }

      /**
       * @see CatalogType#isPublished()
       */
      public B published() {
         this.isPublished = Boolean.TRUE;
         return self();
      }

      @Override
      public Catalog build() {
         return new Catalog(this);
      }

      public B fromCatalogType(Catalog in) {
         return fromEntityType(in).owner(in.getOwner()).items(in.getCatalogItems()).isPublished(in.isPublished());
      }
   }

   protected Catalog(Builder<?> builder) {
      super(builder);
      this.owner = builder.owner;
      this.catalogItems = builder.catalogItems == null || builder.catalogItems.isEmpty() ? null : ImmutableSet.copyOf(builder.catalogItems);
      this.isPublished = builder.isPublished;
   }

   protected Catalog() {
      // For JAXB
   }

   @XmlElement(name = "Owner")
   private Owner owner;
   @XmlElementView(name = "CatalogItems")
   @XmlElement(name = "CatalogItem")
   private Set<Reference> catalogItems;
   @XmlElement(name = "IsPublished")
   private Boolean isPublished;

   /**
    * Gets the value of the owner property.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Gets the value of the catalogItems property.
    */
   public Set<Reference> getCatalogItems() {
      return catalogItems == null ? ImmutableSet.<Reference>of() : ImmutableSet.copyOf(catalogItems);
   }

   /**
    * Gets the value of the isPublished property.
    */
   public Boolean isPublished() {
      return isPublished;
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Catalog that = Catalog.class.cast(o);
      return super.equals(that) &&
            equal(this.owner, that.owner) && 
            equal(this.getCatalogItems(), that.getCatalogItems()) &&
            equal(this.isPublished, that.isPublished);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), owner, getCatalogItems(), catalogItems);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner)
            .add("catalogItems", getCatalogItems())
            .add("isPublished", isPublished);
   }

}
