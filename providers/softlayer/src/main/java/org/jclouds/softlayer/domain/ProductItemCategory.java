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
package org.jclouds.softlayer.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The SoftLayer_Product_Item_Category data type contains
 * general category information for prices.
 *
 * @author Jason King
 * @see <a href=
"http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item_Category"
/>
 */
public class ProductItemCategory {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromProductItemCategory(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected String categoryCode;

      /**
       * @see ProductItemCategory#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see ProductItemCategory#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ProductItemCategory#getCategoryCode()
       */
      public T categoryCode(String categoryCode) {
         this.categoryCode = categoryCode;
         return self();
      }

      public ProductItemCategory build() {
         return new ProductItemCategory(id, name, categoryCode);
      }

      public T fromProductItemCategory(ProductItemCategory in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .categoryCode(in.getCategoryCode());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String name;
   private final String categoryCode;

   @ConstructorProperties({
         "id", "name", "categoryCode"
   })
   protected ProductItemCategory(int id, @Nullable String name, @Nullable String categoryCode) {
      this.id = id;
      this.name = name;
      this.categoryCode = categoryCode;
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The friendly, descriptive name of the category as seen on the order forms and on invoices.
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return The code used to identify this category.
    */
   @Nullable
   public String getCategoryCode() {
      return this.categoryCode;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ProductItemCategory that = ProductItemCategory.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("categoryCode", categoryCode);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
