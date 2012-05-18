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

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.util.Date;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

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
         } catch(IllegalArgumentException e) {
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
         } catch(IllegalArgumentException e) {
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

         } catch(IllegalArgumentException e) {
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

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private AllocationState allocationState;
      private int averageLoad;
      private String capabilities;
      private long clusterId;
      private String clusterName;
      private ClusterType clusterType;
      private String cpuAllocated;
      private int cpuNumber;
      private int cpuSpeed;
      private String cpuUsed;
      private float cpuWithOverProvisioning;
      private Date created;
      private Date disconnected;
      private long diskSizeAllocated;
      private long diskSizeTotal;
      private String events;
      private boolean hasEnoughCapacity;
      private String hostTags;
      private String hypervisor;
      private String ipAddress;
      private boolean localStorageActive;
      private long jobId;
      private AsyncJob.Status jobStatus;
      private Date lastPinged;
      private long managementServerId;
      private long memoryAllocated;
      private long memoryTotal;
      private long memoryUsed;
      private String name;
      private long networkKbsRead;
      private long networkKbsWrite;
      private long osCategoryId;
      private long osCategoryName;
      private long podId;
      private String podName;
      private Date removed;
      private State state;
      private Type type;
      private String version;
      private long zoneId;
      private String zoneName;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return this;
      }

      public Builder averageLoad(int averageLoad) {
         this.averageLoad = averageLoad;
         return this;
      }

      public Builder capabilities(String capabilities) {
         this.capabilities = capabilities;
         return this;
      }

      public Builder clusterId(long clusterId) {
         this.clusterId = clusterId;
         return this;
      }

      public Builder clusterName(String clusterName) {
         this.clusterName = clusterName;
         return this;
      }

      public Builder clusterType(ClusterType clusterType) {
         this.clusterType = clusterType;
         return this;
      }

      public Builder cpuAllocated(String cpuAllocated) {
         this.cpuAllocated = cpuAllocated;
         return this;
      }

      public Builder cpuNumber(int cpuNumber) {
         this.cpuNumber = cpuNumber;
         return this;
      }

      public Builder cpuSpeed(int cpuSpeed) {
         this.cpuSpeed = cpuSpeed;
         return this;
      }

      public Builder cpuUsed(String cpuUsed) {
         this.cpuUsed = cpuUsed;
         return this;
      }

      public Builder cpuWithOverProvisioning(float cpuWithOverProvisioning) {
         this.cpuWithOverProvisioning = cpuWithOverProvisioning;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder disconnected(Date disconnected) {
         this.disconnected = disconnected;
         return this;
      }

      public Builder diskSizeAllocated(long diskSizeAllocated) {
         this.diskSizeAllocated = diskSizeAllocated;
         return this;
      }

      public Builder diskSizeTotal(long diskSizeTotal) {
         this.diskSizeTotal = diskSizeTotal;
         return this;
      }

      public Builder events(String events) {
         this.events = events;
         return this;
      }

      public Builder hasEnoughCapacity(boolean hasEnoughCapacity) {
         this.hasEnoughCapacity = hasEnoughCapacity;
         return this;
      }

      public Builder hostTags(String hostTags) {
         this.hostTags = hostTags;
         return this;
      }

      public Builder hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder localStorageActive(boolean localStorageActive) {
         this.localStorageActive = localStorageActive;
         return this;
      }

      public Builder jobId(long jobId) {
         this.jobId = jobId;
         return this;
      }

      public Builder jobStatus(AsyncJob.Status jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      public Builder lastPinged(Date lastPinged) {
         this.lastPinged = lastPinged;
         return this;
      }

      public Builder managementServerId(long managementServerId) {
         this.managementServerId = managementServerId;
         return this;
      }

      public Builder memoryAllocated(long memoryAllocated) {
         this.memoryAllocated = memoryAllocated;
         return this;
      }

      public Builder memoryTotal(long memoryTotal) {
         this.memoryTotal = memoryTotal;
         return this;
      }

      public Builder memoryUsed(long memoryUsed) {
         this.memoryUsed = memoryUsed;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder networkKbsRead(long networkKbsRead) {
         this.networkKbsRead = networkKbsRead;
         return this;
      }

      public Builder networkKbsWrite(long networkKbsWrite) {
         this.networkKbsWrite = networkKbsWrite;
         return this;
      }

      public Builder osCategoryId(long osCategoryId) {
         this.osCategoryId = osCategoryId;
         return this;
      }

      public Builder osCategoryName(long osCategoryName) {
         this.osCategoryName = osCategoryName;
         return this;
      }

      public Builder podId(long podId) {
         this.podId = podId;
         return this;
      }

      public Builder podName(String podName) {
         this.podName = podName;
         return this;
      }

      public Builder removed(Date removed) {
         this.removed = removed;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder version(String version) {
         this.version = version;
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

      public Host build() {
         return new Host(id, allocationState, averageLoad, capabilities,
            clusterId, clusterName, clusterType, cpuAllocated,
            cpuNumber, cpuSpeed, cpuUsed, cpuWithOverProvisioning,
            created, disconnected, diskSizeAllocated, diskSizeTotal,
            events, hasEnoughCapacity, hostTags, hypervisor,
            ipAddress, localStorageActive, jobId, jobStatus,
            lastPinged, managementServerId, memoryAllocated, memoryTotal,
            memoryUsed, name, networkKbsRead, networkKbsWrite,
            osCategoryId, osCategoryName, podId, podName, removed,
            state, type, version, zoneId, zoneName);
      }
   }

   private long id;
   @SerializedName("allocationstate")
   private AllocationState allocationState;
   @SerializedName("averageload")
   private int averageLoad;
   @SerializedName("capabilities")
   private String capabilities;
   @SerializedName("clusterid")
   private long clusterId;
   @SerializedName("clustername")
   private String clusterName;
   @SerializedName("clustertype")
   private ClusterType clusterType;
   @SerializedName("cpuallocated")
   private String cpuAllocated;
   @SerializedName("cpunumber")
   private int cpuNumber;
   @SerializedName("cpuspeed")
   private int cpuSpeed;
   @SerializedName("cpuused")
   private String cpuUsed;
   @SerializedName("cpuwithoverprovisioning")
   private float cpuWithOverProvisioning;
   private Date created;
   private Date disconnected;
   @SerializedName("disksizeallocated")
   private long diskSizeAllocated;
   @SerializedName("disksizetotal")
   private long diskSizeTotal;
   private String events;
   @SerializedName("hasenoughcapacity")
   private boolean hasEnoughCapacity;
   @SerializedName("hosttags")
   private String hostTags;
   private String hypervisor;
   @SerializedName("ipaddress")
   private String ipAddress;
   @SerializedName("islocalstorageactive")
   private boolean localStorageActive;
   @SerializedName("jobid")
   private long jobId;
   @SerializedName("jobstatus")
   private AsyncJob.Status jobStatus;
   @SerializedName("lastpinged")
   private Date lastPinged;
   @SerializedName("managementserverid")
   private long managementServerId;
   @SerializedName("memoryallocated")
   private long memoryAllocated;
   @SerializedName("memorytotal")
   private long memoryTotal;
   @SerializedName("memoryused")
   private long memoryUsed;
   private String name;
   @SerializedName("networkkbsread")
   private long networkKbsRead;
   @SerializedName("networkkbswrite")
   private long networkKbsWrite;
   @SerializedName("oscategoryid")
   private long osCategoryId;
   @SerializedName("oscategoryname")
   private long osCategoryName;
   @SerializedName("podid")
   private long podId;
   @SerializedName("podname")
   private String podName;
   private Date removed;
   private State state;
   private Type type;
   private String version;
   @SerializedName("zoneid")
   private long zoneId;
   @SerializedName("zonename")
   private String zoneName;

   /* exists for the deserializer, only */
   Host() {
   }

   public Host(long id, AllocationState allocationState, int averageLoad, String capabilities,
               long clusterId, String clusterName, ClusterType clusterType, String cpuAllocated,
               int cpuNumber, int cpuSpeed, String cpuUsed, float cpuWithOverProvisioning,
               Date created, Date disconnected, long diskSizeAllocated, long diskSizeTotal,
               String events, boolean hasEnoughCapacity, String hostTags, String hypervisor,
               String ipAddress, boolean localStorageActive, long jobId, AsyncJob.Status jobStatus,
               Date lastPinged, long managementServerId, long memoryAllocated, long memoryTotal,
               long memoryUsed, String name, long networkKbsRead, long networkKbsWrite,
               long osCategoryId, long osCategoryName, long podId, String podName, Date removed,
               State state, Type type, String version, long zoneId, String zoneName) {
      this.id = id;
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
      this.hostTags = hostTags;
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

   public long getId() {
      return id;
   }

   public AllocationState getAllocationState() {
      return allocationState;
   }

   public int getAverageLoad() {
      return averageLoad;
   }

   public String getCapabilities() {
      return capabilities;
   }

   public long getClusterId() {
      return clusterId;
   }

   public String getClusterName() {
      return clusterName;
   }

   public ClusterType getClusterType() {
      return clusterType;
   }

   public String getCpuAllocated() {
      return cpuAllocated;
   }

   public int getCpuNumber() {
      return cpuNumber;
   }

   public int getCpuSpeed() {
      return cpuSpeed;
   }

   public String getCpuUsed() {
      return cpuUsed;
   }

   public float getCpuWithOverProvisioning() {
      return cpuWithOverProvisioning;
   }

   public Date getCreated() {
      return created;
   }

   public Date getDisconnected() {
      return disconnected;
   }

   public long getDiskSizeAllocated() {
      return diskSizeAllocated;
   }

   public long getDiskSizeTotal() {
      return diskSizeTotal;
   }

   public String getEvents() {
      return events;
   }

   public boolean isHasEnoughCapacity() {
      return hasEnoughCapacity;
   }

   public String getHostTags() {
      return hostTags;
   }

   public String getHypervisor() {
      return hypervisor;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public boolean isLocalStorageActive() {
      return localStorageActive;
   }

   public long getJobId() {
      return jobId;
   }

   public AsyncJob.Status getJobStatus() {
      return jobStatus;
   }

   public Date getLastPinged() {
      return lastPinged;
   }

   public long getManagementServerId() {
      return managementServerId;
   }

   public long getMemoryAllocated() {
      return memoryAllocated;
   }

   public long getMemoryTotal() {
      return memoryTotal;
   }

   public long getMemoryUsed() {
      return memoryUsed;
   }

   public String getName() {
      return name;
   }

   public long getNetworkKbsRead() {
      return networkKbsRead;
   }

   public long getNetworkKbsWrite() {
      return networkKbsWrite;
   }

   public long getOsCategoryId() {
      return osCategoryId;
   }

   public long getOsCategoryName() {
      return osCategoryName;
   }

   public long getPodId() {
      return podId;
   }

   public String getPodName() {
      return podName;
   }

   public Date getRemoved() {
      return removed;
   }

   public State getState() {
      return state;
   }

   public Type getType() {
      return type;
   }

   public String getVersion() {
      return version;
   }

   public long getZoneId() {
      return zoneId;
   }

   public String getZoneName() {
      return zoneName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Host that = (Host) o;

      if (!Objects.equal(averageLoad, that.averageLoad)) return false;
      if (!Objects.equal(clusterId, that.clusterId)) return false;
      if (!Objects.equal(cpuNumber, that.cpuNumber)) return false;
      if (!Objects.equal(cpuSpeed, that.cpuSpeed)) return false;
      if (!Objects.equal(cpuWithOverProvisioning, that.cpuWithOverProvisioning)) return false;
      if (!Objects.equal(diskSizeAllocated, that.diskSizeAllocated)) return false;
      if (!Objects.equal(diskSizeTotal, that.diskSizeTotal)) return false;
      if (!Objects.equal(hasEnoughCapacity, that.hasEnoughCapacity)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(jobId, that.jobId)) return false;
      if (!Objects.equal(localStorageActive, that.localStorageActive)) return false;
      if (!Objects.equal(managementServerId, that.managementServerId)) return false;
      if (!Objects.equal(memoryAllocated, that.memoryAllocated)) return false;
      if (!Objects.equal(memoryTotal, that.memoryTotal)) return false;
      if (!Objects.equal(memoryUsed, that.memoryUsed)) return false;
      if (!Objects.equal(networkKbsRead, that.networkKbsRead)) return false;
      if (!Objects.equal(networkKbsWrite, that.networkKbsWrite)) return false;
      if (!Objects.equal(osCategoryId, that.osCategoryId)) return false;
      if (!Objects.equal(osCategoryName, that.osCategoryName)) return false;
      if (!Objects.equal(podId, that.podId)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(allocationState, that.allocationState)) return false;
      if (!Objects.equal(capabilities, that.capabilities)) return false;
      if (!Objects.equal(clusterName, that.clusterName)) return false;
      if (!Objects.equal(clusterType, that.clusterType)) return false;
      if (!Objects.equal(cpuAllocated, that.cpuAllocated)) return false;
      if (!Objects.equal(cpuUsed, that.cpuUsed)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(disconnected, that.disconnected)) return false;
      if (!Objects.equal(events, that.events)) return false;
      if (!Objects.equal(hostTags, that.hostTags)) return false;
      if (!Objects.equal(hypervisor, that.hypervisor)) return false;
      if (!Objects.equal(ipAddress, that.ipAddress)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;
      if (!Objects.equal(lastPinged, that.lastPinged)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(podName, that.podName)) return false;
      if (!Objects.equal(removed, that.removed)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(type, that.type)) return false;
      if (!Objects.equal(version, that.version)) return false;
      if (!Objects.equal(zoneName, that.zoneName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(averageLoad, clusterId, cpuNumber, cpuSpeed, cpuWithOverProvisioning, diskSizeAllocated,
                               diskSizeTotal, hasEnoughCapacity, id, jobId, localStorageActive, managementServerId,
                               memoryAllocated, memoryTotal, memoryUsed, networkKbsRead, networkKbsWrite, osCategoryId,
                               osCategoryName, podId, zoneId, allocationState, capabilities, clusterName, clusterType,
                               cpuAllocated, cpuUsed, created, disconnected, events, hostTags, hypervisor, ipAddress,
                               jobStatus, lastPinged, name, podName, removed, state, type, version, zoneName);
   }
   
   @Override
   public String toString() {
      return "Host{" +
         "id=" + id +
         ", allocationState=" + allocationState +
         ", averageLoad=" + averageLoad +
         ", capabilities='" + capabilities + '\'' +
         ", clusterId=" + clusterId +
         ", clusterName='" + clusterName + '\'' +
         ", clusterType=" + clusterType +
         ", cpuAllocated='" + cpuAllocated + '\'' +
         ", cpuNumber=" + cpuNumber +
         ", cpuSpeed=" + cpuSpeed +
         ", cpuUsed='" + cpuUsed + '\'' +
         ", cpuWithOverProvisioning=" + cpuWithOverProvisioning +
         ", created=" + created +
         ", disconnected=" + disconnected +
         ", diskSizeAllocated=" + diskSizeAllocated +
         ", diskSizeTotal=" + diskSizeTotal +
         ", events='" + events + '\'' +
         ", hasEnoughCapacity=" + hasEnoughCapacity +
         ", hostTags='" + hostTags + '\'' +
         ", hypervisor='" + hypervisor + '\'' +
         ", ipAddress='" + ipAddress + '\'' +
         ", localStorageActive=" + localStorageActive +
         ", jobId=" + jobId +
         ", jobStatus=" + jobStatus +
         ", lastPinged=" + lastPinged +
         ", managementServerId=" + managementServerId +
         ", memoryAllocated=" + memoryAllocated +
         ", memoryTotal=" + memoryTotal +
         ", memoryUsed=" + memoryUsed +
         ", name='" + name + '\'' +
         ", networkKbsRead=" + networkKbsRead +
         ", networkKbsWrite=" + networkKbsWrite +
         ", osCategoryId=" + osCategoryId +
         ", osCategoryName=" + osCategoryName +
         ", podId=" + podId +
         ", podName='" + podName + '\'' +
         ", removed=" + removed +
         ", state=" + state +
         ", type=" + type +
         ", version='" + version + '\'' +
         ", zoneId=" + zoneId +
         ", zoneName='" + zoneName + '\'' +
         '}';
   }

   @Override
   public int compareTo(Host other) {
      return Long.valueOf(this.getId()).compareTo(other.getId());
   }
}
