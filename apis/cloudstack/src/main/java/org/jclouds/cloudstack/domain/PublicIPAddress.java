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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class PublicIPAddress implements Comparable<PublicIPAddress> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String account;
      private Date allocated;
      private long associatedNetworkId;
      private String domain;
      private long domainId;
      private boolean usesVirtualNetwork;
      private String IPAddress;
      private boolean isSourceNAT;
      private boolean isStaticNAT;
      private long networkId;
      private State state;
      private String virtualMachineDisplayName;
      private long virtualMachineId = -1;
      private String virtualMachineName;
      private long VLANId;
      private String VLANName;
      private long zoneId;
      private String zoneName;
      private Long jobId;
      private Integer jobStatus;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder allocated(Date allocated) {
         this.allocated = allocated;
         return this;
      }

      public Builder associatedNetworkId(long associatedNetworkId) {
         this.associatedNetworkId = associatedNetworkId;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder usesVirtualNetwork(boolean usesVirtualNetwork) {
         this.usesVirtualNetwork = usesVirtualNetwork;
         return this;
      }

      public Builder IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return this;
      }

      public Builder isSourceNAT(boolean isSourceNAT) {
         this.isSourceNAT = isSourceNAT;
         return this;
      }

      public Builder isStaticNAT(boolean isStaticNAT) {
         this.isStaticNAT = isStaticNAT;
         return this;
      }

      public Builder networkId(long networkId) {
         this.networkId = networkId;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder virtualMachineDisplayName(String virtualMachineDisplayName) {
         this.virtualMachineDisplayName = virtualMachineDisplayName;
         return this;
      }

      public Builder virtualMachineId(long virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return this;
      }

      public Builder virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return this;
      }

      public Builder VLANId(long VLANId) {
         this.VLANId = VLANId;
         return this;
      }

      public Builder VLANName(String VLANName) {
         this.VLANName = VLANName;
         return this;
      }

      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

      public Builder jobId(Long jobId) {
         this.jobId = jobId;
         return this;
      }

      public Builder jobStatus(int jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      public PublicIPAddress build() {
         return new PublicIPAddress(id, account, allocated, associatedNetworkId, domain, domainId, usesVirtualNetwork,
               IPAddress, isSourceNAT, isStaticNAT, networkId, state, virtualMachineDisplayName, virtualMachineId,
               virtualMachineName, VLANId, VLANName, zoneId, zoneName, jobId, jobStatus);
      }
   }

   private long id;
   private String account;
   private Date allocated;
   @SerializedName("associatednetworkid")
   private long associatedNetworkId;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("forvirtualnetwork")
   private boolean usesVirtualNetwork;
   @SerializedName("ipaddress")
   private String IPAddress;
   @SerializedName("issourcenat")
   private boolean isSourceNAT;
   @SerializedName("isstaticnat")
   private boolean isStaticNAT;
   @SerializedName("networkid")
   private long networkId;
   private State state;
   @SerializedName("virtualmachinedisplayname")
   private String virtualMachineDisplayName;
   @SerializedName("virtualmachineid")
   private long virtualMachineId = -1;
   @SerializedName("virtualmachinename")
   private String virtualMachineName;
   @SerializedName("VLANid")
   private long VLANId;
   @SerializedName("VLANname")
   private String VLANName;
   @SerializedName("zoneid")
   private long zoneId;
   @SerializedName("zonename")
   private String zoneName;
   @SerializedName("jobid")
   @Nullable
   private Long jobId;
   @SerializedName("jobstatus")
   @Nullable
   private Integer jobStatus;

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

   // for serialization
   PublicIPAddress() {

   }

   public PublicIPAddress(long id, String account, Date allocated, long associatedNetworkId, String domain,
                          long domainId, boolean usesVirtualNetwork, String iPAddress, boolean isSourceNAT, boolean isStaticNAT,
                          long networkId, State state, String virtualMachineDisplayName, long virtualMachineId,
                          String virtualMachineName, long VLANId, String VLANName, long zoneId, String zoneName, Long jobId, 
                          Integer jobStatus) {
      this.id = id;
      this.account = account;
      this.allocated = allocated;
      this.associatedNetworkId = associatedNetworkId;
      this.domain = domain;
      this.domainId = domainId;
      this.usesVirtualNetwork = usesVirtualNetwork;
      this.IPAddress = iPAddress;
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

   @Override
   public int compareTo(PublicIPAddress arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return public IP address id
    */
   public long getId() {
      return id;
   }

   /**
    * @return the account the public IP address is associated with
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return date the public IP address was acquired
    */
   public Date getAllocated() {
      return allocated;
   }

   /**
    * @return shows the current pending asynchronous job ID. This tag is not
    *         returned if no current pending jobs are acting on the virtual
    *         machine
    */
   @Nullable
   public Long getJobId() {
      return jobId;
   }

   /**
    * @return shows the current pending asynchronous job status
    */
   @Nullable
   public Integer getJobStatus() {
      return jobStatus;
   }


   /**
    * @return the ID of the Network associated with the IP address
    */
   public long getAssociatedNetworkId() {
      return associatedNetworkId;
   }

   /**
    * @return the domain the public IP address is associated with
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the domain ID the public IP address is associated with
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return uses virtual network
    */
   public boolean usesVirtualNetwork() {
      return usesVirtualNetwork;
   }

   /**
    * @return public IP address
    */
   public String getIPAddress() {
      return IPAddress;
   }

   /**
    * @return true if the IP address is a source nat address, false otherwise
    */
   public boolean isSourceNAT() {
      return isSourceNAT;
   }

   /**
    * @return true if this ip is for static nat, false otherwise
    */
   public boolean isStaticNAT() {
      return isStaticNAT;
   }

   /**
    * @return the ID of the Network where ip belongs to
    */
   public long getNetworkId() {
      return networkId;
   }

   /**
    * @return State of the ip address. Can be: Allocating, Allocated and
    *         Releasing
    */
   public State getState() {
      return state;
   }

   /**
    * @return virtual machine display name the ip address is assigned to (not
    *         null only for static nat Ip)
    */
   public String getVirtualMachineDisplayName() {
      return virtualMachineDisplayName;
   }

   /**
    * @return virtual machine id the ip address is assigned to (not null only
    *         for static nat Ip)
    */
   public long getVirtualMachineId() {
      return virtualMachineId;
   }

   /**
    * @return virtual machine name the ip address is assigned to (not null only
    *         for static nat Ip)
    */
   public String getVirtualMachineName() {
      return virtualMachineName;
   }

   /**
    * @return the ID of the VLAN associated with the IP address
    */
   public long getVLANId() {
      return VLANId;
   }

   /**
    * @return the VLAN associated with the IP address
    */
   public String getVLANName() {
      return VLANName;
   }

   /**
    * @return the ID of the zone the public IP address belongs to
    */
   public long getZoneId() {
      return zoneId;
   }

   /**
    * @return the name of the zone the public IP address belongs to
    */
   public String getZoneName() {
      return zoneName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PublicIPAddress that = (PublicIPAddress) o;

      if (!Objects.equal(IPAddress, that.IPAddress)) return false;
      if (!Objects.equal(VLANId, that.VLANId)) return false;
      if (!Objects.equal(VLANName, that.VLANName)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(allocated, that.allocated)) return false;
      if (!Objects.equal(associatedNetworkId, that.associatedNetworkId)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(isSourceNAT, that.isSourceNAT)) return false;
      if (!Objects.equal(isStaticNAT, that.isStaticNAT)) return false;
      if (!Objects.equal(networkId, that.networkId)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(usesVirtualNetwork, that.usesVirtualNetwork)) return false;
      if (!Objects.equal(virtualMachineDisplayName, that.virtualMachineDisplayName)) return false;
      if (!Objects.equal(virtualMachineId, that.virtualMachineId)) return false;
      if (!Objects.equal(virtualMachineName, that.virtualMachineName)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(zoneName, that.zoneName)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(IPAddress, VLANId, VLANName, account, allocated, associatedNetworkId,
                               domain, domainId, id, isSourceNAT, isStaticNAT, networkId, state,
                               usesVirtualNetwork, virtualMachineDisplayName, virtualMachineId,
                               virtualMachineName, zoneId, zoneName, jobStatus);
   }

   @Override
   public String toString() {
      return "PublicIPAddress{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", allocated=" + allocated +
            ", associatedNetworkId=" + associatedNetworkId +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", usesVirtualNetwork=" + usesVirtualNetwork +
            ", IPAddress='" + IPAddress + '\'' +
            ", isSourceNAT=" + isSourceNAT +
            ", isStaticNAT=" + isStaticNAT +
            ", networkId=" + networkId +
            ", state=" + state +
            ", virtualMachineDisplayName='" + virtualMachineDisplayName + '\'' +
            ", virtualMachineId=" + virtualMachineId +
            ", virtualMachineName='" + virtualMachineName + '\'' +
            ", VLANId=" + VLANId +
            ", VLANName='" + VLANName + '\'' +
            ", zoneId=" + zoneId +
            ", zoneName='" + zoneName + '\'' +
            ", jobId=" + jobId +
            ", jobStatus=" + jobStatus +
            '}';
   }

}
