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

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a host issued by Cloudstack
 *
 * @author Andrei Savu
 */
public class Host implements Comparable<Host> {

   public static enum ClusterType {
      CLOUD_MANAGED,
      EXTERNAL_MANAGED,
      UNKNOWN;

      public static ClusterType fromValue(String value) {
         try {
            return valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, value));
         } catch (IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
      }
   }

   public static enum State {
      CONNECTING,
      UP,
      DOWN,
      DISCONNECTED,
      UPDATING,
      PREPARE_FOR_MAINTENANCE,
      ERROR_IN_MAINTENANCE,
      MAINTENANCE,
      ALERT,
      REMOVED,
      REBALANCING,
      UNKNOWN;

      public static State fromValue(String value) {
         try {
            return valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, value));
         } catch (IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
      }
   }

   public static enum Type {
      STORAGE,
      ROUTING,
      SECONDARY_STORAGE,
      SECONDARY_STORAGE_CMD_EXECUTOR,
      CONSOLE_PROXY,
      EXTERNAL_FIREWALL,
      EXTERNAL_LOAD_BALANCER,
      PXE_SERVER,
      TRAFFIC_MONITOR,
      EXTERNAL_DHCP,
      SECONDARY_STORAGE_VM,
      LOCAL_SECONDARY_STORAGE,
      UNKNOWN;

      public static Type fromValue(String value) {
         try {
            if (value.equals("SecondaryStorageVM")) {
               return SECONDARY_STORAGE_VM;
            }
            return valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, value));

         } catch (IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         if (this == SECONDARY_STORAGE_VM) {
            return "SecondaryStorageVM"; // note the inconsistency in VM naming
         }
         return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHost(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected AllocationState allocationState;
      protected int averageLoad;
      protected String capabilities;
      protected String clusterId;
      protected String clusterName;
      protected Host.ClusterType clusterType;
      protected String cpuAllocated;
      protected int cpuNumber;
      protected int cpuSpeed;
      protected String cpuUsed;
      protected float cpuWithOverProvisioning;
      protected Date created;
      protected Date disconnected;
      protected long diskSizeAllocated;
      protected long diskSizeTotal;
      protected String events;
      protected boolean hasEnoughCapacity;
      protected ImmutableSet.Builder<String> tags = ImmutableSet.<String>builder();
      protected String hypervisor;
      protected String ipAddress;
      protected boolean localStorageActive;
      protected String jobId;
      protected AsyncJob.Status jobStatus;
      protected Date lastPinged;
      protected String managementServerId;
      protected long memoryAllocated;
      protected long memoryTotal;
      protected long memoryUsed;
      protected String name;
      protected long networkKbsRead;
      protected long networkKbsWrite;
      protected String osCategoryId;
      protected String osCategoryName;
      protected String podId;
      protected String podName;
      protected Date removed;
      protected Host.State state;
      protected Host.Type type;
      protected String version;
      protected String zoneId;
      protected String zoneName;

      /**
       * @see Host#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Host#getAllocationState()
       */
      public T allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return self();
      }

      /**
       * @see Host#getAverageLoad()
       */
      public T averageLoad(int averageLoad) {
         this.averageLoad = averageLoad;
         return self();
      }

      /**
       * @see Host#getCapabilities()
       */
      public T capabilities(String capabilities) {
         this.capabilities = capabilities;
         return self();
      }

      /**
       * @see Host#getClusterId()
       */
      public T clusterId(String clusterId) {
         this.clusterId = clusterId;
         return self();
      }

      /**
       * @see Host#getClusterName()
       */
      public T clusterName(String clusterName) {
         this.clusterName = clusterName;
         return self();
      }

      /**
       * @see Host#getClusterType()
       */
      public T clusterType(Host.ClusterType clusterType) {
         this.clusterType = clusterType;
         return self();
      }

      /**
       * @see Host#getCpuAllocated()
       */
      public T cpuAllocated(String cpuAllocated) {
         this.cpuAllocated = cpuAllocated;
         return self();
      }

      /**
       * @see Host#getCpuNumber()
       */
      public T cpuNumber(int cpuNumber) {
         this.cpuNumber = cpuNumber;
         return self();
      }

      /**
       * @see Host#getCpuSpeed()
       */
      public T cpuSpeed(int cpuSpeed) {
         this.cpuSpeed = cpuSpeed;
         return self();
      }

      /**
       * @see Host#getCpuUsed()
       */
      public T cpuUsed(String cpuUsed) {
         this.cpuUsed = cpuUsed;
         return self();
      }

      /**
       * @see Host#getCpuWithOverProvisioning()
       */
      public T cpuWithOverProvisioning(float cpuWithOverProvisioning) {
         this.cpuWithOverProvisioning = cpuWithOverProvisioning;
         return self();
      }

      /**
       * @see Host#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see Host#getDisconnected()
       */
      public T disconnected(Date disconnected) {
         this.disconnected = disconnected;
         return self();
      }

      /**
       * @see Host#getDiskSizeAllocated()
       */
      public T diskSizeAllocated(long diskSizeAllocated) {
         this.diskSizeAllocated = diskSizeAllocated;
         return self();
      }

      /**
       * @see Host#getDiskSizeTotal()
       */
      public T diskSizeTotal(long diskSizeTotal) {
         this.diskSizeTotal = diskSizeTotal;
         return self();
      }

      /**
       * @see Host#getEvents()
       */
      public T events(String events) {
         this.events = events;
         return self();
      }

      /**
       * @see Host#isHasEnoughCapacity()
       */
      public T hasEnoughCapacity(boolean hasEnoughCapacity) {
         this.hasEnoughCapacity = hasEnoughCapacity;
         return self();
      }

      /**
       * @see Host#getTags()
       */
      public T tags(Iterable<String> tags) {
         this.tags = ImmutableSet.<String>builder().addAll(tags);
         return self();
      }
      
      /**
       * @see Host#getTags()
       */
      public T tag(String tag) {
         this.tags.add(tag);
         return self();
      }
      
      /**
       * @see Host#getHypervisor()
       */
      public T hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return self();
      }

      /**
       * @see Host#getIpAddress()
       */
      public T ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return self();
      }

      /**
       * @see Host#isLocalStorageActive()
       */
      public T localStorageActive(boolean localStorageActive) {
         this.localStorageActive = localStorageActive;
         return self();
      }

      /**
       * @see Host#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see Host#getJobStatus()
       */
      public T jobStatus(AsyncJob.Status jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      /**
       * @see Host#getLastPinged()
       */
      public T lastPinged(Date lastPinged) {
         this.lastPinged = lastPinged;
         return self();
      }

      /**
       * @see Host#getManagementServerId()
       */
      public T managementServerId(String managementServerId) {
         this.managementServerId = managementServerId;
         return self();
      }

      /**
       * @see Host#getMemoryAllocated()
       */
      public T memoryAllocated(long memoryAllocated) {
         this.memoryAllocated = memoryAllocated;
         return self();
      }

      /**
       * @see Host#getMemoryTotal()
       */
      public T memoryTotal(long memoryTotal) {
         this.memoryTotal = memoryTotal;
         return self();
      }

      /**
       * @see Host#getMemoryUsed()
       */
      public T memoryUsed(long memoryUsed) {
         this.memoryUsed = memoryUsed;
         return self();
      }

      /**
       * @see Host#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Host#getNetworkKbsRead()
       */
      public T networkKbsRead(long networkKbsRead) {
         this.networkKbsRead = networkKbsRead;
         return self();
      }

      /**
       * @see Host#getNetworkKbsWrite()
       */
      public T networkKbsWrite(long networkKbsWrite) {
         this.networkKbsWrite = networkKbsWrite;
         return self();
      }

      /**
       * @see Host#getOsCategoryId()
       */
      public T osCategoryId(String osCategoryId) {
         this.osCategoryId = osCategoryId;
         return self();
      }

      /**
       * @see Host#getOsCategoryName()
       */
      public T osCategoryName(String osCategoryName) {
         this.osCategoryName = osCategoryName;
         return self();
      }

      /**
       * @see Host#getPodId()
       */
      public T podId(String podId) {
         this.podId = podId;
         return self();
      }

      /**
       * @see Host#getPodName()
       */
      public T podName(String podName) {
         this.podName = podName;
         return self();
      }

      /**
       * @see Host#getRemoved()
       */
      public T removed(Date removed) {
         this.removed = removed;
         return self();
      }

      /**
       * @see Host#getState()
       */
      public T state(Host.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see Host#getType()
       */
      public T type(Host.Type type) {
         this.type = type;
         return self();
      }

      /**
       * @see Host#getVersion()
       */
      public T version(String version) {
         this.version = version;
         return self();
      }

      /**
       * @see Host#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see Host#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }


      public Host build() {
         return new Host(id, allocationState, averageLoad, capabilities, clusterId, clusterName, clusterType, cpuAllocated, cpuNumber, cpuSpeed, cpuUsed, cpuWithOverProvisioning, created, disconnected, diskSizeAllocated, diskSizeTotal, events, hasEnoughCapacity, tags.build(), hypervisor, ipAddress, localStorageActive, jobId, jobStatus, lastPinged, managementServerId, memoryAllocated, memoryTotal, memoryUsed, name, networkKbsRead, networkKbsWrite, osCategoryId, osCategoryName, podId, podName, removed, state, type, version, zoneId, zoneName);
      }

      public T fromHost(Host in) {
         return this
               .id(in.getId())
               .allocationState(in.getAllocationState())
               .averageLoad(in.getAverageLoad())
               .capabilities(in.getCapabilities())
               .clusterId(in.getClusterId())
               .clusterName(in.getClusterName())
               .clusterType(in.getClusterType())
               .cpuAllocated(in.getCpuAllocated())
               .cpuNumber(in.getCpuNumber())
               .cpuSpeed(in.getCpuSpeed())
               .cpuUsed(in.getCpuUsed())
               .cpuWithOverProvisioning(in.getCpuWithOverProvisioning())
               .created(in.getCreated())
               .disconnected(in.getDisconnected())
               .diskSizeAllocated(in.getDiskSizeAllocated())
               .diskSizeTotal(in.getDiskSizeTotal())
               .events(in.getEvents())
               .hasEnoughCapacity(in.isHasEnoughCapacity())
               .tags(in.getTags())
               .hypervisor(in.getHypervisor())
               .ipAddress(in.getIpAddress())
               .localStorageActive(in.isLocalStorageActive())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus())
               .lastPinged(in.getLastPinged())
               .managementServerId(in.getManagementServerId())
               .memoryAllocated(in.getMemoryAllocated())
               .memoryTotal(in.getMemoryTotal())
               .memoryUsed(in.getMemoryUsed())
               .name(in.getName())
               .networkKbsRead(in.getNetworkKbsRead())
               .networkKbsWrite(in.getNetworkKbsWrite())
               .osCategoryId(in.getOsCategoryId())
               .osCategoryName(in.getOsCategoryName())
               .podId(in.getPodId())
               .podName(in.getPodName())
               .removed(in.getRemoved())
               .state(in.getState())
               .type(in.getType())
               .version(in.getVersion())
               .zoneId(in.getZoneId())
               .zoneName(in.getZoneName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final AllocationState allocationState;
   private final int averageLoad;
   private final String capabilities;
   private final String clusterId;
   private final String clusterName;
   private final Host.ClusterType clusterType;
   private final String cpuAllocated;
   private final int cpuNumber;
   private final int cpuSpeed;
   private final String cpuUsed;
   private final float cpuWithOverProvisioning;
   private final Date created;
   private final Date disconnected;
   private final long diskSizeAllocated;
   private final long diskSizeTotal;
   private final String events;
   private final boolean hasEnoughCapacity;
   private final Set<String> tags;
   private final String hypervisor;
   private final String ipAddress;
   private final boolean localStorageActive;
   private final String jobId;
   private final AsyncJob.Status jobStatus;
   private final Date lastPinged;
   private final String managementServerId;
   private final long memoryAllocated;
   private final long memoryTotal;
   private final long memoryUsed;
   private final String name;
   private final long networkKbsRead;
   private final long networkKbsWrite;
   private final String osCategoryId;
   private final String osCategoryName;
   private final String podId;
   private final String podName;
   private final Date removed;
   private final Host.State state;
   private final Host.Type type;
   private final String version;
   private final String zoneId;
   private final String zoneName;

   @ConstructorProperties({
         "id", "allocationstate", "averageload", "capabilities", "clusterid", "clustername", "clustertype", "cpuallocated", "cpunumber", "cpuspeed", "cpuused", "cpuwithoverprovisioning", "created", "disconnected", "disksizeallocated", "disksizetotal", "events", "hasenoughcapacity", "hosttags", "hypervisor", "ipaddress", "islocalstorageactive", "jobid", "jobstatus", "lastpinged", "managementserverid", "memoryallocated", "memorytotal", "memoryused", "name", "networkkbsread", "networkkbswrite", "oscategoryid", "oscategoryname", "podid", "podname", "removed", "state", "type", "version", "zoneid", "zonename"
   })
   protected Host(String id, @Nullable AllocationState allocationState, int averageLoad, @Nullable String capabilities,
                  @Nullable String clusterId, @Nullable String clusterName, @Nullable Host.ClusterType clusterType,
                  @Nullable String cpuAllocated, int cpuNumber, int cpuSpeed, @Nullable String cpuUsed,
                  float cpuWithOverProvisioning, @Nullable Date created, @Nullable Date disconnected, long diskSizeAllocated,
                  long diskSizeTotal, @Nullable String events, boolean hasEnoughCapacity, @Nullable Iterable<String> tags,
                  @Nullable String hypervisor, @Nullable String ipAddress, boolean localStorageActive, @Nullable String jobId,
                  @Nullable AsyncJob.Status jobStatus, @Nullable Date lastPinged, @Nullable String managementServerId,
                  long memoryAllocated, long memoryTotal, long memoryUsed, @Nullable String name, long networkKbsRead, long networkKbsWrite,
                  @Nullable String osCategoryId, @Nullable String osCategoryName, @Nullable String podId, @Nullable String podName,
                  @Nullable Date removed, @Nullable Host.State state, @Nullable Host.Type type, @Nullable String version, @Nullable String zoneId,
                  @Nullable String zoneName) {
      this.id = checkNotNull(id, "id");
      this.allocationState = allocationState;
      this.averageLoad = averageLoad;
      this.capabilities = capabilities;
      this.clusterId = clusterId;
      this.clusterName = clusterName;
      this.clusterType = clusterType;
      this.cpuAllocated = cpuAllocated;
      this.cpuNumber = cpuNumber;
      this.cpuSpeed = cpuSpeed;
      this.cpuUsed = cpuUsed;
      this.cpuWithOverProvisioning = cpuWithOverProvisioning;
      this.created = created;
      this.disconnected = disconnected;
      this.diskSizeAllocated = diskSizeAllocated;
      this.diskSizeTotal = diskSizeTotal;
      this.events = events;
      this.hasEnoughCapacity = hasEnoughCapacity;
      this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.<String> of();
      this.hypervisor = hypervisor;
      this.ipAddress = ipAddress;
      this.localStorageActive = localStorageActive;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
      this.lastPinged = lastPinged;
      this.managementServerId = managementServerId;
      this.memoryAllocated = memoryAllocated;
      this.memoryTotal = memoryTotal;
      this.memoryUsed = memoryUsed;
      this.name = name;
      this.networkKbsRead = networkKbsRead;
      this.networkKbsWrite = networkKbsWrite;
      this.osCategoryId = osCategoryId;
      this.osCategoryName = osCategoryName;
      this.podId = podId;
      this.podName = podName;
      this.removed = removed;
      this.state = state;
      this.type = type;
      this.version = version;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public AllocationState getAllocationState() {
      return this.allocationState;
   }

   public int getAverageLoad() {
      return this.averageLoad;
   }

   @Nullable
   public String getCapabilities() {
      return this.capabilities;
   }

   @Nullable
   public String getClusterId() {
      return this.clusterId;
   }

   @Nullable
   public String getClusterName() {
      return this.clusterName;
   }

   @Nullable
   public Host.ClusterType getClusterType() {
      return this.clusterType;
   }

   @Nullable
   public String getCpuAllocated() {
      return this.cpuAllocated;
   }

   public int getCpuNumber() {
      return this.cpuNumber;
   }

   public int getCpuSpeed() {
      return this.cpuSpeed;
   }

   @Nullable
   public String getCpuUsed() {
      return this.cpuUsed;
   }

   public float getCpuWithOverProvisioning() {
      return this.cpuWithOverProvisioning;
   }

   @Nullable
   public Date getCreated() {
      return this.created;
   }

   @Nullable
   public Date getDisconnected() {
      return this.disconnected;
   }

   public long getDiskSizeAllocated() {
      return this.diskSizeAllocated;
   }

   public long getDiskSizeTotal() {
      return this.diskSizeTotal;
   }

   @Nullable
   public String getEvents() {
      return this.events;
   }

   public boolean isHasEnoughCapacity() {
      return this.hasEnoughCapacity;
   }

   /**
    * @return the tags for the host
    */
   public Set<String> getTags() {
      return this.tags;
   }


   @Nullable
   public String getHypervisor() {
      return this.hypervisor;
   }

   @Nullable
   public String getIpAddress() {
      return this.ipAddress;
   }

   public boolean isLocalStorageActive() {
      return this.localStorageActive;
   }

   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   @Nullable
   public AsyncJob.Status getJobStatus() {
      return this.jobStatus;
   }

   @Nullable
   public Date getLastPinged() {
      return this.lastPinged;
   }

   @Nullable
   public String getManagementServerId() {
      return this.managementServerId;
   }

   public long getMemoryAllocated() {
      return this.memoryAllocated;
   }

   public long getMemoryTotal() {
      return this.memoryTotal;
   }

   public long getMemoryUsed() {
      return this.memoryUsed;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public long getNetworkKbsRead() {
      return this.networkKbsRead;
   }

   public long getNetworkKbsWrite() {
      return this.networkKbsWrite;
   }

   @Nullable
   public String getOsCategoryId() {
      return this.osCategoryId;
   }

   @Nullable
   public String getOsCategoryName() {
      return this.osCategoryName;
   }

   @Nullable
   public String getPodId() {
      return this.podId;
   }

   @Nullable
   public String getPodName() {
      return this.podName;
   }

   @Nullable
   public Date getRemoved() {
      return this.removed;
   }

   @Nullable
   public Host.State getState() {
      return this.state;
   }

   @Nullable
   public Host.Type getType() {
      return this.type;
   }

   @Nullable
   public String getVersion() {
      return this.version;
   }

   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   @Nullable
   public String getZoneName() {
      return this.zoneName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, allocationState, averageLoad, capabilities, clusterId, clusterName, clusterType, cpuAllocated, cpuNumber, cpuSpeed, cpuUsed, cpuWithOverProvisioning, created, disconnected, diskSizeAllocated, diskSizeTotal, events, hasEnoughCapacity, tags, hypervisor, ipAddress, localStorageActive, jobId, jobStatus, lastPinged, managementServerId, memoryAllocated, memoryTotal, memoryUsed, name, networkKbsRead, networkKbsWrite, osCategoryId, osCategoryName, podId, podName, removed, state, type, version, zoneId, zoneName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Host that = Host.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.allocationState, that.allocationState)
            && Objects.equal(this.averageLoad, that.averageLoad)
            && Objects.equal(this.capabilities, that.capabilities)
            && Objects.equal(this.clusterId, that.clusterId)
            && Objects.equal(this.clusterName, that.clusterName)
            && Objects.equal(this.clusterType, that.clusterType)
            && Objects.equal(this.cpuAllocated, that.cpuAllocated)
            && Objects.equal(this.cpuNumber, that.cpuNumber)
            && Objects.equal(this.cpuSpeed, that.cpuSpeed)
            && Objects.equal(this.cpuUsed, that.cpuUsed)
            && Objects.equal(this.cpuWithOverProvisioning, that.cpuWithOverProvisioning)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.disconnected, that.disconnected)
            && Objects.equal(this.diskSizeAllocated, that.diskSizeAllocated)
            && Objects.equal(this.diskSizeTotal, that.diskSizeTotal)
            && Objects.equal(this.events, that.events)
            && Objects.equal(this.hasEnoughCapacity, that.hasEnoughCapacity)
            && Objects.equal(this.tags, that.tags)
            && Objects.equal(this.hypervisor, that.hypervisor)
            && Objects.equal(this.ipAddress, that.ipAddress)
            && Objects.equal(this.localStorageActive, that.localStorageActive)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus)
            && Objects.equal(this.lastPinged, that.lastPinged)
            && Objects.equal(this.managementServerId, that.managementServerId)
            && Objects.equal(this.memoryAllocated, that.memoryAllocated)
            && Objects.equal(this.memoryTotal, that.memoryTotal)
            && Objects.equal(this.memoryUsed, that.memoryUsed)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.networkKbsRead, that.networkKbsRead)
            && Objects.equal(this.networkKbsWrite, that.networkKbsWrite)
            && Objects.equal(this.osCategoryId, that.osCategoryId)
            && Objects.equal(this.osCategoryName, that.osCategoryName)
            && Objects.equal(this.podId, that.podId)
            && Objects.equal(this.podName, that.podName)
            && Objects.equal(this.removed, that.removed)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.version, that.version)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("allocationState", allocationState).add("averageLoad", averageLoad)
            .add("capabilities", capabilities).add("clusterId", clusterId).add("clusterName", clusterName)
            .add("clusterType", clusterType).add("cpuAllocated", cpuAllocated).add("cpuNumber", cpuNumber)
            .add("cpuSpeed", cpuSpeed).add("cpuUsed", cpuUsed).add("cpuWithOverProvisioning", cpuWithOverProvisioning)
            .add("created", created).add("disconnected", disconnected).add("diskSizeAllocated", diskSizeAllocated)
            .add("diskSizeTotal", diskSizeTotal).add("events", events).add("hasEnoughCapacity", hasEnoughCapacity)
            .add("tags", tags).add("hypervisor", hypervisor).add("ipAddress", ipAddress)
            .add("localStorageActive", localStorageActive).add("jobId", jobId).add("jobStatus", jobStatus)
            .add("lastPinged", lastPinged).add("managementServerId", managementServerId).add("memoryAllocated", memoryAllocated)
            .add("memoryTotal", memoryTotal).add("memoryUsed", memoryUsed).add("name", name).add("networkKbsRead", networkKbsRead)
            .add("networkKbsWrite", networkKbsWrite).add("osCategoryId", osCategoryId).add("osCategoryName", osCategoryName)
            .add("podId", podId).add("podName", podName).add("removed", removed).add("state", state).add("type", type)
            .add("version", version).add("zoneId", zoneId).add("zoneName", zoneName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Host other) {
      return this.getId().compareTo(other.getId());
   }
}
