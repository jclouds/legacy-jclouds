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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Class VirtualMachine
 *
 * @author Adrian Cole
 */
public class VirtualMachine {

   /**
    */
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

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVirtualMachine(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected long cpuCount;
      protected long cpuSpeed;
      protected String cpuUsed;
      protected String displayName;
      protected Date created;
      protected String domain;
      protected String domainId;
      protected boolean usesVirtualNetwork;
      protected String group;
      protected String groupId;
      protected String guestOSId;
      protected boolean HAEnabled;
      protected String hostId;
      protected String hostname;
      protected String IPAddress;
      protected String ISODisplayText;
      protected String ISOId;
      protected String ISOName;
      protected String jobId;
      protected Integer jobStatus;
      protected long memory;
      protected String name;
      protected Long networkKbsRead;
      protected Long networkKbsWrite;
      protected String password;
      protected boolean passwordEnabled;
      protected String publicIP;
      protected String publicIPId;
      protected String rootDeviceId;
      protected String rootDeviceType;
      protected String serviceOfferingId;
      protected String serviceOfferingName;
      protected VirtualMachine.State state;
      protected String templateDisplayText;
      protected String templateId;
      protected String templateName;
      protected String zoneId;
      protected String zoneName;
      protected Set<NIC> nics = ImmutableSet.of();
      protected String hypervisor;
      protected Set<SecurityGroup> securityGroups = ImmutableSet.of();

      /**
       * @see VirtualMachine#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see VirtualMachine#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see VirtualMachine#getCpuCount()
       */
      public T cpuCount(long cpuCount) {
         this.cpuCount = cpuCount;
         return self();
      }

      /**
       * @see VirtualMachine#getCpuSpeed()
       */
      public T cpuSpeed(long cpuSpeed) {
         this.cpuSpeed = cpuSpeed;
         return self();
      }

      /**
       * @see VirtualMachine#getCpuUsed()
       */
      public T cpuUsed(String cpuUsed) {
         this.cpuUsed = cpuUsed;
         return self();
      }

      /**
       * @see VirtualMachine#getDisplayName()
       */
      public T displayName(String displayName) {
         this.displayName = displayName;
         return self();
      }

      /**
       * @see VirtualMachine#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see VirtualMachine#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see VirtualMachine#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see VirtualMachine#usesVirtualNetwork()
       */
      public T usesVirtualNetwork(boolean usesVirtualNetwork) {
         this.usesVirtualNetwork = usesVirtualNetwork;
         return self();
      }

      /**
       * @see VirtualMachine#getGroup()
       */
      public T group(String group) {
         this.group = group;
         return self();
      }

      /**
       * @see VirtualMachine#getGroupId()
       */
      public T groupId(String groupId) {
         this.groupId = groupId;
         return self();
      }

      /**
       * @see VirtualMachine#getGuestOSId()
       */
      public T guestOSId(String guestOSId) {
         this.guestOSId = guestOSId;
         return self();
      }

      /**
       * @see VirtualMachine#isHAEnabled()
       */
      public T isHAEnabled(boolean HAEnabled) {
         this.HAEnabled = HAEnabled;
         return self();
      }

      /**
       * @see VirtualMachine#getHostId()
       */
      public T hostId(String hostId) {
         this.hostId = hostId;
         return self();
      }

      /**
       * @see VirtualMachine#getHostname()
       */
      public T hostname(String hostname) {
         this.hostname = hostname;
         return self();
      }

      /**
       * @see VirtualMachine#getIPAddress()
       */
      public T IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return self();
      }

      /**
       * @see VirtualMachine#getISODisplayText()
       */
      public T ISODisplayText(String ISODisplayText) {
         this.ISODisplayText = ISODisplayText;
         return self();
      }

      /**
       * @see VirtualMachine#getISOId()
       */
      public T ISOId(String ISOId) {
         this.ISOId = ISOId;
         return self();
      }

      /**
       * @see VirtualMachine#getISOName()
       */
      public T ISOName(String ISOName) {
         this.ISOName = ISOName;
         return self();
      }

