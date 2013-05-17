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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class PublicIPAddress
 *
 * @author Adrian Cole
 */
public class PublicIPAddress {

   /**
    */
   public static enum State {
      ALLOCATING, ALLOCATED, RELEASING, UNRECOGNIZED;

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

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromPublicIPAddress(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected Date allocated;
      protected String associatedNetworkId;
      protected String domain;
      protected String domainId;
      protected boolean usesVirtualNetwork;
      protected String IPAddress;
      protected boolean isSourceNAT;
      protected boolean isStaticNAT;
      protected String networkId;
      protected PublicIPAddress.State state;
      protected String virtualMachineDisplayName;
      protected String virtualMachineId;
      protected String virtualMachineName;
      protected String VLANId;
      protected String VLANName;
      protected String zoneId;
      protected String zoneName;
      protected String jobId;
      protected Integer jobStatus;

      /**
       * @see PublicIPAddress#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see PublicIPAddress#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see PublicIPAddress#getAllocated()
       */
      public T allocated(Date allocated) {
         this.allocated = allocated;
         return self();
      }

      /**
       * @see PublicIPAddress#getAssociatedNetworkId()
       */
      public T associatedNetworkId(String associatedNetworkId) {
         this.associatedNetworkId = associatedNetworkId;
         return self();
      }

      /**
       * @see PublicIPAddress#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see PublicIPAddress#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see PublicIPAddress#isUsesVirtualNetwork()
       */
      public T usesVirtualNetwork(boolean usesVirtualNetwork) {
         this.usesVirtualNetwork = usesVirtualNetwork;
         return self();
      }

      /**
       * @see PublicIPAddress#getIPAddress()
       */
      public T IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return self();
      }

      /**
       * @see PublicIPAddress#isSourceNAT()
       */
      public T isSourceNAT(boolean isSourceNAT) {
         this.isSourceNAT = isSourceNAT;
         return self();
      }

      /**
       * @see PublicIPAddress#isStaticNAT()
       */
      public T isStaticNAT(boolean isStaticNAT) {
         this.isStaticNAT = isStaticNAT;
         return self();
      }

      /**
       * @see PublicIPAddress#getNetworkId()
       */
      public T networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }

      /**
       * @see PublicIPAddress#getState()
       */
      public T state(PublicIPAddress.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see PublicIPAddress#getVirtualMachineDisplayName()
       */
      public T virtualMachineDisplayName(String virtualMachineDisplayName) {
         this.virtualMachineDisplayName = virtualMachineDisplayName;
         return self();
      }

      /**
       * @see PublicIPAddress#getVirtualMachineId()
       */
      public T virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return self();
      }

