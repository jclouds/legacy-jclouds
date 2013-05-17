/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The virtual guest data type presents the structure in which all virtual guests will be presented.
 * Internally, the structure supports various virtualization platforms with no change to external
 * interaction. <br/>
 * A guest, also known as a virtual server or CloudLayer Computing Instance, represents an
 * allocation of resources on a virtual host.
 *
 * The hostname and domain must be alphanumeric strings that may be separated by periods '.'.
 * The only other allowable special character is the dash '-'.
 * However the special characters '.' and '-' may not be consecutive.
 * Each alphanumeric string separated by a period is considered a label.
 * Labels must begin and end with an alphanumeric character.
 * Each label cannot be solely comprised of digits and must be between 1-63 characters in length.
 * The last label, the TLD (top level domain) must be between 2-6 alphabetic characters.
 * The domain portion must consist of least one label followed by a period '.' then ending with the TLD label.
 * Combining the hostname, followed by a period '.', followed by the domain gives the FQDN (fully qualified domain name),
 * which may not exceed 253 characters in total length.
 *
 * @author Adrian Cole
 * @see <a href=
"http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest"
/>
 */
public class VirtualGuest {

   /**
    * These states come from the powerState field. i.e.
    * https://api.softlayer.com/rest/v3/SoftLayer_Account/getVirtualGuests/{id}?objectMask=powerState
    */
   public static enum State {
      HALTED,
      PAUSED,
      RUNNING,
      UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static class BillingItem {
      private final int id;

      @ConstructorProperties("id")
      public BillingItem(int id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return "[id=" + id + "]";
      }
   }
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVirtualGuest(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int accountId;
      protected Date createDate;
      protected boolean dedicatedAccountHostOnly;
      protected String domain;
      protected String fullyQualifiedDomainName;
      protected String hostname;
      protected int id;
      protected Date lastVerifiedDate;
      protected int maxCpu;
      protected String maxCpuUnits;
      protected int maxMemory;
      protected Date metricPollDate;
      protected Date modifyDate;
      protected String notes;
      protected boolean privateNetworkOnly;
      protected int startCpus;
      protected int statusId;
      protected String uuid;
      protected String primaryBackendIpAddress;
      protected String primaryIpAddress;
      protected int billingItemId;
      protected OperatingSystem operatingSystem;
      protected Datacenter datacenter;
      protected PowerState powerState;

      /**
       * @see VirtualGuest#getAccountId()
       */
      public T accountId(int accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see VirtualGuest#getCreateDate()
       */
      public T createDate(Date createDate) {
         this.createDate = createDate;
         return self();
      }

      /**
       * @see VirtualGuest#isDedicatedAccountHostOnly()
       */
      public T dedicatedAccountHostOnly(boolean dedicatedAccountHostOnly) {
         this.dedicatedAccountHostOnly = dedicatedAccountHostOnly;
         return self();
      }

      /**
       * @see VirtualGuest#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see VirtualGuest#getFullyQualifiedDomainName()
       */
      public T fullyQualifiedDomainName(String fullyQualifiedDomainName) {
         this.fullyQualifiedDomainName = fullyQualifiedDomainName;
         return self();
      }

      /**
       * @see VirtualGuest#getHostname()
       */
      public T hostname(String hostname) {
         this.hostname = hostname;
         return self();
      }

      /**
       * @see VirtualGuest#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see VirtualGuest#getLastVerifiedDate()
       */
      public T lastVerifiedDate(Date lastVerifiedDate) {
         this.lastVerifiedDate = lastVerifiedDate;
         return self();
      }

      /**
       * @see VirtualGuest#getMaxCpu()
       */
      public T maxCpu(int maxCpu) {
         this.maxCpu = maxCpu;
         return self();
      }

      /**
       * @see VirtualGuest#getMaxCpuUnits()
       */
      public T maxCpuUnits(String maxCpuUnits) {
         this.maxCpuUnits = maxCpuUnits;
         return self();
      }

      /**
       * @see VirtualGuest#getMaxMemory()
       */
      public T maxMemory(int maxMemory) {
         this.maxMemory = maxMemory;
         return self();
      }

      /**
       * @see VirtualGuest#getMetricPollDate()
       */
      public T metricPollDate(Date metricPollDate) {
         this.metricPollDate = metricPollDate;
         return self();
      }

      /**
       * @see VirtualGuest#getModifyDate()
       */
      public T modifyDate(Date modifyDate) {
         this.modifyDate = modifyDate;
         return self();
      }

      /**
       * @see VirtualGuest#getNotes()
       */
      public T notes(String notes) {
         this.notes = notes;
         return self();
      }

      /**
       * @see VirtualGuest#isPrivateNetworkOnly()
       */
      public T privateNetworkOnly(boolean privateNetworkOnly) {
         this.privateNetworkOnly = privateNetworkOnly;
         return self();
      }

      /**
       * @see VirtualGuest#getStartCpus()
       */
      public T startCpus(int startCpus) {
         this.startCpus = startCpus;
         return self();
      }

      /**
       * @see VirtualGuest#getStatusId()
       */
      public T statusId(int statusId) {
         this.statusId = statusId;
         return self();
      }

      /**
       * @see VirtualGuest#getUuid()
       */
      public T uuid(String uuid) {
         this.uuid = uuid;
         return self();
      }

      /**
       * @see VirtualGuest#getPrimaryBackendIpAddress()
       */
      public T primaryBackendIpAddress(String primaryBackendIpAddress) {
         this.primaryBackendIpAddress = primaryBackendIpAddress;
         return self();
      }

      /**
       * @see VirtualGuest#getPrimaryIpAddress()
       */
      public T primaryIpAddress(String primaryIpAddress) {
         this.primaryIpAddress = primaryIpAddress;
         return self();
      }

      /**
       * @see VirtualGuest#getBillingItemId()
       */
      public T billingItemId(int billingItemId) {
         this.billingItemId = billingItemId;
         return self();
      }

      /**
       * @see VirtualGuest#getOperatingSystem()
       */
      public T operatingSystem(OperatingSystem operatingSystem) {
         this.operatingSystem = operatingSystem;
         return self();
      }

      /**
       * @see VirtualGuest#getDatacenter()
       */
      public T datacenter(Datacenter datacenter) {
         this.datacenter = datacenter;
         return self();
      }

      /**
       * @see VirtualGuest#getPowerState()
       */
      public T powerState(PowerState powerState) {
         this.powerState = powerState;
         return self();
      }

      public VirtualGuest build() {
         return new VirtualGuest(accountId, createDate, dedicatedAccountHostOnly, domain, fullyQualifiedDomainName, hostname,
               id, lastVerifiedDate, maxCpu, maxCpuUnits, maxMemory, metricPollDate, modifyDate, notes, privateNetworkOnly,
               startCpus, statusId, uuid, primaryBackendIpAddress, primaryIpAddress, new BillingItem(billingItemId),
               operatingSystem, datacenter, powerState);
      }

      public T fromVirtualGuest(VirtualGuest in) {
         return this
               .accountId(in.getAccountId())
               .createDate(in.getCreateDate())
               .dedicatedAccountHostOnly(in.isDedicatedAccountHostOnly())
               .domain(in.getDomain())
               .fullyQualifiedDomainName(in.getFullyQualifiedDomainName())
               .hostname(in.getHostname())
               .id(in.getId())
               .lastVerifiedDate(in.getLastVerifiedDate())
               .maxCpu(in.getMaxCpu())
               .maxCpuUnits(in.getMaxCpuUnits())
               .maxMemory(in.getMaxMemory())
               .metricPollDate(in.getMetricPollDate())
               .modifyDate(in.getModifyDate())
               .notes(in.getNotes())
               .privateNetworkOnly(in.isPrivateNetworkOnly())
               .startCpus(in.getStartCpus())
               .statusId(in.getStatusId())
               .uuid(in.getUuid())
               .primaryBackendIpAddress(in.getPrimaryBackendIpAddress())
               .primaryIpAddress(in.getPrimaryIpAddress())
               .billingItemId(in.getBillingItemId())
               .operatingSystem(in.getOperatingSystem())
               .datacenter(in.getDatacenter())
               .powerState(in.getPowerState());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int accountId;
   private final Date createDate;
   private final boolean dedicatedAccountHostOnly;
   private final String domain;
   private final String fullyQualifiedDomainName;
   private final String hostname;
   private final int id;
   private final Date lastVerifiedDate;
   private final int maxCpu;
   private final String maxCpuUnits;
   private final int maxMemory;
   private final Date metricPollDate;
   private final Date modifyDate;
   private final String notes;
   private final boolean privateNetworkOnly;
   private final int startCpus;
   private final int statusId;
   private final String uuid;
   private final String primaryBackendIpAddress;
   private final String primaryIpAddress;
   private final int billingItemId;
   private final OperatingSystem operatingSystem;
   private final Datacenter datacenter;
   private final PowerState powerState;

   @ConstructorProperties({
         "accountId", "createDate", "dedicatedAccountHostOnlyFlag", "domain", "fullyQualifiedDomainName", "hostname", "id", "lastVerifiedDate", "maxCpu", "maxCpuUnits", "maxMemory", "metricPollDate", "modifyDate", "notes", "privateNetworkOnlyFlag", "startCpus", "statusId", "uuid", "primaryBackendIpAddress", "primaryIpAddress", "billingItem", "operatingSystem", "datacenter", "powerState"
   })
   protected VirtualGuest(int accountId, @Nullable Date createDate, boolean dedicatedAccountHostOnly, @Nullable String domain,
                          @Nullable String fullyQualifiedDomainName, @Nullable String hostname, int id, @Nullable Date lastVerifiedDate,
                          int maxCpu, @Nullable String maxCpuUnits, int maxMemory, @Nullable Date metricPollDate, @Nullable Date modifyDate,
                          @Nullable String notes, boolean privateNetworkOnly, int startCpus, int statusId, @Nullable String uuid,
                          @Nullable String primaryBackendIpAddress, @Nullable String primaryIpAddress, @Nullable BillingItem billingItem,
                          @Nullable OperatingSystem operatingSystem, @Nullable Datacenter datacenter, @Nullable PowerState powerState) {
      this.accountId = accountId;
      this.createDate = createDate;
      this.dedicatedAccountHostOnly = dedicatedAccountHostOnly;
      this.domain = domain;
      this.fullyQualifiedDomainName = fullyQualifiedDomainName;
      this.hostname = hostname;
      this.id = id;
      this.lastVerifiedDate = lastVerifiedDate;
      this.maxCpu = maxCpu;
      this.maxCpuUnits = maxCpuUnits;
      this.maxMemory = maxMemory;
      this.metricPollDate = metricPollDate;
      this.modifyDate = modifyDate;
      this.notes = notes;
      this.privateNetworkOnly = privateNetworkOnly;
      this.startCpus = startCpus;
      this.statusId = statusId;
      this.uuid = uuid;
      this.primaryBackendIpAddress = primaryBackendIpAddress;
      this.primaryIpAddress = primaryIpAddress;
      this.billingItemId = billingItem == null ? -1 : billingItem.id;
      this.operatingSystem = operatingSystem;
      this.datacenter = datacenter;
      this.powerState = powerState;
   }

   /**
    * @return A computing instance's associated account id
    */
   public int getAccountId() {
      return this.accountId;
   }

   /**
    * @return The date a virtual computing instance was created.
    */
   @Nullable
   public Date getCreateDate() {
      return this.createDate;
   }

   /**
    * @return When true this flag specifies that a compute instance is to run on hosts that only
   have guests from the same account.
    */
   public boolean isDedicatedAccountHostOnly() {
      return this.dedicatedAccountHostOnly;
   }

   /**
    * @return A computing instance's domain name
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return A name reflecting the hostname and domain of the computing instance.
    */
   @Nullable
   public String getFullyQualifiedDomainName() {
      return this.fullyQualifiedDomainName;
   }

   /**
    * @return A virtual computing instance's hostname
    */
   @Nullable
   public String getHostname() {
      return this.hostname;
   }

   /**
    * @return Unique ID for a computing instance.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The last timestamp of when the guest was verified as a resident virtual machine on the
   host's hypervisor platform.
    */
   @Nullable
   public Date getLastVerifiedDate() {
      return this.lastVerifiedDate;
   }

   /**
    * @return The maximum amount of CPU resources a computing instance may utilize.
    */
   public int getMaxCpu() {
      return this.maxCpu;
   }

   /**
    * @return The unit of the maximum amount of CPU resources a computing instance may utilize.
    */
   @Nullable
   public String getMaxCpuUnits() {
      return this.maxCpuUnits;
   }

   /**
    * @return The maximum amount of memory a computing instance may utilize.
    */
   public int getMaxMemory() {
      return this.maxMemory;
   }

   /**
    * @return The date of the most recent metric tracking poll performed.
    */
   @Nullable
   public Date getMetricPollDate() {
      return this.metricPollDate;
   }

   /**
    * @return The date a virtual computing instance was last modified.
    */
   @Nullable
   public Date getModifyDate() {
      return this.modifyDate;
   }

   /**
    * @return A small note about a cloud instance to use at your discretion.
    */
   @Nullable
   public String getNotes() {
      return this.notes;
   }

   /**
    * @return Whether the computing instance only has access to the private network.
    */
   public boolean isPrivateNetworkOnly() {
      return this.privateNetworkOnly;
   }

   /**
    * @return The number of CPUs available to a computing instance upon startup.
    */
   public int getStartCpus() {
      return this.startCpus;
   }

   /**
    * @return A computing instances status ID
    */
   public int getStatusId() {
      return this.statusId;
   }

   /**
    * @return Unique ID for a computing instance's record on a virtualization platform.
    */
   @Nullable
   public String getUuid() {
      return this.uuid;
   }

   /**
    * @return private ip address
    */
   @Nullable
   public String getPrimaryBackendIpAddress() {
      return this.primaryBackendIpAddress;
   }

   /**
    * @return public ip address
    */
   @Nullable
   public String getPrimaryIpAddress() {
      return this.primaryIpAddress;
   }

   /**
    * @return The billing item for a CloudLayer Compute Instance.
    */
   public int getBillingItemId() {
      return this.billingItemId;
   }

   /**
    * @return A guest's operating system.
    */
   @Nullable
   public OperatingSystem getOperatingSystem() {
      return this.operatingSystem;
   }

   /**
    * @return The guest's datacenter
    */
   @Nullable
   public Datacenter getDatacenter() {
      return this.datacenter;
   }

   /**
    * @return The current power state of a virtual guest.
    */
   @Nullable
   public PowerState getPowerState() {
      return this.powerState;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accountId, createDate, dedicatedAccountHostOnly, domain, fullyQualifiedDomainName, hostname, id, lastVerifiedDate, maxCpu, maxCpuUnits, maxMemory, metricPollDate, modifyDate, notes, privateNetworkOnly, startCpus, statusId, uuid, primaryBackendIpAddress, primaryIpAddress, billingItemId, operatingSystem, datacenter, powerState);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VirtualGuest that = VirtualGuest.class.cast(obj);
      return Objects.equal(this.accountId, that.accountId)
            && Objects.equal(this.createDate, that.createDate)
            && Objects.equal(this.dedicatedAccountHostOnly, that.dedicatedAccountHostOnly)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.fullyQualifiedDomainName, that.fullyQualifiedDomainName)
            && Objects.equal(this.hostname, that.hostname)
            && Objects.equal(this.id, that.id)
            && Objects.equal(this.lastVerifiedDate, that.lastVerifiedDate)
            && Objects.equal(this.maxCpu, that.maxCpu)
            && Objects.equal(this.maxCpuUnits, that.maxCpuUnits)
            && Objects.equal(this.maxMemory, that.maxMemory)
            && Objects.equal(this.metricPollDate, that.metricPollDate)
            && Objects.equal(this.modifyDate, that.modifyDate)
            && Objects.equal(this.notes, that.notes)
            && Objects.equal(this.privateNetworkOnly, that.privateNetworkOnly)
            && Objects.equal(this.startCpus, that.startCpus)
            && Objects.equal(this.statusId, that.statusId)
            && Objects.equal(this.uuid, that.uuid)
            && Objects.equal(this.primaryBackendIpAddress, that.primaryBackendIpAddress)
            && Objects.equal(this.primaryIpAddress, that.primaryIpAddress)
            && Objects.equal(this.billingItemId, that.billingItemId)
            && Objects.equal(this.operatingSystem, that.operatingSystem)
            && Objects.equal(this.datacenter, that.datacenter)
            && Objects.equal(this.powerState, that.powerState);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("accountId", accountId).add("createDate", createDate).add("dedicatedAccountHostOnly", dedicatedAccountHostOnly).add("domain", domain).add("fullyQualifiedDomainName", fullyQualifiedDomainName).add("hostname", hostname).add("id", id).add("lastVerifiedDate", lastVerifiedDate).add("maxCpu", maxCpu).add("maxCpuUnits", maxCpuUnits).add("maxMemory", maxMemory).add("metricPollDate", metricPollDate).add("modifyDate", modifyDate).add("notes", notes).add("privateNetworkOnly", privateNetworkOnly).add("startCpus", startCpus).add("statusId", statusId).add("uuid", uuid).add("primaryBackendIpAddress", primaryBackendIpAddress).add("primaryIpAddress", primaryIpAddress).add("billingItemId", billingItemId).add("operatingSystem", operatingSystem).add("datacenter", datacenter).add("powerState", powerState);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
