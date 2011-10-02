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
package org.jclouds.softlayer.domain;

import com.google.common.base.CaseFormat;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

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
 * Each label cannot be soley comprised of digits and must be between 1-63 characters in length.
 * The last label, the TLD (top level domain) must be between 2-6 alphabetic characters.
 * The domain portion must consist of least one label followed by a period '.' then ending with the TLD label.
 * Combining the hostname, followed by a period '.', followed by the domain gives the FQDN (fully qualified domain name),
 * which may not exceed 253 characters in total length.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest"
 *      />
 */
public class VirtualGuest implements Comparable<VirtualGuest> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int id = -1;
      private int accountId = -1;
      private Date createDate;
      private boolean dedicatedAccountHostOnly;
      private String hostname;
      private String domain;
      private String fullyQualifiedDomainName;
      private Date lastVerifiedDate;
      private int maxCpu = -1;
      private String maxCpuUnits;
      private int maxMemory = -1;
      private Date metricPollDate;
      private Date modifyDate;
      private String notes;
      private boolean privateNetworkOnly;
      private int startCpus = -1;
      private int statusId = -1;
      private String uuid;
      private String primaryBackendIpAddress;
      private String primaryIpAddress;
      private BillingItemVirtualGuest billingItem;
      private OperatingSystem operatingSystem;
      private Datacenter datacenter;
      private PowerState powerState;

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder accountId(int accountId) {
         this.accountId = accountId;
         return this;
      }

      public Builder createDate(Date createDate) {
         this.createDate = createDate;
         return this;
      }

      public Builder dedicatedAccountHostOnly(boolean dedicatedAccountHostOnly) {
         this.dedicatedAccountHostOnly = dedicatedAccountHostOnly;
         return this;
      }

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder fullyQualifiedDomainName(String fullyQualifiedDomainName) {
         this.fullyQualifiedDomainName = fullyQualifiedDomainName;
         return this;
      }

      public Builder lastVerifiedDate(Date lastVerifiedDate) {
         this.lastVerifiedDate = lastVerifiedDate;
         return this;
      }

      public Builder maxCpu(int maxCpu) {
         this.maxCpu = maxCpu;
         return this;
      }

      public Builder maxCpuUnits(String maxCpuUnits) {
         this.maxCpuUnits = maxCpuUnits;
         return this;
      }

      public Builder maxMemory(int maxMemory) {
         this.maxMemory = maxMemory;
         return this;
      }

      public Builder metricPollDate(Date metricPollDate) {
         this.metricPollDate = metricPollDate;
         return this;
      }

      public Builder modifyDate(Date modifyDate) {
         this.modifyDate = modifyDate;
         return this;
      }

      public Builder notes(String notes) {
         this.notes = notes;
         return this;
      }

      public Builder privateNetworkOnly(boolean privateNetworkOnly) {
         this.privateNetworkOnly = privateNetworkOnly;
         return this;
      }

      public Builder startCpus(int startCpus) {
         this.startCpus = startCpus;
         return this;
      }

      public Builder statusId(int statusId) {
         this.statusId = statusId;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder primaryBackendIpAddress(String primaryBackendIpAddress) {
         this.primaryBackendIpAddress = primaryBackendIpAddress;
         return this;
      }

      public Builder primaryIpAddress(String primaryIpAddress) {
         this.primaryIpAddress = primaryIpAddress;
         return this;
      }

      public Builder billingItem(BillingItemVirtualGuest billingItem) {
         this.billingItem = billingItem;
         return this;
      }

      public Builder operatingSystem(OperatingSystem operatingSystem) {
         this.operatingSystem = operatingSystem;
         return this;
      }

      public Builder datacenter(Datacenter datacenter) {
         this.datacenter = datacenter;
         return this;
      }

      public Builder powerState(PowerState powerState) {
         this.powerState = powerState;
         return this;
      }

      public VirtualGuest build() {
         return new VirtualGuest(accountId, createDate, dedicatedAccountHostOnly, domain,
            fullyQualifiedDomainName, hostname, id, lastVerifiedDate, maxCpu,
            maxCpuUnits, maxMemory, metricPollDate, modifyDate, notes,
            privateNetworkOnly, startCpus, statusId, uuid, primaryBackendIpAddress,
            primaryIpAddress,billingItem,operatingSystem,datacenter,powerState);
      }

      public static Builder fromVirtualGuest(VirtualGuest in) {
         return VirtualGuest.builder()
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
                                 .billingItem(in.getBillingItem())
                                 .operatingSystem(in.getOperatingSystem())
                                 .datacenter(in.getDatacenter())
                                 .powerState(in.getPowerState());
      }
   }

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

   private int accountId = -1;
   private Date createDate;
   @SerializedName("dedicatedAccountHostOnlyFlag")
   private boolean dedicatedAccountHostOnly;
   private String domain;
   private String fullyQualifiedDomainName;
   private String hostname;
   private int id = -1;
   private Date lastVerifiedDate;
   private int maxCpu = -1;
   private String maxCpuUnits;
   private int maxMemory = -1;
   private Date metricPollDate;
   private Date modifyDate;
   private String notes;
   @SerializedName("privateNetworkOnlyFlag")
   private boolean privateNetworkOnly;
   private int startCpus = -1;
   private int statusId = -1;
   private String uuid;
   private String primaryBackendIpAddress;
   private String primaryIpAddress;

   private BillingItemVirtualGuest billingItem;
   private OperatingSystem operatingSystem;
   private Datacenter datacenter;
   private PowerState powerState;
   
   // for deserializer
   VirtualGuest() {

   }

   public VirtualGuest(int accountId, Date createDate, boolean dedicatedAccountHostOnly, String domain,
            String fullyQualifiedDomainName, String hostname, int id, Date lastVerifiedDate, int maxCpu,
            String maxCpuUnits, int maxMemory, Date metricPollDate, Date modifyDate, String notes,
            boolean privateNetworkOnly, int startCpus, int statusId, String uuid, String primaryBackendIpAddress,
            String primaryIpAddress,BillingItemVirtualGuest billingItem, OperatingSystem operatingSystem, Datacenter datacenter, PowerState powerState) {
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
      this.billingItem = billingItem;
      this.operatingSystem = operatingSystem;
      this.datacenter = datacenter;
      this.powerState = powerState;
   }

   @Override
   public int compareTo(VirtualGuest arg0) {
      return new Integer(id).compareTo(arg0.getId());
   }

   /**
    * @return A computing instance's associated account id
    */
   public int getAccountId() {
      return accountId;
   }

   /**
    * @return The date a virtual computing instance was created.
    */
   public Date getCreateDate() {
      return createDate;
   }

   /**
    * @return When true this flag specifies that a compute instance is to run on hosts that only
    *         have guests from the same account.
    */
   public boolean isDedicatedAccountHostOnly() {
      return dedicatedAccountHostOnly;
   }

   /**
    * @return A computing instance's domain name
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return A name reflecting the hostname and domain of the computing instance.
    */
   public String getFullyQualifiedDomainName() {
      return fullyQualifiedDomainName;
   }

   /**
    * @return A virtual computing instance's hostname
    */
   public String getHostname() {
      return hostname;
   }

   /**
    * @return Unique ID for a computing instance.
    */
   public int getId() {
      return id;
   }

   /**
    * @return The last timestamp of when the guest was verified as a resident virtual machine on the
    *         host's hypervisor platform.
    */

   public Date getLastVerifiedDate() {
      return lastVerifiedDate;
   }

   /**
    * @return The maximum amount of CPU resources a computing instance may utilize.
    */
   public int getMaxCpu() {
      return maxCpu;
   }

   /**
    * @return The unit of the maximum amount of CPU resources a computing instance may utilize.
    */
   public String getMaxCpuUnits() {
      return maxCpuUnits;
   }

   /**
    * @return The maximum amount of memory a computing instance may utilize.
    */
   public int getMaxMemory() {
      return maxMemory;
   }

   /**
    * @return The date of the most recent metric tracking poll performed.
    */
   public Date getMetricPollDate() {
      return metricPollDate;
   }

   /**
    * @return The date a virtual computing instance was last modified.
    */
   public Date getModifyDate() {
      return modifyDate;
   }

   /**
    * @return A small note about a cloud instance to use at your discretion.
    */
   public String getNotes() {
      return notes;
   }

   /**
    * @return Whether the computing instance only has access to the private network.
    */
   public boolean isPrivateNetworkOnly() {
      return privateNetworkOnly;
   }

   /**
    * @return The number of CPUs available to a computing instance upon startup.
    */
   public int getStartCpus() {
      return startCpus;
   }

   /**
    * @return A computing instances status ID
    */
   public int getStatusId() {
      return statusId;
   }

   /**
    * @return Unique ID for a computing instance's record on a virtualization platform.
    */
   public String getUuid() {
      return uuid;
   }

   /**
    * @return private ip address
    */
   public String getPrimaryBackendIpAddress() {
      return primaryBackendIpAddress;
   }

   /**
    * @return public ip address
    */
   public String getPrimaryIpAddress() {
      return primaryIpAddress;
   }

   /**
    * @return The billing item for a CloudLayer Compute Instance.
    */
   public BillingItemVirtualGuest getBillingItem() {
      return billingItem;
   }

   /**
    * @return A guest's operating system.
    */
   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
   }

   /**
    * @return The guest's datacenter
    */
   public Datacenter getDatacenter() {
      return datacenter;
   }

   /**
    * @return The current power state of a virtual guest.
    */
   public PowerState getPowerState() {
      return powerState;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (accountId ^ (accountId >>> 32));
      result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
      result = prime * result + (dedicatedAccountHostOnly ? 1231 : 1237);
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + ((fullyQualifiedDomainName == null) ? 0 : fullyQualifiedDomainName.hashCode());
      result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
      result = prime * result + (id ^ (id >>> 32));
      result = prime * result + ((lastVerifiedDate == null) ? 0 : lastVerifiedDate.hashCode());
      result = prime * result + maxCpu;
      result = prime * result + ((maxCpuUnits == null) ? 0 : maxCpuUnits.hashCode());
      result = prime * result + maxMemory;
      result = prime * result + ((metricPollDate == null) ? 0 : metricPollDate.hashCode());
      result = prime * result + ((modifyDate == null) ? 0 : modifyDate.hashCode());
      result = prime * result + ((notes == null) ? 0 : notes.hashCode());
      result = prime * result + ((primaryBackendIpAddress == null) ? 0 : primaryBackendIpAddress.hashCode());
      result = prime * result + ((primaryIpAddress == null) ? 0 : primaryIpAddress.hashCode());
      result = prime * result + (privateNetworkOnly ? 1231 : 1237);
      result = prime * result + startCpus;
      result = prime * result + statusId;
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      result = prime * result + ((billingItem == null) ? 0 : billingItem.hashCode());
      result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
      result = prime * result + ((datacenter == null) ? 0 : datacenter.hashCode());
      result = prime * result + ((powerState == null) ? 0 : powerState.hashCode());
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
      VirtualGuest other = (VirtualGuest) obj;
      if (accountId != other.accountId)
         return false;
      if (createDate == null) {
         if (other.createDate != null)
            return false;
      } else if (!createDate.equals(other.createDate))
         return false;
      if (dedicatedAccountHostOnly != other.dedicatedAccountHostOnly)
         return false;
      if (domain == null) {
         if (other.domain != null)
            return false;
      } else if (!domain.equals(other.domain))
         return false;
      if (fullyQualifiedDomainName == null) {
         if (other.fullyQualifiedDomainName != null)
            return false;
      } else if (!fullyQualifiedDomainName.equals(other.fullyQualifiedDomainName))
         return false;
      if (hostname == null) {
         if (other.hostname != null)
            return false;
      } else if (!hostname.equals(other.hostname))
         return false;
      if (id != other.id)
         return false;
      if (lastVerifiedDate == null) {
         if (other.lastVerifiedDate != null)
            return false;
      } else if (!lastVerifiedDate.equals(other.lastVerifiedDate))
         return false;
      if (maxCpu != other.maxCpu)
         return false;
      if (maxCpuUnits == null) {
         if (other.maxCpuUnits != null)
            return false;
      } else if (!maxCpuUnits.equals(other.maxCpuUnits))
         return false;
      if (maxMemory != other.maxMemory)
         return false;
      if (metricPollDate == null) {
         if (other.metricPollDate != null)
            return false;
      } else if (!metricPollDate.equals(other.metricPollDate))
         return false;
      if (modifyDate == null) {
         if (other.modifyDate != null)
            return false;
      } else if (!modifyDate.equals(other.modifyDate))
         return false;
      if (notes == null) {
         if (other.notes != null)
            return false;
      } else if (!notes.equals(other.notes))
         return false;
      if (primaryBackendIpAddress == null) {
         if (other.primaryBackendIpAddress != null)
            return false;
      } else if (!primaryBackendIpAddress.equals(other.primaryBackendIpAddress))
         return false;
      if (primaryIpAddress == null) {
         if (other.primaryIpAddress != null)
            return false;
      } else if (!primaryIpAddress.equals(other.primaryIpAddress))
         return false;
      if (privateNetworkOnly != other.privateNetworkOnly)
         return false;
      if (startCpus != other.startCpus)
         return false;
      if (statusId != other.statusId)
         return false;
      if (uuid == null) {
         if (other.uuid != null)
            return false;
      } else if (!uuid.equals(other.uuid))
         return false;
      if (billingItem == null) {
         if (other.billingItem != null)
            return false;
      } else if (!billingItem.equals(other.billingItem))
         return false;
      if (operatingSystem == null) {
         if (other.operatingSystem != null)
            return false;
      } else if (!operatingSystem.equals(other.operatingSystem))
         return false;
      if (datacenter == null) {
         if (other.datacenter != null)
            return false;
      } else if (!datacenter.equals(other.datacenter))
         return false;
      if (powerState == null) {
         if (other.powerState != null)
            return false;
      } else if (!powerState.equals(other.powerState))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[accountId=" + accountId + ", createDate=" + createDate + ", dedicatedAccountHostOnly="
               + dedicatedAccountHostOnly + ", domain=" + domain + ", fullyQualifiedDomainName="
               + fullyQualifiedDomainName + ", hostname=" + hostname + ", id=" + id + ", lastVerifiedDate="
               + lastVerifiedDate + ", maxCpu=" + maxCpu + ", maxCpuUnits=" + maxCpuUnits + ", maxMemory=" + maxMemory
               + ", metricPollDate=" + metricPollDate + ", modifyDate=" + modifyDate + ", notes=" + notes
               + ", primaryBackendIpAddress=" + primaryBackendIpAddress + ", primaryIpAddress=" + primaryIpAddress
               + ", privateNetworkOnly=" + privateNetworkOnly + ", startCpus=" + startCpus + ", statusId=" + statusId
               + ", uuid=" + uuid + ", billingItem="+billingItem+", operatingSystem="+operatingSystem+", datacenter="+datacenter
               + ", powerState="+powerState+"]";
   }

}
