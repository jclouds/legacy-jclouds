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
 * Represents the results from a AdminUser vCloud query as a record.
 * 
 * @author Aled Sage
 */
@XmlRootElement(name = "UserRecord")
@XmlType(name = "QueryResultAdminUserRecordType")
public class QueryResultAdminUserRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultAdminUserRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String org;
      private String fullName;
      private Boolean isEnabled;
      private Integer numberOfDeployedVMs;
      private Integer deployedVMQuota;
      private Integer numberOfStoredVMs;
      private Integer storedVMQuota;
      private Boolean isLdapUser;
      private Integer deployedVMQuotaRank;
      private Integer storedVMQuotaRank;

      /**
       * @see QueryResultAdminUserRecord#getName()
       */
      public B name(String val) {
         this.name = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#get()
       */
      public B org(String val) {
         this.org = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#getFullName()
       */
      public B fullName(String val) {
         this.fullName = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#isEnabled()
       */
      public B isEnabled(Boolean val) {
         this.isEnabled = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#getNumberOfDeployedVMs()
       */
      public B numberOfDeployedVMs(Integer val) {
         this.numberOfDeployedVMs = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#getDeployedVMQuota()
       */
      public B deployedVMQuota(Integer val) {
         this.deployedVMQuota = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#getNumberOfStoredVMs()
       */
      public B numberOfStoredVMs(Integer val) {
         this.numberOfStoredVMs = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#getStoredVMQuota()
       */
      public B storedVMQuota(Integer val) {
         this.storedVMQuota = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#isLdapUser()
       */
      public B isLdapUser(Boolean val) {
         this.isLdapUser = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#getDeployedVMQuotaRank()
       */
      public B deployedVMQuotaRank(Integer val) {
         this.deployedVMQuotaRank = val;
         return self();
      }

      /**
       * @see QueryResultAdminUserRecord#getStoredVMQuotaRank()
       */
      public B storedVMQuotaRank(Integer val) {
         this.storedVMQuotaRank = val;
         return self();
      }

      @Override
      public QueryResultAdminUserRecord build() {
         return new QueryResultAdminUserRecord(this);
      }

      public B fromQueryResultAdminUserRecord(QueryResultAdminUserRecord in) {
         return fromQueryResultRecordType(in)
                  .name(in.getName())
                  .org(in.get())
                  .fullName(in.getFullName())
                  .isEnabled(in.isEnabled())
                  .numberOfDeployedVMs(in.getNumberOfDeployedVMs())
                  .deployedVMQuota(in.getDeployedVMQuota())
                  .numberOfStoredVMs(in.getNumberOfStoredVMs())
                  .storedVMQuota(in.getStoredVMQuota())
                  .isLdapUser(in.isLdapUser())
                  .deployedVMQuotaRank(in.getDeployedVMQuotaRank())
                  .storedVMQuotaRank(in.getStoredVMQuotaRank());
      }

   }

   @XmlAttribute
   private String name;
   @XmlAttribute
   private String org;
   @XmlAttribute
   private String fullName;
   @XmlAttribute
   private Boolean isEnabled;
   @XmlAttribute
   private Integer numberOfDeployedVMs;
   @XmlAttribute
   private Integer deployedVMQuota;
   @XmlAttribute
   private Integer numberOfStoredVMs;
   @XmlAttribute
   private Integer storedVMQuota;
   @XmlAttribute
   private Boolean isLdapUser;
   @XmlAttribute
   private Integer deployedVMQuotaRank;
   @XmlAttribute
   private Integer storedVMQuotaRank;

   protected QueryResultAdminUserRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.org = builder.org;
      this.fullName = builder.fullName;
      this.isEnabled = builder.isEnabled;
      this.numberOfDeployedVMs = builder.numberOfDeployedVMs;
      this.deployedVMQuota = builder.deployedVMQuota;
      this.numberOfStoredVMs = builder.numberOfStoredVMs;
      this.storedVMQuota = builder.storedVMQuota;
      this.isLdapUser = builder.isLdapUser;
      this.deployedVMQuotaRank = builder.deployedVMQuotaRank;
      this.storedVMQuotaRank = builder.storedVMQuotaRank;
   }

   protected QueryResultAdminUserRecord() {
      // for JAXB
   }

   /**
    * name
    */
   public String getName() {
      return name;
   }

   /**
    * Organization reference or id
    */
   public String get() {
      return org;
   }

   /**
    * Full name
    */
   public String getFullName() {
      return fullName;
   }

   /**
    * Shows whether it is enabled
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Number of deployed VMs
    */
   public Integer getNumberOfDeployedVMs() {
      return numberOfDeployedVMs;
   }

   /**
    * Deployed VM quota
    */
   public Integer getDeployedVMQuota() {
      return deployedVMQuota;
   }

   /**
    * Number of stored VMs
    */
   public Integer getNumberOfStoredVMs() {
      return numberOfStoredVMs;
   }

   /**
    * Stored VM Quota
    */
   public Integer getStoredVMQuota() {
      return storedVMQuota;
   }

   /**
    * Shows if the user was imported from LDAP
    */
   public Boolean isLdapUser() {
      return isLdapUser;
   }

   /**
    * Deployed VM quota rank
    */
   public Integer getDeployedVMQuotaRank() {
      return deployedVMQuotaRank;
   }

   /**
    * Stored VM Quota rank
    */
   public Integer getStoredVMQuotaRank() {
      return storedVMQuotaRank;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultAdminUserRecord that = QueryResultAdminUserRecord.class.cast(o);

      return super.equals(that) && equal(name, that.name) && equal(org, that.org) && equal(fullName, that.fullName) 
                && equal(isEnabled, that.isEnabled) && equal(numberOfDeployedVMs, that.numberOfDeployedVMs) 
                && equal(deployedVMQuota, that.deployedVMQuota) && equal(numberOfStoredVMs, that.numberOfStoredVMs) 
                && equal(storedVMQuota, that.storedVMQuota) && equal(isLdapUser, that.isLdapUser) 
                && equal(deployedVMQuotaRank, that.deployedVMQuotaRank) && equal(storedVMQuotaRank, that.storedVMQuotaRank);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, org, fullName, isEnabled, numberOfDeployedVMs, deployedVMQuota, numberOfStoredVMs, storedVMQuota, isLdapUser, deployedVMQuotaRank, storedVMQuotaRank);
   }
   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("org", org).add("fullName", fullName).add("isEnabled", isEnabled)
                              .add("numberOfDeployedVMs", numberOfDeployedVMs).add("deployedVMQuota", deployedVMQuota)
                              .add("numberOfStoredVMs", numberOfStoredVMs).add("storedVMQuota", storedVMQuota)
                              .add("isLdapUser", isLdapUser).add("deployedVMQuotaRank", deployedVMQuotaRank)
                              .add("storedVMQuotaRank", storedVMQuotaRank);
   }
}
