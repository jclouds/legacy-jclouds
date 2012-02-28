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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.ProductSection;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * 
 * Essentially a container with a list of product sections.
 * 
 * <p>Java class for ProductSectionList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSectionList", propOrder = {
    "productSection"
})
public class ProductSectionList
    extends ResourceType<ProductSectionList>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromProductSectionList(this);
   }

   public static class Builder extends ResourceType.Builder<ProductSectionList> {
      
      private List<ProductSection> productSection;

      /**
       * @see ProductSectionList#getProductSection()
       */
      public Builder productSection(List<ProductSection> productSection) {
         this.productSection = productSection;
         return this;
      }


      public ProductSectionList build() {
         ProductSectionList productSectionList = new ProductSectionList(productSection);
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
            .productSection(in.getProductSection());
      }
   }

   private ProductSectionList() {
      // For JAXB and builder use
   }

   private ProductSectionList(List<ProductSection> productSection) {
      this.productSection = productSection;
   }


    @XmlElement(name = "ProductSection", namespace = "http://schemas.dmtf.org/ovf/envelope/1")
    protected List<ProductSection> productSection;

    /**
     * Gets the value of the productSection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productSection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductSection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductSection }
     */
    public List<ProductSection> getProductSection() {
        if (productSection == null) {
            productSection = new ArrayList<ProductSection>();
        }
        return this.productSection;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ProductSectionList that = ProductSectionList.class.cast(o);
      return equal(productSection, that.productSection);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(productSection);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("productSection", productSection).toString();
   }

}
