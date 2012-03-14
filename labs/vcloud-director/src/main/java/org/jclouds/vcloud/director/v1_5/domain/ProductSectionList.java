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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants;
import org.jclouds.vcloud.director.v1_5.domain.ovf.ProductSection;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Essentially a container with a list of product sections.
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
 */
@XmlRootElement(name = "ProductSectionList")
@XmlType(name = "ProductSectionListType")
public class ProductSectionList extends ResourceType<ProductSectionList> implements Set<ProductSection> {
   
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
         if (checkNotNull(productSections, "productSections").size() > 0)
            this.productSections = Sets.newLinkedHashSet(productSections);
         return this;
      }

      /**
       * @see ProductSectionList#getProductSections()
       */
      public Builder productSections(ProductSection productSection) {
         if (productSections == null)
            productSections = Sets.newLinkedHashSet();
         this.productSections.add(checkNotNull(productSection, "productSection"));
         return this;
      }

      @Override
      public ProductSectionList build() {
         ProductSectionList productSectionList = new ProductSectionList(href, type, links, productSections);
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
         return Builder.class.cast(super.links(links));
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         return Builder.class.cast(super.link(link));
      }


      @Override
      public Builder fromResourceType(ResourceType<ProductSectionList> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder fromProductSectionList(ProductSectionList in) {
         return fromResourceType(in)
               .productSections(ImmutableSet.copyOf(in));
      }
   }

   private ProductSectionList() {
      // for JAXB
   }

   private ProductSectionList(URI href, String type, @Nullable Set<Link> links, @Nullable Set<ProductSection> productSections) {
      super(href, type, links);
      this.productSections = productSections != null && productSections.isEmpty() ? null : productSections;
   }


   @XmlElement(name = "ProductSection", namespace = VCloudDirectorConstants.VCLOUD_OVF_NS)
   protected Set<ProductSection> productSections;

   /**
    * Gets the value of the productSection property.
    */
   public Set<ProductSection> getProductSections() {
      return productSections == null ? ImmutableSet.<ProductSection>of() : Collections.unmodifiableSet(productSections);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ProductSectionList that = ProductSectionList.class.cast(o);
      return super.equals(that) && equal(this.productSections, that.productSections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), productSections);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("productSections", productSections);
   }
   
   /**
    * The delegate always returns a {@link Set} even if {@link #productSections} is {@literal null}.
    * 
    * The delegated {@link Set} is used by the methods implementing its interface.
    * <p>
    * NOTE Annoying lack of multiple inheritance for using ForwardingList!
    */
   private Set<ProductSection> delegate() {
      return getProductSections();
   }

   @Override
   public boolean add(ProductSection arg0) {
      return delegate().add(arg0);
   }

   @Override
   public boolean addAll(Collection<? extends ProductSection> arg0) {
      return delegate().addAll(arg0);
   }

   @Override
   public void clear() {
      delegate().clear();
   }

   @Override
   public boolean contains(Object arg0) {
      return delegate().contains(arg0);
   }

   @Override
   public boolean containsAll(Collection<?> arg0) {
      return delegate().containsAll(arg0);
   }

   @Override
   public boolean isEmpty() {
      return delegate().isEmpty();
   }

   @Override
   public Iterator<ProductSection> iterator() {
      return delegate().iterator();
   }

   @Override
   public boolean remove(Object arg0) {
      return delegate().remove(arg0);
   }

   @Override
   public boolean removeAll(Collection<?> arg0) {
      return delegate().removeAll(arg0);
   }

   @Override
   public boolean retainAll(Collection<?> arg0) {
      return delegate().retainAll(arg0);
   }

   @Override
   public int size() {
      return delegate().size();
   }

   @Override
   public Object[] toArray() {
      return delegate().toArray();
   }

   @Override
   public <T> T[] toArray(T[] arg0) {
      return delegate().toArray(arg0);
   }
}
