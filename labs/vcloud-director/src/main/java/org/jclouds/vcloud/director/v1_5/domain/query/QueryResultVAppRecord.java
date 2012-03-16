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
package org.jclouds.vcloud.director.v1_5.domain.query;

import static com.google.common.base.Objects.equal;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a VApp vCloud query as a record.
 * 
 * <pre>
 * &lt;complexType name="QueryResultVAppRecordType" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "VAppRecord")
@XmlType(name = "QueryResultVAppRecordType")
public class QueryResultVAppRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultVAppRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String vdc;
      private String vdcName;
      private Boolean isPublic;
      private Boolean isEnabled;
      private Boolean isBusy;
      private Date creationDate;
      private String status;
      private String ownerName;
      private Boolean isDeployed;
      private Boolean isInMaintenanceMode;

      /**
       * @see QueryResultVAppRecord#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getVdc()
       */
      public B vdc(String vdc) {
         this.vdc = vdc;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getVdcName()
       */
      public B vdcName(String vdcName) {
         this.vdcName = vdcName;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getIsPublic()
       */
      public B isPublic(Boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getIsEnabled()
       */
      public B isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getIsBusy()
       */
      public B isBusy(Boolean isBusy) {
         this.isBusy = isBusy;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getCreationDate()
       */
      public B creationDate(Date creationDate) {
         this.creationDate = creationDate;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getStatus()
       */
      public B status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getOwnerName()
       */
      public B ownerName(String ownerName) {
         this.ownerName = ownerName;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getIsDeployed()
       */
      public B isDeployed(Boolean isDeployed) {
         this.isDeployed = isDeployed;
         return self();
      }

      /**
       * @see QueryResultVAppRecord#getIsInMaintenanceMode()
       */
      public B isInMaintenanceMode(Boolean isInMaintenanceMode) {
         this.isInMaintenanceMode = isInMaintenanceMode;
         return self();
      }

      @Override
      public QueryResultVAppRecord build() {
         return new QueryResultVAppRecord(this);
      }

      public B fromQueryResultVAppRecord(QueryResultVAppRecord in) {
         return fromQueryResultRecordType(in)
               .name(in.getName())
               .vdc(in.getVdc())
               .vdcName(in.getVdcName())
               .isPublic(in.isIsPublic())
               .isEnabled(in.isIsEnabled())
               .isBusy(in.isIsBusy())
               .creationDate(in.getCreationDate())
               .status(in.getStatus())
               .ownerName(in.getOwnerName())
               .isDeployed(in.isIsDeployed())
               .isInMaintenanceMode(in.isIsInMaintenanceMode());
      }
   }

   private QueryResultVAppRecord() {
      // For JAXB and builder use
   }

   private QueryResultVAppRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.vdc = builder.vdc;
      this.vdcName = builder.vdcName;
      this.isPublic = builder.isPublic;
      this.isEnabled = builder.isEnabled;
      this.isBusy = builder.isBusy;
      this.creationDate = builder.creationDate;
      this.status = builder.status;
      this.ownerName = builder.ownerName;
      this.isDeployed = builder.isDeployed;
      this.isInMaintenanceMode = builder.isInMaintenanceMode;
   }

   @XmlAttribute
   protected String name;
   @XmlAttribute
   protected String vdc;
   @XmlAttribute
   protected String vdcName;
   @XmlAttribute
   protected Boolean isPublic;
   @XmlAttribute
   protected Boolean isEnabled;
   @XmlAttribute
   protected Boolean isBusy;
   @XmlAttribute
   @XmlSchemaType(name = "dateTime")
   protected Date creationDate;
   @XmlAttribute
   protected String status;
   @XmlAttribute
   protected String ownerName;
   @XmlAttribute
   protected Boolean isDeployed;
   @XmlAttribute
   protected Boolean isInMaintenanceMode;

   /**
    * Gets the value of the name property.
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the value of the vdc property.
    */
   public String getVdc() {
      return vdc;
   }

   /**
    * Gets the value of the vdcName property.
    */
   public String getVdcName() {
      return vdcName;
   }

   /**
    * Gets the value of the isPublic property.
    */
   public Boolean isIsPublic() {
      return isPublic;
   }

   /**
    * Gets the value of the isEnabled property.
    */
   public Boolean isIsEnabled() {
      return isEnabled;
   }

   /**
    * Gets the value of the isBusy property.
    */
   public Boolean isIsBusy() {
      return isBusy;
   }

   /**
    * Gets the value of the creationDate property.
    */
   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * Gets the value of the status property.
    */
   public String getStatus() {
      return status;
   }

   /**
    * Gets the value of the ownerName property.
    */
   public String getOwnerName() {
      return ownerName;
   }

   /**
    * Gets the value of the isDeployed property.
    */
   public Boolean isIsDeployed() {
      return isDeployed;
   }

   /**
    * Gets the value of the isInMaintenanceMode property.
    */
   public Boolean isIsInMaintenanceMode() {
      return isInMaintenanceMode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultVAppRecord that = QueryResultVAppRecord.class.cast(o);
      return super.equals(that) &&
            equal(this.name, that.name) &&
            equal(this.vdc, that.vdc) &&
            equal(this.vdcName, that.vdcName) &&
            equal(this.isPublic, that.isPublic) &&
            equal(this.isEnabled, that.isEnabled) &&
            equal(this.isBusy, that.isBusy) &&
            equal(this.creationDate, that.creationDate) &&
            equal(this.status, that.status) &&
            equal(this.ownerName, that.ownerName) &&
            equal(this.isDeployed, that.isDeployed) &&
            equal(this.isInMaintenanceMode, that.isInMaintenanceMode);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, vdc, vdcName, isPublic, isEnabled, isBusy, creationDate, status, ownerName, isDeployed, isInMaintenanceMode);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("name", name)
            .add("vdc", vdc)
            .add("vdcName", vdcName)
            .add("isPublic", isPublic)
            .add("isEnabled", isEnabled)
            .add("isBusy", isBusy)
            .add("creationDate", creationDate)
            .add("status", status)
            .add("ownerName", ownerName)
            .add("isDeployed", isDeployed)
            .add("isInMaintenanceMode", isInMaintenanceMode);
   }

}
