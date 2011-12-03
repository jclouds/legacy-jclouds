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

import javax.annotation.Nullable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.CaseFormat;
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((IPAddress == null) ? 0 : IPAddress.hashCode());
      result = prime * result + (int) (VLANId ^ (VLANId >>> 32));
      result = prime * result + ((VLANName == null) ? 0 : VLANName.hashCode());
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((allocated == null) ? 0 : allocated.hashCode());
      result = prime * result + (int) (associatedNetworkId ^ (associatedNetworkId >>> 32));
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (isSourceNAT ? 1231 : 1237);
      result = prime * result + (isStaticNAT ? 1231 : 1237);
      result = prime * result + (int) (networkId ^ (networkId >>> 32));
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + (usesVirtualNetwork ? 1231 : 1237);
      result = prime * result + ((virtualMachineDisplayName == null) ? 0 : virtualMachineDisplayName.hashCode());
      result = prime * result + (int) (virtualMachineId ^ (virtualMachineId >>> 32));
      result = prime * result + ((virtualMachineName == null) ? 0 : virtualMachineName.hashCode());
      result = prime * result + (int) (zoneId ^ (zoneId >>> 32));
      result = prime * result + ((zoneName == null) ? 0 : zoneName.hashCode());
      result = prime * result + ((jobStatus == null) ? 0 : jobStatus.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PublicIPAddress other = (PublicIPAddress) obj;
      if (IPAddress == null) {
         if (other.IPAddress != null)
            return false;
      } else if (!IPAddress.equals(other.IPAddress))
         return false;
      if (VLANId != other.VLANId)
         return false;
      if (VLANName == null) {
         if (other.VLANName != null)
            return false;
      } else if (!VLANName.equals(other.VLANName))
         return false;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (allocated == null) {
         if (other.allocated != null)
            return false;
      } else if (!allocated.equals(other.allocated))
         return false;
      if (associatedNetworkId != other.associatedNetworkId)
         return false;
      if (domain == null) {
         if (other.domain != null)
            return false;
      } else if (!domain.equals(other.domain))
         return false;
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      if (isSourceNAT != other.isSourceNAT)
         return false;
      if (isStaticNAT != other.isStaticNAT)
         return false;
      if (networkId != other.networkId)
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (usesVirtualNetwork != other.usesVirtualNetwork)
         return false;
      if (virtualMachineDisplayName == null) {
         if (other.virtualMachineDisplayName != null)
            return false;
      } else if (!virtualMachineDisplayName.equals(other.virtualMachineDisplayName))
         return false;
      if (virtualMachineId != other.virtualMachineId)
         return false;
      if (virtualMachineName == null) {
         if (other.virtualMachineName != null)
            return false;
      } else if (!virtualMachineName.equals(other.virtualMachineName))
         return false;
      if (zoneId != other.zoneId)
         return false;
      if (zoneName == null) {
         if (other.zoneName != null)
            return false;
      } else if (!zoneName.equals(other.zoneName))
         return false;
      if (jobId == null) {
         if (other.jobId != null)
            return false;
      } else if (!jobId.equals(other.jobId))
         return false;
      if (jobStatus == null) {
         if (other.jobStatus != null)
            return false;
      } else if (!jobStatus.equals(other.jobStatus))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", IPAddress=" + IPAddress + ", VLANId=" + VLANId + ", VLANName=" + VLANName + ", account="
            + account + ", allocated=" + allocated + ", associatedNetworkId=" + associatedNetworkId + ", domain="
            + domain + ", domainId=" + domainId + ", usesVirtualNetwork=" + usesVirtualNetwork + ", isSourceNAT="
            + isSourceNAT + ", isStaticNAT=" + isStaticNAT + ", networkId=" + networkId + ", state=" + state
            + ", virtualMachineDisplayName=" + virtualMachineDisplayName + ", virtualMachineId=" + virtualMachineId
            + ", virtualMachineName=" + virtualMachineName + ", zoneId=" + zoneId + ", zoneName=" + zoneName + "]";
   }

}
