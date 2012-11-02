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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.dmtf.DMTFConstants;
import org.jclouds.dmtf.ovf.ProductSection;

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
public class ProductSectionList extends Resource implements Set<ProductSection> {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromProductSectionList(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {

      private Set<ProductSection> productSections = Sets.newLinkedHashSet();

      /**
       * @see ProductSectionList#getProductSections()
       */
      public B productSections(Set<ProductSection> productSections) {
         this.productSections = Sets.newLinkedHashSet(checkNotNull(productSections, "productSections"));
         return self();
      }

      /**
       * @see ProductSectionList#getProductSections()
       */
      public B productSection(ProductSection productSection) {
         this.productSections.add(checkNotNull(productSection, "productSection"));
         return self();
      }

      @Override
      public ProductSectionList build() {
         return new ProductSectionList(this);
      }

      public B fromProductSectionList(ProductSectionList in) {
         return fromResource(in)
               .productSections(Sets.newLinkedHashSet(in));
      }
   }

   protected ProductSectionList() {
      // for JAXB
   }

   protected ProductSectionList(Builder<?> builder) {
      super(builder);
      this.productSections = builder.productSections != null && builder.productSections.isEmpty() ? null : builder.productSections;
   }


   @XmlElement(name = "ProductSection", namespace = DMTFConstants.OVF_NS)
   private Set<ProductSection> productSections;

   /**
    * Gets the value of the productSection property.
    */
   public Set<ProductSection> getProductSections() {
      return productSections != null ? ImmutableSet.copyOf(productSections) : ImmutableSet.<ProductSection>of();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ProductSectionList that = ProductSectionList.class.cast(o);
      return super.equals(that)
            && equal(this.productSections, that.productSections);
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
