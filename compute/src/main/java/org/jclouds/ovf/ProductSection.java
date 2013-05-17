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
package org.jclouds.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The ProductSection element specifies product-information for an appliance, such as product name,
 * version, and vendor.
 * 
 * @author Adrian Cole
 */
public class ProductSection extends Section<ProductSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromDeploymentOptionSection(this);
   }

   public static class Builder extends Section.Builder<ProductSection> {
      protected Set<Property> properties = Sets.newLinkedHashSet();

      /**
       * @see ProductSection#getPropertys
       */
      public Builder property(Property property) {
         this.properties.add(checkNotNull(property, "property"));
         return this;
      }

      /**
       * @see ProductSection#getPropertys
       */
      public Builder properties(Iterable<Property> properties) {
         this.properties = ImmutableSet.<Property> copyOf(checkNotNull(properties, "properties"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ProductSection build() {
         return new ProductSection(info, properties);
      }

      public Builder fromDeploymentOptionSection(ProductSection in) {
         return info(in.getInfo()).properties(in.getProperties());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<ProductSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   protected final Set<Property> properties;

   public ProductSection(String info, Iterable<Property> properties) {
      super(info);
      this.properties = ImmutableSet.<Property> copyOf(checkNotNull(properties, "properties"));

   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
      ProductSection other = (ProductSection) obj;
      if (properties == null) {
         if (other.properties != null)
            return false;
      } else if (!properties.equals(other.properties))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[info=%s, properties=%s]", info, properties);
   }

   public Set<Property> getProperties() {
      return properties;
   }

}
