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
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class VirtualMachine implements Comparable<VirtualMachine> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String account;
      private long cpuCount;
      private long cpuSpeed;
      private String cpuUsed;
      private String displayName;
      private Date created;
      private String domain;
      private long domainId;
      private boolean usesVirtualNetwork;
      private String group;
      private long groupId;
      private long guestOSId;
      private boolean HAEnabled;
      private long hostId;
      private String hostname;
      private String IPAddress;
      private String ISODisplayText;
      private long ISOId;
      private String ISOName;
      private Long jobId;
      private Integer jobStatus;
      private long memory;
      private String name;
      private Long networkKbsRead;
      private Long networkKbsWrite;
      private String password;
      private boolean passwordEnabled;
      private long rootDeviceId;
      private String rootDeviceType;
      private long serviceOfferingId;
      private String serviceOfferingName;
      private State state;
      private String templateDisplayText;
      private long templateId;
      private String templateName;
      private long zoneId;
      private String zoneName;
      private Set<NIC> nics = ImmutableSet.<NIC> of();
      private String hypervisor;
      private Set<SecurityGroup> securityGroups = ImmutableSet.<SecurityGroup> of();

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder cpuCount(long cpuCount) {
         this.cpuCount = cpuCount;
         return this;
      }

      public Builder cpuSpeed(long cpuSpeed) {
         this.cpuSpeed = cpuSpeed;
         return this;
      }

      public Builder cpuUsed(String cpuUsed) {
         this.cpuUsed = cpuUsed;
         return this;
      }

      public Builder displayName(String displayName) {
         this.displayName = displayName;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
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

      public Builder group(String group) {
         this.group = group;
         return this;
      }

      public Builder groupId(long groupId) {
         this.groupId = groupId;
         return this;
      }

      public Builder guestOSId(long guestOSId) {
         this.guestOSId = guestOSId;
         return this;
      }

      public Builder isHAEnabled(boolean HAEnabled) {
         this.HAEnabled = HAEnabled;
         return this;
      }

      public Builder hostId(long hostId) {
         this.hostId = hostId;
         return this;
      }

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return this;
      }

      public Builder ISODisplayText(String ISODisplayText) {
         this.ISODisplayText = ISODisplayText;
         return this;
      }

      public Builder ISOId(long ISOId) {
         this.ISOId = ISOId;
         return this;
      }

      public Builder ISOName(String ISOName) {
         this.ISOName = ISOName;
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

      public Builder memory(long memory) {
         this.memory = memory;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder networkKbsRead(Long networkKbsRead) {
         this.networkKbsRead = networkKbsRead;
         return this;
      }

      public Builder networkKbsWrite(Long networkKbsWrite) {
         this.networkKbsWrite = networkKbsWrite;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Builder passwordEnabled(boolean passwordEnabled) {
         this.passwordEnabled = passwordEnabled;
         return this;
      }

      public Builder rootDeviceId(long rootDeviceId) {
         this.rootDeviceId = rootDeviceId;
         return this;
      }

      public Builder rootDeviceType(String rootDeviceType) {
         this.rootDeviceType = rootDeviceType;
         return this;
      }

      public Builder serviceOfferingId(long serviceOfferingId) {
         this.serviceOfferingId = serviceOfferingId;
         return this;
      }

      public Builder serviceOfferingName(String serviceOfferingName) {
         this.serviceOfferingName = serviceOfferingName;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder templateDisplayText(String templateDisplayText) {
         this.templateDisplayText = templateDisplayText;
         return this;
      }

      public Builder templateId(long templateId) {
         this.templateId = templateId;
         return this;
      }

      public Builder templateName(String templateName) {
         this.templateName = templateName;
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

      public Builder nics(Iterable<NIC> nics) {
         this.nics = ImmutableSet.<NIC> copyOf(checkNotNull(nics, "nics"));
         return this;
      }

      public Builder hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      public Builder securityGroups(Set<SecurityGroup> securityGroups) {
         this.securityGroups = ImmutableSet.<SecurityGroup> copyOf(checkNotNull(securityGroups, "securityGroups"));
         return this;
      }

      public VirtualMachine build() {
         return new VirtualMachine(id, account, cpuCount, cpuSpeed, cpuUsed, displayName, created, domain, domainId,
               usesVirtualNetwork, group, groupId, guestOSId, HAEnabled, hostId, hostname, IPAddress, ISODisplayText,
               ISOId, ISOName, jobId, jobStatus, memory, name, networkKbsRead, networkKbsWrite, password,
               passwordEnabled, rootDeviceId, rootDeviceType, securityGroups, serviceOfferingId, serviceOfferingName,
               state, templateDisplayText, templateId, templateName, zoneId, zoneName, nics, hypervisor);
      }
   }

   public static enum State {
      STARTING, RUNNING, STOPPING, STOPPED, DESTROYED, EXPUNGING, MIGRATING, ERROR, UNKNOWN, SHUTDOWNED, UNRECOGNIZED;
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

   private long id;
   private String account;
   @SerializedName("cpunumber")
   private long cpuCount;
   @SerializedName("cpuspeed")
   private long cpuSpeed;
   @SerializedName("cpuused")
   private String cpuUsed;
   @SerializedName("displayname")
   private String displayName;
   private Date created;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("forvirtualnetwork")
   private boolean usesVirtualNetwork;
   private String group;
   @SerializedName("groupid")
   private long groupId;
   @SerializedName("guestosid")
   private long guestOSId;
   @SerializedName("haenable")
   private boolean HAEnabled;
   @SerializedName("hostid")
   private long hostId;
   private String hostname;
   @SerializedName("ipaddress")
   private String IPAddress;
   @SerializedName("isodisplaytext")
   private String ISODisplayText;
   @SerializedName("isoid")
   private long ISOId;
   @SerializedName("isoname")
   private String ISOName;
   @SerializedName("jobid")
   @Nullable
   private Long jobId;
   @SerializedName("jobstatus")
   @Nullable
   private Integer jobStatus;
   private long memory;
   private String name;
   @SerializedName("networkkbsread")
   private Long networkKbsRead;
   @SerializedName("networkkbswrite")
   private Long networkKbsWrite;
   @Nullable
   private String password;
   @SerializedName("passwordenabled")
   private boolean passwordEnabled;
   @SerializedName("rootdeviceid")
   private long rootDeviceId;
   @SerializedName("rootdevicetype")
   private String rootDeviceType;
   @SerializedName("serviceofferingid")
   private long serviceOfferingId;
   @SerializedName("serviceofferingname")
   private String serviceOfferingName;
   private State state;
   @SerializedName("templatedisplaytext")
   private String templateDisplayText;
   @SerializedName("templateid")
   private long templateId;
   @SerializedName("templatename")
   private String templateName;
   @SerializedName("zoneid")
   private long zoneId;
   @SerializedName("zonename")
   private String zoneName;
   @SerializedName("nic")
   private Set<NIC> nics = ImmutableSet.<NIC> of();
   private String hypervisor;
   @SerializedName("securitygroup")
   private Set<SecurityGroup> securityGroups = ImmutableSet.<SecurityGroup> of();

   public VirtualMachine(long id, String account, long cpuCount, long cpuSpeed, String cpuUsed, String displayName,
         Date created, String domain, long domainId, boolean usesVirtualNetwork, String group, long groupId,
         long guestOSId, boolean hAEnabled, long hostId, String hostname, String iPAddress, String iSODisplayText,
         long iSOId, String iSOName, Long jobId, Integer jobStatus, long memory, String name, Long networkKbsRead,
         Long networkKbsWrite, String password, boolean passwordEnabled, long rootDeviceId, String rootDeviceType,
         Set<SecurityGroup> securityGroups, long serviceOfferingId, String serviceOfferingName, State state,
         String templateDisplayText, long templateId, String templateName, long zoneId, String zoneName, Set<NIC> nics,
         String hypervisor) {
      Preconditions.checkArgument(Strings.isNullOrEmpty(cpuUsed) || cpuUsed.matches("^[0-9\\.]+%$"), "cpuUsed value should be a decimal number followed by %");
      this.id = id;
      this.account = account;
      this.cpuCount = cpuCount;
      this.cpuSpeed = cpuSpeed;
      this.cpuUsed = cpuUsed != null ? cpuUsed + "" : null;
      this.displayName = displayName;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.usesVirtualNetwork = usesVirtualNetwork;
      this.group = group;
      this.groupId = groupId;
      this.guestOSId = guestOSId;
      this.HAEnabled = hAEnabled;
      this.hostId = hostId;
      this.hostname = hostname;
      this.IPAddress = iPAddress;
      this.ISODisplayText = iSODisplayText;
      this.ISOId = iSOId;
      this.ISOName = iSOName;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
      this.memory = memory;
      this.name = name;
      this.networkKbsRead = networkKbsRead;
      this.networkKbsWrite = networkKbsWrite;
      this.password = password;
      this.passwordEnabled = passwordEnabled;
      this.rootDeviceId = rootDeviceId;
      this.rootDeviceType = rootDeviceType;
      this.securityGroups = ImmutableSet.copyOf(checkNotNull(securityGroups, "securityGroups"));
      this.serviceOfferingId = serviceOfferingId;
      this.serviceOfferingName = serviceOfferingName;
      this.state = state;
      this.templateDisplayText = templateDisplayText;
      this.templateId = templateId;
      this.templateName = templateName;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
      this.nics = nics;
      this.hypervisor = hypervisor;
   }

   /**
    * present only for serializer
    * 
    */
   VirtualMachine() {
   }

   /**
    * @return the ID of the virtual machine
    */
   public long getId() {
      return id;
   }

   /**
    * @return the account associated with the virtual machine
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the number of cpu this virtual machine is running with
    */
   public long getCpuCount() {
      return cpuCount;
   }

   /**
    * @return the speed of each cpu
    */
   public long getCpuSpeed() {
      return cpuSpeed;
   }

   /**
    * @return the amount of the vm's CPU currently used
    */
   public float getCpuUsed() {
      return cpuUsed != null ? Float.parseFloat(cpuUsed.substring(0, cpuUsed.length() - 1)) : 0.0f;
   }

   /**
    * @return user generated name. The name of the virtual machine is returned
    *         if no displayname exists.
    */
   public String getDisplayName() {
      return displayName;
   }

   /**
    * @return the date when this virtual machine was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return the name of the domain in which the virtual machine exists
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the ID of the domain in which the virtual machine exists
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return the virtual network for the service offering
    */
   public boolean usesVirtualNetwork() {
      return usesVirtualNetwork;
   }

   /**
    * @return the group name of the virtual machine
    */
   public String getGroup() {
      return group;
   }

   /**
    * @return the group ID of the virtual machine
    */
   public long getGroupId() {
      return groupId;
   }

   /**
    * @return Os type ID of the virtual machine
    */
   public long getGuestOSId() {
      return guestOSId;
   }

   /**
    * @return true if high-availability is enabled, false otherwise
    */
   public boolean isHAEnabled() {
      return HAEnabled;
   }

   /**
    * @return the ID of the host for the virtual machine
    */
   public long getHostId() {
      return hostId;
   }

   /**
    * @return the name of the host for the virtual machine
    */
   public String getHostname() {
      return hostname;
   }

   /**
    * @return the ip address of the virtual machine
    */
   public String getIPAddress() {
      if (IPAddress != null)
         return IPAddress;
      // some versions of 2.2.0 do not populate the IP address field
      if (getNICs().size() > 0) {
         return Iterables.get(getNICs(), 0).getIPAddress();
      }
      return null;
   }

   /**
    * @return an alternate display text of the ISO attached to the virtual
    *         machine
    */
   public String getISODisplayText() {
      return ISODisplayText;
   }

   /**
    * @return the ID of the ISO attached to the virtual machine
    */
   public long getISOId() {
      return ISOId;
   }

   /**
    * @return the name of the ISO attached to the virtual machine
    */
   public String getISOName() {
      return ISOName;
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
    * @return the memory allocated for the virtual machine
    */
   public long getMemory() {
      return memory;
   }

   /**
    * @return the name of the virtual machine
    */
   public String getName() {
      return name;
   }

   /**
    * @return the incoming network traffic on the vm
    */
   public Long getNetworkKbsRead() {
      return networkKbsRead;
   }

   /**
    * @return the outgoing network traffic on the host
    */
   public Long getNetworkKbsWrite() {
      return networkKbsWrite;
   }

   /**
    * @return the password (if exists) of the virtual machine
    */
   @Nullable
   public String getPassword() {
      return password;
   }

   /**
    * @return true if the password rest feature is enabled, false otherwise
    */
   public boolean isPasswordEnabled() {
      return passwordEnabled;
   }

   /**
    * @return device ID of the root volume
    */
   public long getRootDeviceId() {
      return rootDeviceId;
   }

   /**
    * @return device type of the root volume
    */
   public String getRootDeviceType() {
      return rootDeviceType;
   }

   /**
    * @return list of security groups associated with the virtual machine
    */
   public Set<SecurityGroup> getSecurityGroups() {
      return securityGroups;
   }

   /**
    * @return the ID of the service offering of the virtual machine
    */
   public long getServiceOfferingId() {
      return serviceOfferingId;
   }

   /**
    * @return the name of the service offering of the virtual machine
    */
   public String getServiceOfferingName() {
      return serviceOfferingName;
   }

   /**
    * @return the state of the virtual machine
    */
   public State getState() {
      return state;
   }

   /**
    * @return an alternate display text of the template for the virtual machine
    */
   public String getTemplateDisplayText() {
      return templateDisplayText;
   }

   /**
    * @return the ID of the template for the virtual machine. A -1 is returned
    *         if the virtual machine was created from an ISO file.
    */
   public long getTemplateId() {
      return templateId;
   }

   /**
    * @return the name of the template for the virtual machine
    */
   public String getTemplateName() {
      return templateName;
   }

   /**
    * @return the ID of the availablility zone for the virtual machine
    */
   public long getZoneId() {
      return zoneId;
   }

   /**
    * @return the name of the availability zone for the virtual machine
    */
   public String getZoneName() {
      return zoneName;
   }

   /**
    * @return the list of nics associated with vm
    */
   public Set<NIC> getNICs() {
      return nics;
   }

   /**
    * @return type of the hypervisor
    */
   public String getHypervisor() {
      return hypervisor;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (HAEnabled ? 1231 : 1237);
      result = prime * result + ((IPAddress == null) ? 0 : IPAddress.hashCode());
      result = prime * result + ((ISODisplayText == null) ? 0 : ISODisplayText.hashCode());
      result = prime * result + (int) (ISOId ^ (ISOId >>> 32));
      result = prime * result + ((ISOName == null) ? 0 : ISOName.hashCode());
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + (int) (cpuCount ^ (cpuCount >>> 32));
      result = prime * result + (int) (cpuSpeed ^ (cpuSpeed >>> 32));
      result = prime * result + ((cpuUsed == null) ? 0 : cpuUsed.hashCode());
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + ((group == null) ? 0 : group.hashCode());
      result = prime * result + (int) (groupId ^ (groupId >>> 32));
      result = prime * result + (int) (guestOSId ^ (guestOSId >>> 32));
      result = prime * result + (int) (hostId ^ (hostId >>> 32));
      result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
      result = prime * result + ((hypervisor == null) ? 0 : hypervisor.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
      result = prime * result + ((jobStatus == null) ? 0 : jobStatus.hashCode());
      result = prime * result + (int) (memory ^ (memory >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((networkKbsRead == null) ? 0 : networkKbsRead.hashCode());
      result = prime * result + ((networkKbsWrite == null) ? 0 : networkKbsWrite.hashCode());
      result = prime * result + ((nics == null) ? 0 : nics.hashCode());
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + (passwordEnabled ? 1231 : 1237);
      result = prime * result + (int) (rootDeviceId ^ (rootDeviceId >>> 32));
      result = prime * result + ((securityGroups == null) ? 0 : securityGroups.hashCode());
      result = prime * result + (int) (serviceOfferingId ^ (serviceOfferingId >>> 32));
      result = prime * result + ((serviceOfferingName == null) ? 0 : serviceOfferingName.hashCode());
      result = prime * result + ((templateDisplayText == null) ? 0 : templateDisplayText.hashCode());
      result = prime * result + (int) (templateId ^ (templateId >>> 32));
      result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
      result = prime * result + (usesVirtualNetwork ? 1231 : 1237);
      result = prime * result + (int) (zoneId ^ (zoneId >>> 32));
      result = prime * result + ((zoneName == null) ? 0 : zoneName.hashCode());
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
      VirtualMachine other = (VirtualMachine) obj;
      if (HAEnabled != other.HAEnabled)
         return false;
      if (IPAddress == null) {
         if (other.IPAddress != null)
            return false;
      } else if (!IPAddress.equals(other.IPAddress))
         return false;
      if (ISODisplayText == null) {
         if (other.ISODisplayText != null)
            return false;
      } else if (!ISODisplayText.equals(other.ISODisplayText))
         return false;
      if (ISOId != other.ISOId)
         return false;
      if (ISOName == null) {
         if (other.ISOName != null)
            return false;
      } else if (!ISOName.equals(other.ISOName))
         return false;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (cpuCount != other.cpuCount)
         return false;
      if (cpuSpeed != other.cpuSpeed)
         return false;
      if (cpuUsed == null) {
         if (other.cpuUsed != null)
            return false;
      } else if (!cpuUsed.equals(other.cpuUsed))
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (displayName == null) {
         if (other.displayName != null)
            return false;
      } else if (!displayName.equals(other.displayName))
         return false;
      if (domain == null) {
         if (other.domain != null)
            return false;
      } else if (!domain.equals(other.domain))
         return false;
      if (domainId != other.domainId)
         return false;
      if (group == null) {
         if (other.group != null)
            return false;
      } else if (!group.equals(other.group))
         return false;
      if (groupId != other.groupId)
         return false;
      if (guestOSId != other.guestOSId)
         return false;
      if (hostId != other.hostId)
         return false;
      if (hostname == null) {
         if (other.hostname != null)
            return false;
      } else if (!hostname.equals(other.hostname))
         return false;
      if (hypervisor == null) {
         if (other.hypervisor != null)
            return false;
      } else if (!hypervisor.equals(other.hypervisor))
         return false;
      if (id != other.id)
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
      if (memory != other.memory)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (networkKbsRead == null) {
         if (other.networkKbsRead != null)
            return false;
      } else if (!networkKbsRead.equals(other.networkKbsRead))
         return false;
      if (networkKbsWrite == null) {
         if (other.networkKbsWrite != null)
            return false;
      } else if (!networkKbsWrite.equals(other.networkKbsWrite))
         return false;
      if (nics == null) {
         if (other.nics != null)
            return false;
      } else if (!nics.equals(other.nics))
         return false;
      if (password == null) {
         if (other.password != null)
            return false;
      } else if (!password.equals(other.password))
         return false;
      if (passwordEnabled != other.passwordEnabled)
         return false;
      if (rootDeviceId != other.rootDeviceId)
         return false;
      // rootDeviceType and state are volatile
      if (securityGroups == null) {
         if (other.securityGroups != null)
            return false;
      } else if (!securityGroups.equals(other.securityGroups))
         return false;
      if (serviceOfferingId != other.serviceOfferingId)
         return false;
      if (serviceOfferingName == null) {
         if (other.serviceOfferingName != null)
            return false;
      } else if (!serviceOfferingName.equals(other.serviceOfferingName))
         return false;
      if (templateDisplayText == null) {
         if (other.templateDisplayText != null)
            return false;
      } else if (!templateDisplayText.equals(other.templateDisplayText))
         return false;
      if (templateId != other.templateId)
         return false;
      if (templateName == null) {
         if (other.templateName != null)
            return false;
      } else if (!templateName.equals(other.templateName))
         return false;
      if (usesVirtualNetwork != other.usesVirtualNetwork)
         return false;
      if (zoneId != other.zoneId)
         return false;
      if (zoneName == null) {
         if (other.zoneName != null)
            return false;
      } else if (!zoneName.equals(other.zoneName))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "VirtualMachine{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", cpuCount=" + cpuCount +
            ", cpuSpeed=" + cpuSpeed +
            ", cpuUsed='" + cpuUsed + '\'' +
            ", displayName='" + displayName + '\'' +
            ", created=" + created +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", usesVirtualNetwork=" + usesVirtualNetwork +
            ", group='" + group + '\'' +
            ", groupId=" + groupId +
            ", guestOSId=" + guestOSId +
            ", HAEnabled=" + HAEnabled +
            ", hostId=" + hostId +
            ", hostname='" + hostname + '\'' +
            ", IPAddress='" + IPAddress + '\'' +
            ", ISODisplayText='" + ISODisplayText + '\'' +
            ", ISOId=" + ISOId +
            ", ISOName='" + ISOName + '\'' +
            ", jobId=" + jobId +
            ", jobStatus=" + jobStatus +
            ", memory=" + memory +
            ", name='" + name + '\'' +
            ", networkKbsRead=" + networkKbsRead +
            ", networkKbsWrite=" + networkKbsWrite +
            ", password='" + password + '\'' +
            ", passwordEnabled=" + passwordEnabled +
            ", rootDeviceId=" + rootDeviceId +
            ", rootDeviceType='" + rootDeviceType + '\'' +
            ", serviceOfferingId=" + serviceOfferingId +
            ", serviceOfferingName='" + serviceOfferingName + '\'' +
            ", state=" + state +
            ", templateDisplayText='" + templateDisplayText + '\'' +
            ", templateId=" + templateId +
            ", templateName='" + templateName + '\'' +
            ", zoneId=" + zoneId +
            ", zoneName='" + zoneName + '\'' +
            ", nics=" + nics +
            ", hypervisor='" + hypervisor + '\'' +
            ", securityGroups=" + securityGroups +
            '}';
   }

   @Override
   public int compareTo(VirtualMachine arg0) {
      return new Long(id).compareTo(arg0.getId());
   }
}
