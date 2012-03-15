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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Container for references to VappTemplate and Media objects.
 * <p/>
 * <pre>
 * &lt;complexType name="CatalogType" /&gt;
 * </pre>
 *
 * @author danikov
 */
@XmlRootElement(name = "Catalog")
public class CatalogType extends EntityType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromCatalogType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends EntityType.Builder<B> {

      private Owner owner;
      private CatalogItems catalogItems;
      private Boolean isPublished;

      /**
       * @see CatalogType#getOwner()
       */
      public B owner(Owner owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see CatalogType#getCatalogItems()
       */
      public B catalogItems(CatalogItems catalogItems) {
         this.catalogItems = catalogItems;
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
      public CatalogType build() {
         return new CatalogType(this);
      }

      public B fromCatalogType(CatalogType in) {
         return fromEntityType(in).owner(in.getOwner()).catalogItems(in.getCatalogItems()).isPublished(in.isPublished());
      }
   }

   protected CatalogType(Builder<?> builder) {
      super(builder);
      this.owner = builder.owner;
      this.catalogItems = builder.catalogItems;
      this.isPublished = builder.isPublished;
   }

   protected CatalogType() {
      // For JAXB
   }

   @XmlElement(name = "Owner")
   private Owner owner;
   @XmlElement(name = "CatalogItems")
   private CatalogItems catalogItems;
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
   public CatalogItems getCatalogItems() {
      return catalogItems;
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
      CatalogType that = CatalogType.class.cast(o);
      return super.equals(that) &&
            equal(this.owner, that.owner) && 
            equal(this.catalogItems, that.catalogItems) &&
            equal(this.isPublished, that.isPublished);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), owner, catalogItems, catalogItems);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner)
            .add("catalogItems", catalogItems)
            .add("isPublished", isPublished);
   }

}
