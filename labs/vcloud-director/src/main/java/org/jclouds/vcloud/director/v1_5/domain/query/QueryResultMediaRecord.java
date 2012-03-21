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
package org.jclouds.vcloud.director.v1_5.domain.query;

import static com.google.common.base.Objects.equal;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a Media vCloud query as a record.
 * 
 * @author Aled Sage
 */
public class QueryResultMediaRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultMediaRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String ownerName;
      private String catalogName;
      private Boolean isPublished;
      private String name;
      private String vdc;
      private String vdcName;
      private String org;
      private Date creationDate;
      private Boolean isBusy;
      private Long storageB;
      private String owner;
      private String catalog;
      private String catalogItem;
      private String status;

      /**
       * @see QueryResultMediaRecord#getOwnerName()
       */
      public B ownerName(String val) {
         this.ownerName = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getCatalogName()
       */
      public B catalogName(String val) {
         this.catalogName = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#isPublished()
       */
      public B isPublished(Boolean val) {
         this.isPublished = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getName()
       */
      public B name(String val) {
         this.name = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getVdc()
       */
      public B vdc(String val) {
         this.vdc = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getVdcName()
       */
      public B vdcName(String val) {
         this.vdcName = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getOrg()
       */
      public B org(String val) {
         this.org = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getCreationDate()
       */
      public B creationDate(Date val) {
         this.creationDate = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#isBusy()
       */
      public B isBusy(Boolean val) {
         this.isBusy = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getStorageB()
       */
      public B storageB(Long val) {
         this.storageB = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getOwner()
       */
      public B owner(String val) {
         this.owner = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getCatalog()
       */
      public B catalog(String val) {
         this.catalog = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getCatalogItem()
       */
      public B catalogItem(String val) {
         this.catalogItem = val;
         return self();
      }

      /**
       * @see QueryResultMediaRecord#getStatus()
       */
      public B status(String val) {
         this.status = val;
         return self();
      }

      @Override
      public QueryResultMediaRecord build() {
         return new QueryResultMediaRecord(this);
      }

      public B fromQueryResultMediaRecord(QueryResultMediaRecord in) {
         return fromQueryResultRecordType(in)
                  .ownerName(in.getOwnerName())
                  .catalogName(in.getCatalogName())
                  .isPublished(in.isPublished())
                  .name(in.getName())
                  .vdc(in.getVdc())
                  .vdcName(in.getVdcName())
                  .org(in.getOrg())
                  .creationDate(in.getCreationDate())
                  .isBusy(in.isBusy())
                  .storageB(in.getStorageB())
                  .owner(in.getOwner())
                  .catalog(in.getCatalog())
                  .catalogItem(in.getCatalogItem())
                  .status(in.getStatus());
      }

   }

   @XmlAttribute
   private String ownerName;
   @XmlAttribute
   private String catalogName;
   @XmlAttribute
   private Boolean isPublished;
   @XmlAttribute
   private String name;
   @XmlAttribute
   private String vdc;
   @XmlAttribute
   private String vdcName;
   @XmlAttribute
   private String org;
   @XmlAttribute
   private Date creationDate;
   @XmlAttribute
   private Boolean isBusy;
   @XmlAttribute
   private Long storageB;
   @XmlAttribute
   private String owner;
   @XmlAttribute
   private String catalog;
   @XmlAttribute
   private String catalogItem;
   @XmlAttribute
   private String status;

   protected QueryResultMediaRecord(Builder<?> builder) {
      super(builder);
      this.ownerName = builder.ownerName;
      this.catalogName = builder.catalogName;
      this.isPublished = builder.isPublished;
      this.name = builder.name;
      this.vdc = builder.vdc;
      this.vdcName = builder.vdcName;
      this.org = builder.org;
      this.creationDate = builder.creationDate;
      this.isBusy = builder.isBusy;
      this.storageB = builder.storageB;
      this.owner = builder.owner;
      this.catalog = builder.catalog;
      this.catalogItem = builder.catalogItem;
      this.status = builder.status;
   }

   protected QueryResultMediaRecord() {
      // for JAXB
   }

   /**
    * Owner name
    */
   public String getOwnerName() {
      return ownerName;
   }

   /**
    * Catalog name
    */
   public String getCatalogName() {
      return catalogName;
   }

   /**
    * Shows whether it is in published catalog
    */
   public Boolean isPublished() {
      return isPublished;
   }

   /**
    * Media name
    */
   public String getName() {
      return name;
   }

   /**
    * vDC reference or id
    */
   public String getVdc() {
      return vdc;
   }

   /**
    * vDC name
    */
   public String getVdcName() {
      return vdcName;
   }

   /**
    * Organization reference or id
    */
   public String getOrg() {
      return org;
   }

   /**
    * Creation date
    */
   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * Shows whether it is busy
    */
   public Boolean isBusy() {
      return isBusy;
   }

   /**
    * Media storage in Bytes
    */
   public Long getStorageB() {
      return storageB;
   }

   /**
    * Owner reference or id
    */
   public String getOwner() {
      return owner;
   }

   /**
    * Catalog reference or id
    */
   public String getCatalog() {
      return catalog;
   }

   /**
    * Catalog item reference or id
    */
   public String getCatalogItem() {
      return catalogItem;
   }

   /**
    * Media status
    */
   public String getStatus() {
      return status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultMediaRecord that = QueryResultMediaRecord.class.cast(o);

      return super.equals(that) && equal(ownerName, that.ownerName) && equal(catalogName, that.catalogName) 
               && equal(isPublished, that.isPublished) && equal(name, that.name) && equal(vdc, that.vdc) 
               && equal(vdcName, that.vdcName) && equal(org, that.org) && equal(creationDate, that.creationDate) 
               && equal(isBusy, that.isBusy) && equal(storageB, that.storageB) && equal(owner, that.owner) 
               && equal(catalog, that.catalog) && equal(catalogItem, that.catalogItem) && equal(status, that.status);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), ownerName, catalogName, isPublished, name, vdc, vdcName, org, 
                creationDate, isBusy, storageB, owner, catalog, catalogItem, status);
   }
   @Override
   public ToStringHelper string() {
      return super.string().add("ownerName", ownerName).add("catalogName", catalogName)
               .add("isPublished", isPublished).add("name", name).add("vdc", vdc).add("vdcName", vdcName)
               .add("org", org).add("creationDate", creationDate).add("isBusy", isBusy).add("storageB", storageB)
               .add("owner", owner).add("catalog", catalog).add("catalogItem", catalogItem).add("status", status);
   }
}
