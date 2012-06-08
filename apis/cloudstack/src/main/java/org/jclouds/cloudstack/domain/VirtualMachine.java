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
import com.google.common.base.Objects;
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
      private String id;
      private String account;
      private long cpuCount;
      private long cpuSpeed;
      private String cpuUsed;
      private String displayName;
      private Date created;
      private String domain;
      private String domainId;
      private boolean usesVirtualNetwork;
      private String group;
      private String groupId;
      private String guestOSId;
      private boolean HAEnabled;
      private String hostId;
      private String hostname;
      private String IPAddress;
      private String ISODisplayText;
      private String ISOId;
      private String ISOName;
      private String jobId;
      private Integer jobStatus;
      private long memory;
      private String name;
      private Long networkKbsRead;
      private Long networkKbsWrite;
      private String password;
      private boolean passwordEnabled;
      private String publicIP;
      private String publicIPId;
      private String rootDeviceId;
      private String rootDeviceType;
      private String serviceOfferingId;
      private String serviceOfferingName;
      private State state;
      private String templateDisplayText;
      private String templateId;
      private String templateName;
      private String zoneId;
      private String zoneName;
      private Set<NIC> nics = ImmutableSet.<NIC> of();
      private String hypervisor;
      private Set<SecurityGroup> securityGroups = ImmutableSet.<SecurityGroup> of();

      public Builder id(String id) {
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

      public Builder domainId(String domainId) {
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

      public Builder groupId(String groupId) {
         this.groupId = groupId;
         return this;
      }

      public Builder guestOSId(String guestOSId) {
         this.guestOSId = guestOSId;
         return this;
      }

      public Builder isHAEnabled(boolean HAEnabled) {
         this.HAEnabled = HAEnabled;
         return this;
      }

      public Builder hostId(String hostId) {
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

      public Builder ISOId(String ISOId) {
         this.ISOId = ISOId;
         return this;
      }

      public Builder ISOName(String ISOName) {
         this.ISOName = ISOName;
         return this;
      }

      public Builder jobId(String jobId) {
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

      public Builder publicIP(String publicIP) {
         this.publicIP = publicIP;
         return this;
      }

      public Builder publicIPId(String publicIPId) {
         this.publicIPId = publicIPId;
         return this;
      }

      public Builder rootDeviceId(String rootDeviceId) {
         this.rootDeviceId = rootDeviceId;
         return this;
      }

      public Builder rootDeviceType(String rootDeviceType) {
         this.rootDeviceType = rootDeviceType;
         return this;
      }

      public Builder serviceOfferingId(String serviceOfferingId) {
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

      public Builder templateId(String templateId) {
         this.templateId = templateId;
         return this;
      }

      public Builder templateName(String templateName) {
         this.templateName = templateName;
         return this;
      }

      public Builder zoneId(String zoneId) {
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
                                   passwordEnabled, publicIP, publicIPId, rootDeviceId, rootDeviceType, securityGroups, serviceOfferingId, serviceOfferingName,
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

   private String id;
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
   private String domainId;
   @SerializedName("forvirtualnetwork")
   private boolean usesVirtualNetwork;
   private String group;
   @SerializedName("groupid")
   private String groupId;
   @SerializedName("guestosid")
   private String guestOSId;
   @SerializedName("haenable")
   private boolean HAEnabled;
   @SerializedName("hostid")
   private String hostId;
   private String hostname;
   @SerializedName("ipaddress")
   private String IPAddress;
   @SerializedName("isodisplaytext")
   private String ISODisplayText;
   @SerializedName("isoid")
   private String ISOId;
   @SerializedName("isoname")
   private String ISOName;
   @SerializedName("jobid")
   @Nullable
   private String jobId;
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
   @SerializedName("publicip")
   private String publicIP;
   @SerializedName("publicipid")
   private String publicIPId;
   @SerializedName("rootdeviceid")
   private String rootDeviceId;
   @SerializedName("rootdevicetype")
   private String rootDeviceType;
   @SerializedName("serviceofferingid")
   private String serviceOfferingId;
   @SerializedName("serviceofferingname")
   private String serviceOfferingName;
   private State state;
   @SerializedName("templatedisplaytext")
   private String templateDisplayText;
   @SerializedName("templateid")
   private String templateId;
   @SerializedName("templatename")
   private String templateName;
   @SerializedName("zoneid")
   private String zoneId;
   @SerializedName("zonename")
   private String zoneName;
   @SerializedName("nic")
   private Set<NIC> nics = ImmutableSet.<NIC> of();
   private String hypervisor;
   @SerializedName("securitygroup")
   private Set<SecurityGroup> securityGroups = ImmutableSet.<SecurityGroup> of();

   public VirtualMachine(String id, String account, long cpuCount, long cpuSpeed, String cpuUsed, String displayName,
         Date created, String domain, String domainId, boolean usesVirtualNetwork, String group, String groupId,
         String guestOSId, boolean hAEnabled, String hostId, String hostname, String iPAddress, String iSODisplayText,
         String iSOId, String iSOName, String jobId, Integer jobStatus, long memory, String name, Long networkKbsRead,
                         Long networkKbsWrite, String password, boolean passwordEnabled, String publicIP, String publicIPId, String rootDeviceId, String rootDeviceType,
         Set<SecurityGroup> securityGroups, String serviceOfferingId, String serviceOfferingName, State state,
         String templateDisplayText, String templateId, String templateName, String zoneId, String zoneName, Set<NIC> nics,
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
      this.publicIP = publicIP;
      this.publicIPId = publicIPId;
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
   public String getId() {
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
   public String getDomainId() {
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
   public String getGroupId() {
      return groupId;
   }

   /**
    * @return Os type ID of the virtual machine
    */
   public String getGuestOSId() {
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
   public String getHostId() {
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
   public String getISOId() {
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
   public String getJobId() {
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
    * @return public IP of this virtual machine
    */
   public String getPublicIP() {
      return publicIP;
   }

   /**
    * @return ID of the public IP of this virtual machine
    */
   public String getPublicIPId() {
      return publicIPId;
   }

   /**
    * @return device ID of the root volume
    */
   public String getRootDeviceId() {
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
   public String getServiceOfferingId() {
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
   public String getTemplateId() {
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
   public String getZoneId() {
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
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualMachine that = (VirtualMachine) o;

      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(cpuCount, that.cpuCount)) return false;
      if (!Objects.equal(cpuSpeed, that.cpuSpeed)) return false;
      if (!Objects.equal(cpuUsed, that.cpuUsed)) return false;
      if (!Objects.equal(displayName, that.displayName)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(usesVirtualNetwork, that.usesVirtualNetwork)) return false;
      if (!Objects.equal(group, that.group)) return false;
      if (!Objects.equal(groupId, that.groupId)) return false;
      if (!Objects.equal(guestOSId, that.guestOSId)) return false;
      if (!Objects.equal(HAEnabled, that.HAEnabled)) return false;
      if (!Objects.equal(hostId, that.hostId)) return false;
      if (!Objects.equal(hostname, that.hostname)) return false;
      if (!Objects.equal(IPAddress, that.IPAddress)) return false;
      if (!Objects.equal(ISODisplayText, that.ISODisplayText)) return false;
      if (!Objects.equal(ISOId, that.ISOId)) return false;
      if (!Objects.equal(ISOName, that.ISOName)) return false;
      if (!Objects.equal(jobId, that.jobId)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;
      if (!Objects.equal(memory, that.memory)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(networkKbsRead, that.networkKbsRead)) return false;
      if (!Objects.equal(networkKbsWrite, that.networkKbsWrite)) return false;
      if (!Objects.equal(password, that.password)) return false;
      if (!Objects.equal(passwordEnabled, that.passwordEnabled)) return false;
      if (!Objects.equal(publicIP, that.publicIP)) return false;
      if (!Objects.equal(publicIPId, that.publicIPId)) return false;
      if (!Objects.equal(rootDeviceId, that.rootDeviceId)) return false;
      if (!Objects.equal(rootDeviceType, that.rootDeviceType)) return false;
      if (!Objects.equal(securityGroups, that.securityGroups)) return false;
      if (!Objects.equal(serviceOfferingId, that.serviceOfferingId)) return false;
      if (!Objects.equal(serviceOfferingName, that.serviceOfferingName)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(templateDisplayText, that.templateDisplayText)) return false;
      if (!Objects.equal(templateId, that.templateId)) return false;
      if (!Objects.equal(templateName, that.templateName)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(zoneName, that.zoneName)) return false;
      if (!Objects.equal(nics, that.nics)) return false;
      if (!Objects.equal(hypervisor, that.hypervisor)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, account, cpuCount, cpuSpeed, cpuUsed, displayName, created,
                               domain, domainId, usesVirtualNetwork, group, groupId, guestOSId,
                               HAEnabled, hostId, hostname, IPAddress, ISODisplayText, ISOId,
                               ISOName, jobId, jobStatus, memory, name, networkKbsRead,
                               networkKbsWrite, password, passwordEnabled, publicIP, publicIPId, rootDeviceId,
                               rootDeviceType, securityGroups, serviceOfferingId,
                               serviceOfferingName, state, templateDisplayText, templateId,
                               templateName, zoneId, zoneName, nics, hypervisor);
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
            ", publicIP='" + publicIP + '\'' +
            ", publicIPId='" + publicIPId + '\'' +
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
      return id.compareTo(arg0.getId());
   }
}
