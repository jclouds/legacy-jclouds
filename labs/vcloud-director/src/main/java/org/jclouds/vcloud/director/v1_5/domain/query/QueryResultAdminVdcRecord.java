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
 * Represents the results from a AdminVdc vCloud query as a record.
 * 
 * @author Aled Sage
 */
@XmlRootElement(name = "VdcRecord")
@XmlType(name = "QueryResultAdminVdcRecordType")
public class QueryResultAdminVdcRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultAdminVdcRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private Boolean isEnabled;
      private Long cpuAllocationMhz;
      private Long cpuLimitMhz;
      private Long cpuUsedMhz;
      private Long memoryAllocationMB;
      private Long memoryLimitMB;
      private Long memoryUsedMB;
      private Long storageAllocationMB;
      private Long storageLimitMB;
      private Long storageUsedMB;
      private String providerVdcName;
      private String providerVdc;
      private String orgName;
      private String org;
      private Integer numberOfVApps;
      private Integer numberOfMedia;
      private Integer numberOfVAppTemplates;
      private Boolean isSystemVdc;
      private Boolean isBusy;
      private String status;
      private String networkPool;

      /**
       * @see QueryResultAdminVdcRecord#getName()
       */
      public B name(String val) {
         this.name = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#isEnabled()
       */
      public B isEnabled(Boolean val) {
         this.isEnabled = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getCpuAllocationMhz()
       */
      public B cpuAllocationMhz(Long val) {
         this.cpuAllocationMhz = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getCpuLimitMhz()
       */
      public B cpuLimitMhz(Long val) {
         this.cpuLimitMhz = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getCpuUsedMhz()
       */
      public B cpuUsedMhz(Long val) {
         this.cpuUsedMhz = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getMemoryAllocationMB()
       */
      public B memoryAllocationMB(Long val) {
         this.memoryAllocationMB = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getMemoryLimitMB()
       */
      public B memoryLimitMB(Long val) {
         this.memoryLimitMB = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getMemoryUsedMB()
       */
      public B memoryUsedMB(Long val) {
         this.memoryUsedMB = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getStorageAllocationMB()
       */
      public B storageAllocationMB(Long val) {
         this.storageAllocationMB = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getStorageLimitMB()
       */
      public B storageLimitMB(Long val) {
         this.storageLimitMB = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getStorageUsedMB()
       */
      public B storageUsedMB(Long val) {
         this.storageUsedMB = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getProviderVdcName()
       */
      public B providerVdcName(String val) {
         this.providerVdcName = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getProviderVdc()
       */
      public B providerVdc(String val) {
         this.providerVdc = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getOrgName()
       */
      public B orgName(String val) {
         this.orgName = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#get()
       */
      public B org(String val) {
         this.org = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getNumberOfVApps()
       */
      public B numberOfVApps(Integer val) {
         this.numberOfVApps = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getNumberOfMedia()
       */
      public B numberOfMedia(Integer val) {
         this.numberOfMedia = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getNumberOfVAppTemplates()
       */
      public B numberOfVAppTemplates(Integer val) {
         this.numberOfVAppTemplates = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#isSystemVdc()
       */
      public B isSystemVdc(Boolean val) {
         this.isSystemVdc = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#isBusy()
       */
      public B isBusy(Boolean val) {
         this.isBusy = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getStatus()
       */
      public B status(String val) {
         this.status = val;
         return self();
      }

      /**
       * @see QueryResultAdminVdcRecord#getNetworkPool()
       */
      public B networkPool(String val) {
         this.networkPool = val;
         return self();
      }

      @Override
      public QueryResultAdminVdcRecord build() {
         return new QueryResultAdminVdcRecord(this);
      }

      public B fromQueryResultAdminVdcRecord(QueryResultAdminVdcRecord in) {
         return fromQueryResultRecordType(in)
                  .name(in.getName())
                  .isEnabled(in.isEnabled())
                  .cpuAllocationMhz(in.getCpuAllocationMhz())
                  .cpuLimitMhz(in.getCpuLimitMhz())
                  .cpuUsedMhz(in.getCpuUsedMhz())
                  .memoryAllocationMB(in.getMemoryAllocationMB())
                  .memoryLimitMB(in.getMemoryLimitMB())
                  .memoryUsedMB(in.getMemoryUsedMB())
                  .storageAllocationMB(in.getStorageAllocationMB())
                  .storageLimitMB(in.getStorageLimitMB())
                  .storageUsedMB(in.getStorageUsedMB())
                  .providerVdcName(in.getProviderVdcName())
                  .providerVdc(in.getProviderVdc())
                  .orgName(in.getOrgName())
                  .org(in.get())
                  .numberOfVApps(in.getNumberOfVApps())
                  .numberOfMedia(in.getNumberOfMedia())
                  .numberOfVAppTemplates(in.getNumberOfVAppTemplates())
                  .isSystemVdc(in.isSystemVdc())
                  .isBusy(in.isBusy())
                  .status(in.getStatus())
                  .networkPool(in.getNetworkPool());
      }

   }

   @XmlAttribute
   private String name;
   @XmlAttribute
   private Boolean isEnabled;
   @XmlAttribute
   private Long cpuAllocationMhz;
   @XmlAttribute
   private Long cpuLimitMhz;
   @XmlAttribute
   private Long cpuUsedMhz;
   @XmlAttribute
   private Long memoryAllocationMB;
   @XmlAttribute
   private Long memoryLimitMB;
   @XmlAttribute
   private Long memoryUsedMB;
   @XmlAttribute
   private Long storageAllocationMB;
   @XmlAttribute
   private Long storageLimitMB;
   @XmlAttribute
   private Long storageUsedMB;
   @XmlAttribute
   private String providerVdcName;
   @XmlAttribute
   private String providerVdc;
   @XmlAttribute
   private String orgName;
   @XmlAttribute
   private String org;
   @XmlAttribute
   private Integer numberOfVApps;
   @XmlAttribute
   private Integer numberOfMedia;
   @XmlAttribute
   private Integer numberOfVAppTemplates;
   @XmlAttribute
   private Boolean isSystemVdc;
   @XmlAttribute
   private Boolean isBusy;
   @XmlAttribute
   private String status;
   @XmlAttribute
   private String networkPool;

   protected QueryResultAdminVdcRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.isEnabled = builder.isEnabled;
      this.cpuAllocationMhz = builder.cpuAllocationMhz;
      this.cpuLimitMhz = builder.cpuLimitMhz;
      this.cpuUsedMhz = builder.cpuUsedMhz;
      this.memoryAllocationMB = builder.memoryAllocationMB;
      this.memoryLimitMB = builder.memoryLimitMB;
      this.memoryUsedMB = builder.memoryUsedMB;
      this.storageAllocationMB = builder.storageAllocationMB;
      this.storageLimitMB = builder.storageLimitMB;
      this.storageUsedMB = builder.storageUsedMB;
      this.providerVdcName = builder.providerVdcName;
      this.providerVdc = builder.providerVdc;
      this.orgName = builder.orgName;
      this.org = builder.org;
      this.numberOfVApps = builder.numberOfVApps;
      this.numberOfMedia = builder.numberOfMedia;
      this.numberOfVAppTemplates = builder.numberOfVAppTemplates;
      this.isSystemVdc = builder.isSystemVdc;
      this.isBusy = builder.isBusy;
      this.status = builder.status;
      this.networkPool = builder.networkPool;
   }

   protected QueryResultAdminVdcRecord() {
      // for JAXB
   }

   /**
    * name
    */
   public String getName() {
      return name;
   }

   /**
    * Shows whether it is enabled
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Cpu allocation in Mhz
    */
   public Long getCpuAllocationMhz() {
      return cpuAllocationMhz;
   }

   /**
    * Cpu limit in Mhz
    */
   public Long getCpuLimitMhz() {
      return cpuLimitMhz;
   }

   /**
    * Cpu used in Mhz
    */
   public Long getCpuUsedMhz() {
      return cpuUsedMhz;
   }

   /**
    * Memory allocation in MB
    */
   public Long getMemoryAllocationMB() {
      return memoryAllocationMB;
   }

   /**
    * Memory limit in MB
    */
   public Long getMemoryLimitMB() {
      return memoryLimitMB;
   }

   /**
    * Memory used in MB
    */
   public Long getMemoryUsedMB() {
      return memoryUsedMB;
   }

   /**
    * Storage allocation in MB
    */
   public Long getStorageAllocationMB() {
      return storageAllocationMB;
   }

   /**
    * Storage limit in MB
    */
   public Long getStorageLimitMB() {
      return storageLimitMB;
   }

   /**
    * Storage used in MB
    */
   public Long getStorageUsedMB() {
      return storageUsedMB;
   }

   /**
    * provider vDC name
    */
   public String getProviderVdcName() {
      return providerVdcName;
   }

   /**
    * vDC reference or id
    */
   public String getProviderVdc() {
      return providerVdc;
   }

   /**
    * Organization name
    */
   public String getOrgName() {
      return orgName;
   }

   /**
    * Organization reference or id
    */
   public String get() {
      return org;
   }

   /**
    * Number of vApps
    */
   public Integer getNumberOfVApps() {
      return numberOfVApps;
   }

   /**
    * Number of media
    */
   public Integer getNumberOfMedia() {
      return numberOfMedia;
   }

   /**
    * Number of vApp templates
    */
   public Integer getNumberOfVAppTemplates() {
      return numberOfVAppTemplates;
   }

   /**
    * Shows whether it is a system vDC
    */
   public Boolean isSystemVdc() {
      return isSystemVdc;
   }

   /**
    * Shows whether it is busy
    */
   public Boolean isBusy() {
      return isBusy;
   }

   /**
    * Status
    */
   public String getStatus() {
      return status;
   }

   /**
    * Network pool reference or id
    */
   public String getNetworkPool() {
      return networkPool;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultAdminVdcRecord that = QueryResultAdminVdcRecord.class.cast(o);

      return super.equals(that) && equal(name, that.name) && equal(isEnabled, that.isEnabled) 
               && equal(cpuAllocationMhz, that.cpuAllocationMhz) && equal(cpuLimitMhz, that.cpuLimitMhz)
               && equal(cpuUsedMhz, that.cpuUsedMhz) && equal(memoryAllocationMB, that.memoryAllocationMB) 
               && equal(memoryLimitMB, that.memoryLimitMB) && equal(memoryUsedMB, that.memoryUsedMB) 
               && equal(storageAllocationMB, that.storageAllocationMB) && equal(storageLimitMB, that.storageLimitMB) 
               && equal(storageUsedMB, that.storageUsedMB) && equal(providerVdcName, that.providerVdcName) 
               && equal(providerVdc, that.providerVdc) && equal(orgName, that.orgName) && equal(org, that.org) 
               && equal(numberOfVApps, that.numberOfVApps) && equal(numberOfMedia, that.numberOfMedia) 
               && equal(numberOfVAppTemplates, that.numberOfVAppTemplates) && equal(isSystemVdc, that.isSystemVdc) 
               && equal(isBusy, that.isBusy) && equal(status, that.status) && equal(networkPool, that.networkPool);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, isEnabled, cpuAllocationMhz, cpuLimitMhz, cpuUsedMhz, 
               memoryAllocationMB, memoryLimitMB, memoryUsedMB, storageAllocationMB, storageLimitMB, storageUsedMB, 
               providerVdcName, providerVdc, orgName, org, numberOfVApps, numberOfMedia, numberOfVAppTemplates, 
               isSystemVdc, isBusy, status, networkPool);
   }
   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("isEnabled", isEnabled).add("cpuAllocationMhz", cpuAllocationMhz)
               .add("cpuLimitMhz", cpuLimitMhz).add("cpuUsedMhz", cpuUsedMhz).add("memoryAllocationMB", memoryAllocationMB)
               .add("memoryLimitMB", memoryLimitMB).add("memoryUsedMB", memoryUsedMB)
               .add("storageAllocationMB", storageAllocationMB).add("storageLimitMB", storageLimitMB)
               .add("storageUsedMB", storageUsedMB).add("providerVdcName", providerVdcName).add("providerVdc", providerVdc)
               .add("orgName", orgName).add("org", org).add("numberOfVApps", numberOfVApps).add("numberOfMedia", numberOfMedia)
               .add("numberOfVAppTemplates", numberOfVAppTemplates).add("isSystemVdc", isSystemVdc).add("isBusy", isBusy)
               .add("status", status).add("networkPool", networkPool);
   }
}
