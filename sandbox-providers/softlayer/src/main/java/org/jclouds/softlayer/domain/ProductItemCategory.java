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
package org.jclouds.softlayer.domain;

/**
 * The SoftLayer_Product_Item_Category data type contains
 * general category information for prices.
 * 
 * @author Jason King
 * @see <a href=
 *      "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item_Category"
 *      />
 */
public class ProductItemCategory implements Comparable<ProductItemCategory> {

   // TODO there are more elements than this.

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id = -1;
      private String name;
      private String categoryCode;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder categoryCode(String categoryCode) {
         this.categoryCode = categoryCode;
         return this;
      }

      public ProductItemCategory build() {
         return new ProductItemCategory(id, name, categoryCode);
      }

      public static Builder fromProductItemCategory(ProductItemCategory in) {
         return ProductItemCategory.builder().id(in.getId())
                                             .name(in.getName())
                                             .categoryCode(in.getCategoryCode());
      }
   }

   private long id = -1;
   private String name;
   private String categoryCode;

   // for deserializer
   ProductItemCategory() {

   }

   public ProductItemCategory(long id, String name, String categoryCode) {
      this.id = id;
      this.name = name;
      this.categoryCode = categoryCode;
   }

   @Override
   public int compareTo(ProductItemCategory arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public long getId() {
      return id;
   }

   /**
    * @return The friendly, descriptive name of the category as seen on the order forms and on invoices.
    */
   public String getName() {
      return name;
   }

   /**
    * @return The code used to identify this category.
    */
   public String getCategoryCode() {
      return categoryCode;
   }

   public Builder toBuilder() {
      return Builder.fromProductItemCategory(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ProductItemCategory other = (ProductItemCategory) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ProductItemCategory [id=" + id + ", name=" + name + ", categoryCode=" + categoryCode + "]";
   }
}
