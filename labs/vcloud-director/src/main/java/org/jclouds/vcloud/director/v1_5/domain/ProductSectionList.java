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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.ProductSection;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Essentially a container with a list of product sections.
 * <p/>
 * <p>Java class for ProductSectionList complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ProductSectionList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element ref="{http://schemas.dmtf.org/ovf/envelope/1}ProductSection" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "ProductSectionList", propOrder = {
      "productSections"
})
public class ProductSectionList
      extends ResourceType<ProductSectionList>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromProductSectionList(this);
   }

   public static class Builder extends ResourceType.Builder<ProductSectionList> {

      private Set<ProductSection> productSections = Sets.newLinkedHashSet();

      /**
       * @see ProductSectionList#getProductSections()
       */
      public Builder productSections(Set<ProductSection> productSections) {
         this.productSections = checkNotNull(productSections, "productSection");
         return this;
      }


      public ProductSectionList build() {
         ProductSectionList productSectionList = new ProductSectionList(productSections);
         return productSectionList;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }


      @Override
      public Builder fromResourceType(ResourceType<ProductSectionList> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder fromProductSectionList(ProductSectionList in) {
         return fromResourceType(in)
               .productSections(in.getProductSections());
      }
   }

   private ProductSectionList() {
      // for JAXB
   }

   private ProductSectionList(Set<ProductSection> productSections) {
      this.productSections = ImmutableSet.copyOf(productSections);
   }


   @XmlElement(name = "ProductSection", namespace = "http://schemas.dmtf.org/ovf/envelope/1")
   protected Set<ProductSection> productSections = Sets.newLinkedHashSet();

   /**
    * Gets the value of the productSection property.
    */
   public Set<ProductSection> getProductSections() {
      return Collections.unmodifiableSet(this.productSections);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ProductSectionList that = ProductSectionList.class.cast(o);
      return equal(productSections, that.productSections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(productSections);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("productSections", productSections).toString();
   }

}