      /**
       * @see PublicIPAddress#getVirtualMachineName()
       */
      public T virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return self();
      }

      /**
       * @see PublicIPAddress#getVLANId()
       */
      public T VLANId(String VLANId) {
         this.VLANId = VLANId;
         return self();
      }

      /**
       * @see PublicIPAddress#getVLANName()
       */
      public T VLANName(String VLANName) {
         this.VLANName = VLANName;
         return self();
      }

      /**
       * @see PublicIPAddress#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see PublicIPAddress#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      /**
       * @see PublicIPAddress#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see PublicIPAddress#getJobStatus()
       */
      public T jobStatus(Integer jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      public PublicIPAddress build() {
         return new PublicIPAddress(id, account, allocated, associatedNetworkId, domain, domainId, usesVirtualNetwork, IPAddress, isSourceNAT, isStaticNAT, networkId, state, virtualMachineDisplayName, virtualMachineId, virtualMachineName, VLANId, VLANName, zoneId, zoneName, jobId, jobStatus);
      }

      public T fromPublicIPAddress(PublicIPAddress in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .allocated(in.getAllocated())
               .associatedNetworkId(in.getAssociatedNetworkId())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .usesVirtualNetwork(in.isUsesVirtualNetwork())
               .IPAddress(in.getIPAddress())
               .isSourceNAT(in.isSourceNAT())
               .isStaticNAT(in.isStaticNAT())
               .networkId(in.getNetworkId())
               .state(in.getState())
               .virtualMachineDisplayName(in.getVirtualMachineDisplayName())
               .virtualMachineId(in.getVirtualMachineId())
               .virtualMachineName(in.getVirtualMachineName())
               .VLANId(in.getVLANId())
               .VLANName(in.getVLANName())
               .zoneId(in.getZoneId())
               .zoneName(in.getZoneName())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final Date allocated;
   private final String associatedNetworkId;
   private final String domain;
   private final String domainId;
   private final boolean usesVirtualNetwork;
   private final String IPAddress;
   private final boolean isSourceNAT;
   private final boolean isStaticNAT;
   private final String networkId;
   private final PublicIPAddress.State state;
   private final String virtualMachineDisplayName;
   private final String virtualMachineId;
   private final String virtualMachineName;
   private final String VLANId;
   private final String VLANName;
   private final String zoneId;
   private final String zoneName;
   private final String jobId;
   private final Integer jobStatus;

   @ConstructorProperties({
         "id", "account", "allocated", "associatednetworkid", "domain", "domainid", "forvirtualnetwork", "ipaddress", "issourcenat",
         "isstaticnat", "networkid", "state", "virtualmachinedisplayname", "virtualmachineid", "virtualmachinename", "VLANid",
         "VLANname", "zoneid", "zonename", "jobid", "jobstatus"
   })
   protected PublicIPAddress(String id, @Nullable String account, @Nullable Date allocated, @Nullable String associatedNetworkId,
                             @Nullable String domain, @Nullable String domainId, boolean usesVirtualNetwork, @Nullable String IPAddress,
                             boolean isSourceNAT, boolean isStaticNAT, @Nullable String networkId, @Nullable PublicIPAddress.State state,
                             @Nullable String virtualMachineDisplayName, @Nullable String virtualMachineId, @Nullable String virtualMachineName,
                             @Nullable String VLANId, @Nullable String VLANName, @Nullable String zoneId, @Nullable String zoneName,
                             @Nullable String jobId, @Nullable Integer jobStatus) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.allocated = allocated;
      this.associatedNetworkId = associatedNetworkId;
      this.domain = domain;
      this.domainId = domainId;
      this.usesVirtualNetwork = usesVirtualNetwork;
      this.IPAddress = IPAddress;
      this.isSourceNAT = isSourceNAT;
      this.isStaticNAT = isStaticNAT;
      this.networkId = networkId;
      this.state = state;
      this.virtualMachineDisplayName = virtualMachineDisplayName;
      this.virtualMachineId = virtualMachineId;
      this.virtualMachineName = virtualMachineName;
      this.VLANId = VLANId;
      this.VLANName = VLANName;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
   }

   /**
    * @return public IP address id
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account the public IP address is associated with
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return date the public IP address was acquired
    */
   @Nullable
   public Date getAllocated() {
      return this.allocated;
   }

   /**
    * @return the ID of the Network associated with the IP address
    */
   @Nullable
   public String getAssociatedNetworkId() {
      return this.associatedNetworkId;
   }

   /**
    * @return the domain the public IP address is associated with
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the domain ID the public IP address is associated with
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return uses virtual network
    */
   public boolean isUsesVirtualNetwork() {
      return this.usesVirtualNetwork;
   }

   /**
    * @return public IP address
    */
   @Nullable
   public String getIPAddress() {
      return this.IPAddress;
   }

   /**
    * @return true if the IP address is a source nat address, false otherwise
    */
   public boolean isSourceNAT() {
      return this.isSourceNAT;
   }

   /**
    * @return true if this ip is for static nat, false otherwise
    */
   public boolean isStaticNAT() {
      return this.isStaticNAT;
   }

   /**
    * @return the ID of the Network where ip belongs to
    */
   @Nullable
   public String getNetworkId() {
      return this.networkId;
   }

   /**
    * @return State of the ip address. Can be: Allocating, Allocated and
    *         Releasing
    */
   @Nullable
   public PublicIPAddress.State getState() {
      return this.state;
   }

   /**
    * @return virtual machine display name the ip address is assigned to (not
    *         null only for static nat Ip)
    */
   @Nullable
   public String getVirtualMachineDisplayName() {
      return this.virtualMachineDisplayName;
   }

   /**
    * @return virtual machine id the ip address is assigned to (not null only
    *         for static nat Ip)
    */
   @Nullable
   public String getVirtualMachineId() {
      return this.virtualMachineId;
   }

   /**
    * @return virtual machine name the ip address is assigned to (not null only
    *         for static nat Ip)
    */
   @Nullable
   public String getVirtualMachineName() {
      return this.virtualMachineName;
   }

   /**
    * @return the ID of the VLAN associated with the IP address
    */
   @Nullable
   public String getVLANId() {
      return this.VLANId;
   }

   /**
    * @return the VLAN associated with the IP address
    */
   @Nullable
   public String getVLANName() {
      return this.VLANName;
   }

   /**
    * @return the ID of the zone the public IP address belongs to
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   /**
    * @return the name of the zone the public IP address belongs to
    */
   @Nullable
   public String getZoneName() {
      return this.zoneName;
   }

   /**
    * @return shows the current pending asynchronous job ID. This tag is not
    *         returned if no current pending jobs are acting on the virtual
    *         machine
    */
   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   /**
    * @return shows the current pending asynchronous job status
    */
   @Nullable
   public Integer getJobStatus() {
      return this.jobStatus;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, allocated, associatedNetworkId, domain, domainId, usesVirtualNetwork, IPAddress, isSourceNAT, isStaticNAT, networkId, state, virtualMachineDisplayName, virtualMachineId, virtualMachineName, VLANId, VLANName, zoneId, zoneName, jobId, jobStatus);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      PublicIPAddress that = PublicIPAddress.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.allocated, that.allocated)
            && Objects.equal(this.associatedNetworkId, that.associatedNetworkId)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.usesVirtualNetwork, that.usesVirtualNetwork)
            && Objects.equal(this.IPAddress, that.IPAddress)
            && Objects.equal(this.isSourceNAT, that.isSourceNAT)
            && Objects.equal(this.isStaticNAT, that.isStaticNAT)
            && Objects.equal(this.networkId, that.networkId)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.virtualMachineDisplayName, that.virtualMachineDisplayName)
            && Objects.equal(this.virtualMachineId, that.virtualMachineId)
            && Objects.equal(this.virtualMachineName, that.virtualMachineName)
            && Objects.equal(this.VLANId, that.VLANId)
            && Objects.equal(this.VLANName, that.VLANName)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("allocated", allocated).add("associatedNetworkId", associatedNetworkId)
            .add("domain", domain).add("domainId", domainId).add("usesVirtualNetwork", usesVirtualNetwork).add("IPAddress", IPAddress)
            .add("isSourceNAT", isSourceNAT).add("isStaticNAT", isStaticNAT).add("networkId", networkId).add("state", state)
            .add("virtualMachineDisplayName", virtualMachineDisplayName).add("virtualMachineId", virtualMachineId)
            .add("virtualMachineName", virtualMachineName).add("VLANId", VLANId).add("VLANName", VLANName).add("zoneId", zoneId)
            .add("zoneName", zoneName).add("jobId", jobId).add("jobStatus", jobStatus);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
