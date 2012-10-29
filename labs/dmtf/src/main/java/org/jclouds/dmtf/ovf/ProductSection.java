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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.dmtf.DMTFConstants.CIM_NS;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.dmtf.cim.CimString;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The ProductSection element specifies product-information for an appliance, such as product name,
 * version, and vendor.
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "ProductSection")
@XmlType(name = "ProductSection_Type")
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

      private MsgType product;
      private MsgType vendor;
      private CimString version;
      private CimString fullVersion;
      private CimString productUrl;
      private CimString vendorUrl;
      private CimString appUrl;
      protected Set<ProductSectionProperty> properties = Sets.newLinkedHashSet();

      @Override
      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see ProductSection#getProduct()
       */
      public B product(MsgType product) {
         this.product = product;
         return self();
      }

      /**
       * @see ProductSection#getVendor()
       */
      public B vendor(MsgType vendor) {
         this.vendor = vendor;
         return self();
      }

      /**
       * @see ProductSection#getVersion()
       */
      public B version(CimString version) {
         this.version = version;
         return self();
      }

      /**
       * @see ProductSection#geFullVersion()
       */
      public B fullVersion(CimString fullVersion) {
         this.fullVersion = fullVersion;
         return self();
      }

      /**
       * @see ProductSection#getProductUrl()
       */
      public B productUrl(CimString productUrl) {
         this.productUrl = productUrl;
         return self();
      }

      /**
       * @see ProductSection#getProductUrl()
       */
      public B productUrl(String productUrl) {
         this.productUrl = new CimString(productUrl);
         return self();
      }

      /**
       * @see ProductSection#getVendorUrl()
       */
      public B vendorUrl(CimString vendorUrl) {
         this.vendorUrl = vendorUrl;
         return self();
      }

      /**
       * @see ProductSection#getAppUrl()
       */
      public B appUrl(CimString appUrl) {
         this.appUrl = appUrl;
         return self();
      }

      /**
       * @see ProductSection#getProperties()
       */
      public B property(ProductSectionProperty property) {
         this.properties.add(checkNotNull(property, "property"));
         return self();
      }

      /**
       * @see ProductSection#getProperties
       */
      public B properties(Iterable<ProductSectionProperty> properties) {
         this.properties = ImmutableSet.copyOf(checkNotNull(properties, "properties"));
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
         return fromSectionType(in)
               .product(in.getProduct())
               .vendor(in.getVendor())
               .version(in.getVersion())
               .fullVersion(in.getFullVersion())
               .productUrl(in.getProductUrl())
               .vendorUrl(in.getVendorUrl())
               .appUrl(in.getAppUrl())
               .properties(Sets.newLinkedHashSet(in.getProperties()));
      }
   }

   private ProductSection(Builder<?> builder) {
      super(builder);
      this.product = builder.product;
      this.vendor = builder.vendor;
      this.version = builder.version;
      this.fullVersion = builder.fullVersion;
      this.productUrl = builder.productUrl;
      this.vendorUrl = builder.vendorUrl;
      this.appUrl = builder.appUrl;
      this.properties = builder.properties != null ? ImmutableSet.copyOf(checkNotNull(builder.properties, "properties")) : ImmutableSet.<ProductSectionProperty>of();
   }
   
   private ProductSection() {
      // For JAXB
   }

   @XmlElement(name = "Product")
   private MsgType product;
   @XmlElement(name = "Vendor")
   private MsgType vendor;
   @XmlElement(name = "Version", namespace = CIM_NS)
   private CimString version;
   @XmlElement(name = "FullVersion", namespace = CIM_NS)
   private CimString fullVersion;
   @XmlElement(name = "ProductUrl", namespace = CIM_NS)
   private CimString productUrl;
   @XmlElement(name = "VendorUrl", namespace = CIM_NS)
   private CimString vendorUrl;
   @XmlElement(name = "AppUrl", namespace = CIM_NS)
   private CimString appUrl;
   @XmlElement(name = "Property")
   private Set<ProductSectionProperty> properties = Sets.newLinkedHashSet();

   /**
    * Name of product.
    */
   public MsgType getProduct() {
      return product;
   }

   /**
    * Name of product vendor.
    */
   public MsgType getVendor() {
      return vendor;
   }

   /**
    * Product version, short form.
    */
   public CimString getVersion() {
      return version;
   }

   /**
    * Product version, long form.
    */
   public CimString getFullVersion() {
      return fullVersion;
   }

   /**
    * URL resolving to product description.
    */
   public CimString getProductUrl() {
      return productUrl;
   }

   /**
    * URL resolving to vendor description.
    */
   public CimString getVendorUrl() {
      return vendorUrl;
   }

   /**
    * Experimental: URL resolving to deployed product instance.
    */
   public CimString getAppUrl() {
      return appUrl;
   }

   // TODO Set<Icon>
   
   /**
    * Properties for application-level customization.
    */
   public Set<ProductSectionProperty> getProperties() {
      return properties;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), product, vendor, version, fullVersion, productUrl, vendorUrl, appUrl, properties);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;

      ProductSection that = ProductSection.class.cast(obj);
      return super.equals(that) &&
            equal(this.product, that.product) &&
            equal(this.vendor, that.vendor) &&
            equal(this.version, that.version) &&
            equal(this.fullVersion, that.fullVersion) &&
            equal(this.productUrl, that.productUrl) &&
            equal(this.vendorUrl, that.vendorUrl) &&
            equal(this.appUrl, that.appUrl) &&
            equal(this.properties, that.properties);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("product", product)
            .add("vendor", vendor)
            .add("version", version)
            .add("fullVersion", fullVersion)
            .add("productUrl", productUrl)
            .add("vendorUrl", vendorUrl)
            .add("appUrl", appUrl)
            .add("properties", properties);
   }

}
