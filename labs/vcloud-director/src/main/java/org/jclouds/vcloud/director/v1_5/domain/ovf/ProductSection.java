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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The ProductSection element specifies product-information for an appliance, such as product name,
 * version, and vendor.
 *
 * TODO this should contain a multitude of other elements!
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "ProductSection", namespace = VCLOUD_OVF_NS)
public class ProductSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromProductSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {
      protected Set<org.jclouds.vcloud.director.v1_5.domain.ovf.Property> properties = Sets.newLinkedHashSet();

      /**
       * @see ProductSection#getProperties
       */
      public B property(org.jclouds.vcloud.director.v1_5.domain.ovf.Property property) {
         this.properties.add(checkNotNull(property, "property"));
         return self();
      }

      /**
       * @see ProductSection#getProperties
       */
      public B properties(Iterable<org.jclouds.vcloud.director.v1_5.domain.ovf.Property> properties) {
         this.properties = ImmutableSet.<org.jclouds.vcloud.director.v1_5.domain.ovf.Property> copyOf(checkNotNull(properties, "properties"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public ProductSection build() {
         return new ProductSection(this);
      }

      public B fromProductSection(ProductSection in) {
         return info(in.getInfo()).properties(in.getProperties());
      }
   }

   
   private Set<org.jclouds.vcloud.director.v1_5.domain.ovf.Property> properties;

   private ProductSection(Builder<?> builder) {
      super(builder);
      this.properties = ImmutableSet.copyOf(checkNotNull(builder.properties, "properties"));
   }
   
   private ProductSection() {
      // For JAXB
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), properties);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;

      ProductSection other = (ProductSection) obj;
      return super.equals(other) && Objects.equal(properties, other.properties);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("properties", properties);
   }

   public Set<org.jclouds.vcloud.director.v1_5.domain.ovf.Property> getProperties() {
      return properties;
   }

}