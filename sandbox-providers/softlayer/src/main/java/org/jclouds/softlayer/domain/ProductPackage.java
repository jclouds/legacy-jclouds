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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The SoftLayer_Product_Package data type contains information about packages
 * from which orders can be generated. Packages contain general information
 * regarding what is in them, where they are currently sold, availability, and
 * pricing.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Package"
 *      />
 */
public class ProductPackage implements Comparable<ProductPackage> {

   // TODO there are more elements than this.

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id = -1;
      private String name;
      private String description;
      private Set<ProductItem> items = Sets.newLinkedHashSet();

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder items(Iterable<ProductItem> items) {
         this.items = ImmutableSet.<ProductItem> copyOf(checkNotNull(items, "items"));
         return this;
      }

      public ProductPackage build() {
         return new ProductPackage(id, name, description, items);
      }

      public static Builder fromProductPackage(ProductPackage in) {
         return ProductPackage.builder().id(in.getId()).name(in.getName()).description(in.getDescription())
               .items(in.getItems());
      }
   }

   private long id = -1;
   private String name;
   private String description;
   private Set<ProductItem> items = Sets.newLinkedHashSet();

   // for deserializer
   ProductPackage() {

   }

   public ProductPackage(long id, String name, String description, Iterable<ProductItem> items) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.items = ImmutableSet.<ProductItem> copyOf(checkNotNull(items, "items"));
   }

   @Override
   public int compareTo(ProductPackage arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return A package's internal identifier. Everything regarding a
    *         SoftLayer_Product_Package is tied back to this id.
    */
   public long getId() {
      return id;
   }

   /**
    * @return The description of the package. For server packages, this is
    *         usually a detailed description of processor type and count.
    */
   public String getName() {
      return name;
   }

   /**
    * @return A generic description of the processor type and count. This
    *         includes HTML, so you may want to strip these tags if you plan to
    *         use it.
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return A collection of valid items available for purchase in this
    *         package.
    */
   public Set<ProductItem> getItems() {
      return items;
   }

   public Builder toBuilder() {
      return Builder.fromProductPackage(this);
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
      ProductPackage other = (ProductPackage) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ProductPackage [id=" + id + ", name=" + name + ", description=" + description + ", items=" + items + "]";
   }
}