      /**
       * @see VirtualMachine#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see VirtualMachine#getJobStatus()
       */
      public T jobStatus(Integer jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      /**
       * @see VirtualMachine#getMemory()
       */
      public T memory(long memory) {
         this.memory = memory;
         return self();
      }

      /**
       * @see VirtualMachine#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see VirtualMachine#getNetworkKbsRead()
       */
      public T networkKbsRead(Long networkKbsRead) {
         this.networkKbsRead = networkKbsRead;
         return self();
      }

      /**
       * @see VirtualMachine#getNetworkKbsWrite()
       */
      public T networkKbsWrite(Long networkKbsWrite) {
         this.networkKbsWrite = networkKbsWrite;
         return self();
      }

      /**
       * @see VirtualMachine#getPassword()
       */
      public T password(String password) {
         this.password = password;
         return self();
      }

      /**
       * @see VirtualMachine#isPasswordEnabled()
       */
      public T passwordEnabled(boolean passwordEnabled) {
         this.passwordEnabled = passwordEnabled;
         return self();
      }

      /**
       * @see VirtualMachine#getPublicIP()
       */
      public T publicIP(String publicIP) {
         this.publicIP = publicIP;
         return self();
      }

      /**
       * @see VirtualMachine#getPublicIPId()
       */
      public T publicIPId(String publicIPId) {
         this.publicIPId = publicIPId;
         return self();
      }

      /**
       * @see VirtualMachine#getRootDeviceId()
       */
      public T rootDeviceId(String rootDeviceId) {
         this.rootDeviceId = rootDeviceId;
         return self();
      }

      /**
       * @see VirtualMachine#getRootDeviceType()
       */
      public T rootDeviceType(String rootDeviceType) {
         this.rootDeviceType = rootDeviceType;
         return self();
      }

      /**
       * @see VirtualMachine#getServiceOfferingId()
       */
      public T serviceOfferingId(String serviceOfferingId) {
         this.serviceOfferingId = serviceOfferingId;
         return self();
      }

      /**
       * @see VirtualMachine#getServiceOfferingName()
       */
      public T serviceOfferingName(String serviceOfferingName) {
         this.serviceOfferingName = serviceOfferingName;
         return self();
      }

      /**
       * @see VirtualMachine#getState()
       */
      public T state(VirtualMachine.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see VirtualMachine#getTemplateDisplayText()
       */
      public T templateDisplayText(String templateDisplayText) {
         this.templateDisplayText = templateDisplayText;
         return self();
      }

      /**
       * @see VirtualMachine#getTemplateId()
       */
      public T templateId(String templateId) {
         this.templateId = templateId;
         return self();
      }

      /**
       * @see VirtualMachine#getTemplateName()
       */
      public T templateName(String templateName) {
         this.templateName = templateName;
         return self();
      }

      /**
       * @see VirtualMachine#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see VirtualMachine#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      /**
       * @see VirtualMachine#getNICs()
       */
      public T nics(Set<NIC> nics) {
         this.nics = ImmutableSet.copyOf(checkNotNull(nics, "nics"));
         return self();
      }

      public T nics(NIC... in) {
         return nics(ImmutableSet.copyOf(in));
      }

      /**
       * @see VirtualMachine#getHypervisor()
       */
      public T hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return self();
      }

      /**
       * @see VirtualMachine#getSecurityGroups()
       */
      public T securityGroups(Set<SecurityGroup> securityGroups) {
         this.securityGroups = ImmutableSet.copyOf(checkNotNull(securityGroups, "securityGroups"));
         return self();
      }

      public T securityGroups(SecurityGroup... in) {
         return securityGroups(ImmutableSet.copyOf(in));
      }

      public VirtualMachine build() {
         return new VirtualMachine(id, account, cpuCount, cpuSpeed, cpuUsed, displayName, created, domain, domainId,
               usesVirtualNetwork, group, groupId, guestOSId, HAEnabled, hostId, hostname, IPAddress, ISODisplayText, ISOId,
               ISOName, jobId, jobStatus, memory, name, networkKbsRead, networkKbsWrite, password, passwordEnabled, publicIP,
               publicIPId, rootDeviceId, rootDeviceType, serviceOfferingId, serviceOfferingName, state, templateDisplayText,
               templateId, templateName, zoneId, zoneName, nics, hypervisor, securityGroups);
      }

      public T fromVirtualMachine(VirtualMachine in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .cpuCount(in.getCpuCount())
               .cpuSpeed(in.getCpuSpeed())
               .cpuUsed(in.getCpuUsedAsString())
               .displayName(in.getDisplayName())
               .created(in.getCreated())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .usesVirtualNetwork(in.usesVirtualNetwork())
               .group(in.getGroup())
               .groupId(in.getGroupId())
               .guestOSId(in.getGuestOSId())
               .isHAEnabled(in.isHAEnabled())
               .hostId(in.getHostId())
               .hostname(in.getHostname())
               .IPAddress(in.getIPAddress())
               .ISODisplayText(in.getISODisplayText())
               .ISOId(in.getISOId())
               .ISOName(in.getISOName())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus())
               .memory(in.getMemory())
               .name(in.getName())
               .networkKbsRead(in.getNetworkKbsRead())
               .networkKbsWrite(in.getNetworkKbsWrite())
               .password(in.getPassword())
               .passwordEnabled(in.isPasswordEnabled())
               .publicIP(in.getPublicIP())
               .publicIPId(in.getPublicIPId())
               .rootDeviceId(in.getRootDeviceId())
               .rootDeviceType(in.getRootDeviceType())
               .serviceOfferingId(in.getServiceOfferingId())
               .serviceOfferingName(in.getServiceOfferingName())
               .state(in.getState())
               .templateDisplayText(in.getTemplateDisplayText())
               .templateId(in.getTemplateId())
               .templateName(in.getTemplateName())
               .zoneId(in.getZoneId())
               .zoneName(in.getZoneName())
               .nics(in.getNICs())
               .hypervisor(in.getHypervisor())
               .securityGroups(in.getSecurityGroups());
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
   private final long cpuCount;
   private final long cpuSpeed;
   private final String cpuUsed;
   private final String displayName;
   private final Date created;
   private final String domain;
   private final String domainId;
   private final boolean usesVirtualNetwork;
   private final String group;
   private final String groupId;
   private final String guestOSId;
   private final boolean HAEnabled;
   private final String hostId;
   private final String hostname;
   private final String IPAddress;
   private final String ISODisplayText;
   private final String ISOId;
   private final String ISOName;
   private final String jobId;
   private final Integer jobStatus;
   private final long memory;
   private final String name;
   private final Long networkKbsRead;
   private final Long networkKbsWrite;
   private final String password;
   private final boolean passwordEnabled;
   private final String publicIP;
   private final String publicIPId;
   private final String rootDeviceId;
   private final String rootDeviceType;
   private final String serviceOfferingId;
   private final String serviceOfferingName;
   private final VirtualMachine.State state;
   private final String templateDisplayText;
   private final String templateId;
   private final String templateName;
   private final String zoneId;
   private final String zoneName;
   private final Set<NIC> nics;
   private final String hypervisor;
   private final Set<SecurityGroup> securityGroups;

   @ConstructorProperties({
         "id", "account", "cpunumber", "cpuspeed", "cpuused", "displayname", "created", "domain", "domainid", "forvirtualnetwork", "group", "groupid", "guestosid", "haenable", "hostid", "hostname", "ipaddress", "isodisplaytext", "isoid", "isoname", "jobid", "jobstatus", "memory", "name", "networkkbsread", "networkkbswrite", "password", "passwordenabled", "publicip", "publicipid", "rootdeviceid", "rootdevicetype", "serviceofferingid", "serviceofferingname", "state", "templatedisplaytext", "templateid", "templatename", "zoneid", "zonename", "nic", "hypervisor", "securitygroup"
   })
   protected VirtualMachine(String id, @Nullable String account, long cpuCount, long cpuSpeed, @Nullable String cpuUsed,
                            @Nullable String displayName, @Nullable Date created, @Nullable String domain, @Nullable String domainId,
                            boolean usesVirtualNetwork, @Nullable String group, @Nullable String groupId, @Nullable String guestOSId,
                            boolean HAEnabled, @Nullable String hostId, @Nullable String hostname, String IPAddress, String ISODisplayText,
                            @Nullable String ISOId, @Nullable String ISOName, @Nullable String jobId, @Nullable Integer jobStatus,
                            long memory, @Nullable String name, @Nullable Long networkKbsRead, @Nullable Long networkKbsWrite, @Nullable String password,
                            boolean passwordEnabled, @Nullable String publicIP, @Nullable String publicIPId, @Nullable String rootDeviceId,
                            @Nullable String rootDeviceType, @Nullable String serviceOfferingId, @Nullable String serviceOfferingName,
                            @Nullable VirtualMachine.State state, @Nullable String templateDisplayText, @Nullable String templateId,
                            @Nullable String templateName, @Nullable String zoneId, @Nullable String zoneName, @Nullable Set<NIC> nics,
                            @Nullable String hypervisor, @Nullable Set<SecurityGroup> securityGroups) {
      Preconditions.checkArgument(Strings.isNullOrEmpty(cpuUsed) || cpuUsed.matches("^[0-9\\.|,\\-]+%$"), "cpuUsed value should be a decimal number followed by %");
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.cpuCount = cpuCount;
      this.cpuSpeed = cpuSpeed;
      this.cpuUsed = cpuUsed;
      this.displayName = displayName;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.usesVirtualNetwork = usesVirtualNetwork;
      this.group = group;
      this.groupId = groupId;
      this.guestOSId = guestOSId;
      this.HAEnabled = HAEnabled;
      this.hostId = hostId;
      this.hostname = hostname;
      this.IPAddress = IPAddress;
      this.ISODisplayText = ISODisplayText;
      this.ISOId = ISOId;
      this.ISOName = ISOName;
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
      this.serviceOfferingId = serviceOfferingId;
      this.serviceOfferingName = serviceOfferingName;
      this.state = state;
      this.templateDisplayText = templateDisplayText;
      this.templateId = templateId;
      this.templateName = templateName;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
      this.nics = nics == null ? ImmutableSet.<NIC>of() : ImmutableSet.copyOf(nics);
      this.hypervisor = hypervisor;
      this.securityGroups = securityGroups == null ? ImmutableSet.<SecurityGroup>of() : ImmutableSet.copyOf(securityGroups);
   }

   /**
    * @return the ID of the virtual machine
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account associated with the virtual machine
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the number of cpu this virtual machine is running with
    */
   public long getCpuCount() {
      return this.cpuCount;
   }

   /**
    * @return the speed of each cpu
    */
   public long getCpuSpeed() {
      return this.cpuSpeed;
   }

   /**
    * @return the amount of the vm's CPU currently used
    */
   public float getCpuUsed() {
      return cpuUsed != null ? Float.parseFloat(cpuUsed.substring(0, cpuUsed.length() - 1).replace(',', '.')) : 0.0f;
   }

   private String getCpuUsedAsString() {
      return cpuUsed;
   }

   /**
    * @return user generated name. The name of the virtual machine is returned
    *         if no displayname exists.
    */
   @Nullable
   public String getDisplayName() {
      return this.displayName;
   }

   /**
    * @return the date when this virtual machine was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the name of the domain in which the virtual machine exists
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the ID of the domain in which the virtual machine exists
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the virtual network for the service offering
    */
   public boolean usesVirtualNetwork() {
      return this.usesVirtualNetwork;
   }

   /**
    * @return the group name of the virtual machine
    */
   @Nullable
   public String getGroup() {
      return this.group;
   }

   /**
    * @return the group ID of the virtual machine
    */
   @Nullable
   public String getGroupId() {
      return this.groupId;
   }

   /**
    * @return Os type ID of the virtual machine
    */
   @Nullable
   public String getGuestOSId() {
      return this.guestOSId;
   }

   /**
    * @return true if high-availability is enabled, false otherwise
    */
   public boolean isHAEnabled() {
      return this.HAEnabled;
   }

   /**
    * @return the ID of the host for the virtual machine
    */
   @Nullable
   public String getHostId() {
      return this.hostId;
   }

   /**
    * @return the name of the host for the virtual machine
    */
   @Nullable
   public String getHostname() {
      return this.hostname;
   }

   /**
    * @return the ip address of the virtual machine
    */
   @Nullable
   public String getIPAddress() {
      return this.IPAddress;
   }

   /**
    * @return an alternate display text of the ISO attached to the virtual
    *         machine
    */
   @Nullable
   public String getISODisplayText() {
      return this.ISODisplayText;
   }

   /**
    * @return the ID of the ISO attached to the virtual machine
    */
   @Nullable
   public String getISOId() {
      return this.ISOId;
   }

   /**
    * @return the name of the ISO attached to the virtual machine
    */
   @Nullable
   public String getISOName() {
      return this.ISOName;
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

   /**
    * @return the memory allocated for the virtual machine
    */
   public long getMemory() {
      return this.memory;
   }

   /**
    * @return the name of the virtual machine
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the incoming network traffic on the vm
    */
   @Nullable
   public Long getNetworkKbsRead() {
      return this.networkKbsRead;
   }

   /**
    * @return the outgoing network traffic on the host
    */
   @Nullable
   public Long getNetworkKbsWrite() {
      return this.networkKbsWrite;
   }

   /**
    * @return the password (if exists) of the virtual machine
    */
   @Nullable
   public String getPassword() {
      return this.password;
   }

   /**
    * @return true if the password rest feature is enabled, false otherwise
    */
   public boolean isPasswordEnabled() {
      return this.passwordEnabled;
   }

   /**
    * @return public IP of this virtual machine
    */
   @Nullable
   public String getPublicIP() {
      return this.publicIP;
   }

   /**
    * @return ID of the public IP of this virtual machine
    */
   @Nullable
   public String getPublicIPId() {
      return this.publicIPId;
   }

   /**
    * @return device ID of the root volume
    */
   @Nullable
   public String getRootDeviceId() {
      return this.rootDeviceId;
   }

   /**
    * @return device type of the root volume
    */
   @Nullable
   public String getRootDeviceType() {
      return this.rootDeviceType;
   }

   /**
    * @return the ID of the service offering of the virtual machine
    */
   @Nullable
   public String getServiceOfferingId() {
      return this.serviceOfferingId;
   }

   /**
    * @return the name of the service offering of the virtual machine
    */
   @Nullable
   public String getServiceOfferingName() {
      return this.serviceOfferingName;
   }

   /**
    * @return the state of the virtual machine
    */
   @Nullable
   public VirtualMachine.State getState() {
      return this.state;
   }

   /**
    * @return an alternate display text of the template for the virtual machine
    */
   @Nullable
   public String getTemplateDisplayText() {
      return this.templateDisplayText;
   }

   /**
    * @return the ID of the template for the virtual machine. A -1 is returned
    *         if the virtual machine was created from an ISO file.
    */
   @Nullable
   public String getTemplateId() {
      return this.templateId;
   }

   /**
    * @return the name of the template for the virtual machine
    */
   @Nullable
   public String getTemplateName() {
      return this.templateName;
   }

   /**
    * @return the ID of the availability zone for the virtual machine
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   /**
    * @return the name of the availability zone for the virtual machine
    */
   @Nullable
   public String getZoneName() {
      return this.zoneName;
   }

   public Set<NIC> getNICs() {
      return this.nics;
   }

   /**
    * @return type of the hypervisor
    */
   @Nullable
   public String getHypervisor() {
      return this.hypervisor;
   }

   /**
    * @return list of security groups associated with the virtual machine
    */
   public Set<SecurityGroup> getSecurityGroups() {
      return this.securityGroups;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, cpuCount, cpuSpeed, cpuUsed, displayName, created, domain, domainId, usesVirtualNetwork, group, groupId, guestOSId, HAEnabled, hostId, hostname, IPAddress, ISODisplayText, ISOId, ISOName, jobId, jobStatus, memory, name, networkKbsRead, networkKbsWrite, password, passwordEnabled, publicIP, publicIPId, rootDeviceId, rootDeviceType, serviceOfferingId, serviceOfferingName, state, templateDisplayText, templateId, templateName, zoneId, zoneName, nics, hypervisor, securityGroups);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VirtualMachine that = VirtualMachine.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.cpuCount, that.cpuCount)
            && Objects.equal(this.cpuSpeed, that.cpuSpeed)
            && Objects.equal(this.cpuUsed, that.cpuUsed)
            && Objects.equal(this.displayName, that.displayName)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.usesVirtualNetwork, that.usesVirtualNetwork)
            && Objects.equal(this.group, that.group)
            && Objects.equal(this.groupId, that.groupId)
            && Objects.equal(this.guestOSId, that.guestOSId)
            && Objects.equal(this.HAEnabled, that.HAEnabled)
            && Objects.equal(this.hostId, that.hostId)
            && Objects.equal(this.hostname, that.hostname)
            && Objects.equal(this.IPAddress, that.IPAddress)
            && Objects.equal(this.ISODisplayText, that.ISODisplayText)
            && Objects.equal(this.ISOId, that.ISOId)
            && Objects.equal(this.ISOName, that.ISOName)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus)
            && Objects.equal(this.memory, that.memory)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.networkKbsRead, that.networkKbsRead)
            && Objects.equal(this.networkKbsWrite, that.networkKbsWrite)
            && Objects.equal(this.password, that.password)
            && Objects.equal(this.passwordEnabled, that.passwordEnabled)
            && Objects.equal(this.publicIP, that.publicIP)
            && Objects.equal(this.publicIPId, that.publicIPId)
            && Objects.equal(this.rootDeviceId, that.rootDeviceId)
            && Objects.equal(this.rootDeviceType, that.rootDeviceType)
            && Objects.equal(this.serviceOfferingId, that.serviceOfferingId)
            && Objects.equal(this.serviceOfferingName, that.serviceOfferingName)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.templateDisplayText, that.templateDisplayText)
            && Objects.equal(this.templateId, that.templateId)
            && Objects.equal(this.templateName, that.templateName)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName)
            && Objects.equal(this.nics, that.nics)
            && Objects.equal(this.hypervisor, that.hypervisor)
            && Objects.equal(this.securityGroups, that.securityGroups);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("cpuCount", cpuCount).add("cpuSpeed", cpuSpeed).add("cpuUsed", cpuUsed)
            .add("displayName", displayName).add("created", created).add("domain", domain).add("domainId", domainId)
            .add("usesVirtualNetwork", usesVirtualNetwork).add("group", group).add("groupId", groupId).add("guestOSId", guestOSId)
            .add("HAEnabled", HAEnabled).add("hostId", hostId).add("hostname", hostname).add("IPAddress", IPAddress)
            .add("ISODisplayText", ISODisplayText).add("ISOId", ISOId).add("ISOName", ISOName).add("jobId", jobId)
            .add("jobStatus", jobStatus).add("memory", memory).add("name", name).add("networkKbsRead", networkKbsRead)
            .add("networkKbsWrite", networkKbsWrite).add("password", password).add("passwordEnabled", passwordEnabled)
            .add("publicIP", publicIP).add("publicIPId", publicIPId).add("rootDeviceId", rootDeviceId).add("rootDeviceType", rootDeviceType)
            .add("serviceOfferingId", serviceOfferingId).add("serviceOfferingName", serviceOfferingName).add("state", state)
            .add("templateDisplayText", templateDisplayText).add("templateId", templateId).add("templateName", templateName)
            .add("zoneId", zoneId).add("zoneName", zoneName).add("nics", nics).add("hypervisor", hypervisor).add("securityGroups", securityGroups);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
