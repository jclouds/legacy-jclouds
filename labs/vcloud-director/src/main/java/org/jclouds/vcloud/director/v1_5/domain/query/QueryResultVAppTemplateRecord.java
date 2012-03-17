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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a Datastore vCloud query as a record.
 * 
 * <pre>
 * &lt;complexType name="QueryResultDatastoreRecordType" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "VAppTemplateRecord")
@XmlType(name = "QueryResultVAppTemplateRecordType")
public class QueryResultVAppTemplateRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultDatastoreRecord(this);
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
      private Boolean isGoldMaster;
      private Boolean isEnabled;
      private String status;
      private Boolean isDeployed;

      /**
       * @see QueryResultVAppTemplateRecord#getOwnerName()
       */
      public B ownerName(String ownerName) {
         this.ownerName = ownerName;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#getCatalogName()
       */
      public B catalogName(String catalogName) {
         this.catalogName = catalogName;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#isPublished()
       */
      public B isPublished(Boolean val) {
         this.isPublished = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#getVdc()
       */
      public B vdc(String val) {
         this.vdc = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#getVdcName()
       */
      public B vdcName(String val) {
         this.vdcName = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#getOrg()
       */
      public B org(String val) {
         this.org = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#getCreationDate()
       */
      public B creationDate(Date val) {
         this.creationDate = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#isBusy()
       */
      public B isBusy(Boolean val) {
         this.isBusy = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#isGoldMaster()
       */
      public B isGoldMaster(Boolean val) {
         this.isGoldMaster = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#isEnabled()
       */
      public B isEnabled(Boolean isEnabled) {
         this.isPublished = isEnabled;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#getStatus()
       */
      public B status(String val) {
         this.status = val;
         return self();
      }

      /**
       * @see QueryResultVAppTemplateRecord#isDeployed()
       */
      public B isDeployed(Boolean val) {
         this.isDeployed = val;
         return self();
      }

      @Override
      public QueryResultVAppTemplateRecord build() {
         return new QueryResultVAppTemplateRecord(this);
      }

      public B fromQueryResultDatastoreRecord(QueryResultVAppTemplateRecord in) {
         return fromQueryResultRecordType(in)
               .ownerName(in.getOwnerName())
               .catalogName(in.getCatalogName())
               .name(in.getName())
               .isPublished(in.isPublished())
               .vdc(in.getVdc())
               .vdcName(in.getVdcName())
               .org(in.getOrg())
               .creationDate(in.getCreationDate())
               .isBusy(in.isBusy())
               .isGoldMaster(in.isGoldMaster())
               .isEnabled(in.isEnabled())
               .status(in.getStatus())
               .isDeployed(in.isDeployed());
      }
   }

   private QueryResultVAppTemplateRecord(Builder<?> builder) {
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
      this.isGoldMaster = builder.isGoldMaster;
      this.isEnabled = builder.isEnabled;
      this.status = builder.status;
      this.isDeployed = builder.isDeployed;
   }

   private QueryResultVAppTemplateRecord() {
      // for JAXB
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
   private Boolean isGoldMaster;
   @XmlAttribute
   private Boolean isEnabled;
   @XmlAttribute
   private String status;
   @XmlAttribute
   private Boolean isDeployed;

   /**
    * Owner name.
    */
   public String getOwnerName() {
      return ownerName;
   }

   /**
    * Catalog name.
    */
   public String getCatalogName() {
      return catalogName;
   }

   /**
    * Shows whether it is in published catalog.
    */
   public boolean isPublished() {
      return isPublished;
   }

   /**
    * Gets the value of the name property.
    * 
    * @return possible object is {@link String }
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
    * Shows whether Vapp template is marked as a gold master
    */
   public Boolean isGoldMaster() {
      return isGoldMaster;
   }
   
   /**
    * Shows whether vDC is enabled
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Vapp template status
    */
   public String getStatus() {
      return status;
   }

   /**
    * Shows whether it is deployed
    */
   public Boolean isDeployed() {
      return isDeployed;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultVAppTemplateRecord that = QueryResultVAppTemplateRecord.class.cast(o);
      return super.equals(that) && equal(ownerName, that.ownerName) && equal(catalogName, that.catalogName) 
            && equal(isPublished, that.isPublished) && equal(name, that.name) && equal(vdc, that.vdc) && equal(vdcName, that.vdcName) 
            && equal(org, that.org) && equal(creationDate, that.creationDate) && equal(isBusy, that.isBusy)
            && equal(isGoldMaster, that.isGoldMaster) && equal(isEnabled, that.isEnabled) && equal(status, that.status)
            && equal(isDeployed, that.isDeployed);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), ownerName, catalogName, isPublished, name, vdc, vdcName, org, creationDate, isBusy, isGoldMaster, isEnabled, status, isDeployed);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("ownerName", ownerName).add("catalogName", catalogName)
            .add("isPublished", isPublished()).add("vdc", vdc).add("vdcName", vdcName).add("org", org)
            .add("creationDate", creationDate).add("isBusy", isBusy).add("isGoldMaster", isGoldMaster)
            .add("isEnabled", isEnabled).add("status", status).add("isDeployed", isDeployed);
   }

}
