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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a VM vCloud query as a record.
 * 
 * <pre>
 * &lt;complexType name="QueryResultVMRecordType" /&gt;
 * </pre>
 * 
 * @author Aled Sage
 */
@XmlRootElement(name = "VMRecord")
@XmlType(name = "QueryResultVMRecordType")
public class QueryResultVMRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultVMRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String containerName;
      private String container;
      private String vdc;
      private Boolean isVAppTemplate;
      private Boolean isDeleted;
      private String guestOs;
      private Integer numberOfCpus;
      private Integer memoryMB;
      private String status;
      private Boolean isBusy;
      private Boolean isDeployed;
      private Boolean isPublished;
      private Boolean catalogName;
      private Integer hardwareVersion;
      private Boolean isInMaintenanceMode;

      /**
       * @see QueryResultVMRecord#getName()
       */
      public B name(String val) {
         this.name = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getContainerName()
       */
      public B containerName(String val) {
         this.containerName = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getContainer()
       */
      public B container(String val) {
         this.container = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getVdc()
       */
      public B vdc(String val) {
         this.vdc = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#isVAppTemplate()
       */
      public B isVAppTemplate(Boolean val) {
         this.isVAppTemplate = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#isDeleted()
       */
      public B isDeleted(Boolean val) {
         this.isDeleted = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getGuestOs()
       */
      public B guestOs(String val) {
         this.guestOs = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getNumberOfCpus()
       */
      public B numberOfCpus(Integer val) {
         this.numberOfCpus = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getMemoryMB()
       */
      public B memoryMB(Integer val) {
         this.memoryMB = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getStatus()
       */
      public B status(String val) {
         this.status = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#isBusy()
       */
      public B isBusy(Boolean val) {
         this.isBusy = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#isDeployed()
       */
      public B isDeployed(Boolean val) {
         this.isDeployed = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#isPublished()
       */
      public B isPublished(Boolean val) {
         this.isPublished = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#isCatalogName()
       */
      public B catalogName(Boolean val) {
         this.catalogName = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#getHardwareVersion()
       */
      public B hardwareVersion(Integer val) {
         this.hardwareVersion = val;
         return self();
      }

      /**
       * @see QueryResultVMRecord#isInMaintenanceMode()
       */
      public B isInMaintenanceMode(Boolean val) {
         this.isInMaintenanceMode = val;
         return self();
      }

      @Override
      public QueryResultVMRecord build() {
         return new QueryResultVMRecord(this);
      }

      public B fromQueryResultVMRecord(QueryResultVMRecord in) {
         return fromQueryResultRecordType(in)
                  .name(in.getName())
                  .containerName(in.getContainerName())
                  .container(in.getContainer())
                  .vdc(in.getVdc())
                  .isVAppTemplate(in.isVAppTemplate())
                  .isDeleted(in.isDeleted())
                  .guestOs(in.getGuestOs())
                  .numberOfCpus(in.getNumberOfCpus())
                  .memoryMB(in.getMemoryMB())
                  .status(in.getStatus())
                  .isBusy(in.isBusy())
                  .isDeployed(in.isDeployed())
                  .isPublished(in.isPublished())
                  .catalogName(in.isCatalogName())
                  .hardwareVersion(in.getHardwareVersion())
                  .isInMaintenanceMode(in.isInMaintenanceMode());
      }

   }

   @XmlAttribute
   private String name;
   @XmlAttribute
   private String containerName;
   @XmlAttribute
   private String container;
   @XmlAttribute
   private String vdc;
   @XmlAttribute
   private Boolean isVAppTemplate;
   @XmlAttribute
   private Boolean isDeleted;
   @XmlAttribute
   private String guestOs;
   @XmlAttribute
   private Integer numberOfCpus;
   @XmlAttribute
   private Integer memoryMB;
   @XmlAttribute
   private String status;
   @XmlAttribute
   private Boolean isBusy;
   @XmlAttribute
   private Boolean isDeployed;
   @XmlAttribute
   private Boolean isPublished;
   @XmlAttribute
   private Boolean catalogName;
   @XmlAttribute
   private Integer hardwareVersion;
   @XmlAttribute
   private Boolean isInMaintenanceMode;

   protected QueryResultVMRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.containerName = builder.containerName;
      this.container = builder.container;
      this.vdc = builder.vdc;
      this.isVAppTemplate = builder.isVAppTemplate;
      this.isDeleted = builder.isDeleted;
      this.guestOs = builder.guestOs;
      this.numberOfCpus = builder.numberOfCpus;
      this.memoryMB = builder.memoryMB;
      this.status = builder.status;
      this.isBusy = builder.isBusy;
      this.isDeployed = builder.isDeployed;
      this.isPublished = builder.isPublished;
      this.catalogName = builder.catalogName;
      this.hardwareVersion = builder.hardwareVersion;
      this.isInMaintenanceMode = builder.isInMaintenanceMode;
   }

   protected QueryResultVMRecord() {
      // for JAXB
   }

   /**
    * name
    */
   public String getName() {
      return name;
   }

   /**
    * Vapp name or Vapp template name
    */
   public String getContainerName() {
      return containerName;
   }

   /**
    * Vapp or Vapp template
    */
   public String getContainer() {
      return container;
   }

   /**
    * vDC reference or id
    */
   public String getVdc() {
      return vdc;
   }

   /**
    * Shows whether the VM belongs to VApp or VAppTemplate
    */
   public Boolean isVAppTemplate() {
      return isVAppTemplate;
   }

   /**
    * Shows whether it is deleted
    */
   public Boolean isDeleted() {
      return isDeleted;
   }

   /**
    * Guest operating system
    */
   public String getGuestOs() {
      return guestOs;
   }

   /**
    * Number of CPUs
    */
   public Integer getNumberOfCpus() {
      return numberOfCpus;
   }

   /**
    * Memory in MB
    */
   public Integer getMemoryMB() {
      return memoryMB;
   }

   /**
    * Status
    */
   public String getStatus() {
      return status;
   }

   /**
    * Shows whether it is busy
    */
   public Boolean isBusy() {
      return isBusy;
   }

   /**
    * Shows whether it is deployed
    */
   public Boolean isDeployed() {
      return isDeployed;
   }

   /**
    * Shows whether it is in published catalog
    */
   public Boolean isPublished() {
      return isPublished;
   }

   /**
    * Catalog name
    */
   public Boolean isCatalogName() {
      return catalogName;
   }

   /**
    * Hardware version
    */
   public Integer getHardwareVersion() {
      return hardwareVersion;
   }

   /**
    * Shows whether it is in maintenance mode
    */
   public Boolean isInMaintenanceMode() {
      return isInMaintenanceMode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultVMRecord that = QueryResultVMRecord.class.cast(o);

      return super.equals(that) && equal(name, that.name) && equal(containerName, that.containerName) && equal(container, that.container) && equal(vdc, that.vdc) && equal(isVAppTemplate, that.isVAppTemplate) && equal(isDeleted, that.isDeleted) && equal(guestOs, that.guestOs) && equal(numberOfCpus, that.numberOfCpus) && equal(memoryMB, that.memoryMB) && equal(status, that.status) && equal(isBusy, that.isBusy) && equal(isDeployed, that.isDeployed) && equal(isPublished, that.isPublished) && equal(catalogName, that.catalogName) && equal(hardwareVersion, that.hardwareVersion) && equal(isInMaintenanceMode, that.isInMaintenanceMode);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, containerName, container, vdc, isVAppTemplate, isDeleted, guestOs, numberOfCpus, memoryMB, status, isBusy, isDeployed, isPublished, catalogName, hardwareVersion, isInMaintenanceMode);
   }
   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("containerName", containerName).add("container", container).add("vdc", vdc).add("isVAppTemplate", isVAppTemplate).add("isDeleted", isDeleted).add("guestOs", guestOs).add("numberOfCpus", numberOfCpus).add("memoryMB", memoryMB).add("status", status).add("isBusy", isBusy).add("isDeployed", isDeployed).add("isPublished", isPublished).add("catalogName", catalogName).add("hardwareVersion", hardwareVersion).add("isInMaintenanceMode", isInMaintenanceMode);
   }
}
