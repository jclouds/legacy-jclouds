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

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Map;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

/**
 * Represents a host issued by Cloudstack
 *
 * @author Andrei Savu
 */
public class Host implements Comparable<Host> {

   public static enum AllocationState {
      DISABLED,
      ENABLED,
      UNKNOWN;

      public static AllocationState fromValue(String value) {
         try{
            return valueOf(value.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
      }
   }

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
      CREATING,
      ENABLED,
      DISABLED,
      PREPARE_FOR_MAINTENANCE,
      ERROR_IN_MAINTENANCE,
      MAINTENANCE,
      ERROR,
      UP,      // seen in response - waiting from confirmation by cloud.com
      ALERT,   // seen in response - waiting from confirmation cloud.com
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
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (allocationState != null ? allocationState.hashCode() : 0);
      result = 31 * result + averageLoad;
      result = 31 * result + (capabilities != null ? capabilities.hashCode() : 0);
      result = 31 * result + (int) (clusterId ^ (clusterId >>> 32));
      result = 31 * result + (clusterName != null ? clusterName.hashCode() : 0);
      result = 31 * result + (clusterType != null ? clusterType.hashCode() : 0);
      result = 31 * result + (cpuAllocated != null ? cpuAllocated.hashCode() : 0);
      result = 31 * result + cpuNumber;
      result = 31 * result + cpuSpeed;
      result = 31 * result + (cpuUsed != null ? cpuUsed.hashCode() : 0);
      result = 31 * result + (int) cpuWithOverProvisioning;
      result = 31 * result + (created != null ? created.hashCode() : 0);
      result = 31 * result + (disconnected != null ? disconnected.hashCode() : 0);
      result = 31 * result + (int) (diskSizeAllocated ^ (diskSizeAllocated >>> 32));
      result = 31 * result + (int) (diskSizeTotal ^ (diskSizeTotal >>> 32));
      result = 31 * result + (events != null ? events.hashCode() : 0);
      result = 31 * result + (hasEnoughCapacity ? 1 : 0);
      result = 31 * result + (hostTags != null ? hostTags.hashCode() : 0);
      result = 31 * result + (hypervisor != null ? hypervisor.hashCode() : 0);
      result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
      result = 31 * result + (localStorageActive ? 1 : 0);
      result = 31 * result + (int) (jobId ^ (jobId >>> 32));
      result = 31 * result + (jobStatus != null ? jobStatus.hashCode() : 0);
      result = 31 * result + (lastPinged != null ? lastPinged.hashCode() : 0);
      result = 31 * result + (int) (managementServerId ^ (managementServerId >>> 32));
      result = 31 * result + (int) (memoryAllocated ^ (memoryAllocated >>> 32));
      result = 31 * result + (int) (memoryTotal ^ (memoryTotal >>> 32));
      result = 31 * result + (int) (memoryUsed ^ (memoryUsed >>> 32));
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (int) (networkKbsRead ^ (networkKbsRead >>> 32));
      result = 31 * result + (int) (networkKbsWrite ^ (networkKbsWrite >>> 32));
      result = 31 * result + (int) (osCategoryId ^ (osCategoryId >>> 32));
      result = 31 * result + (int) (osCategoryName ^ (osCategoryName >>> 32));
      result = 31 * result + (int) (podId ^ (podId >>> 32));
      result = 31 * result + (podName != null ? podName.hashCode() : 0);
      result = 31 * result + (removed != null ? removed.hashCode() : 0);
      result = 31 * result + (state != null ? state.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      result = 31 * result + (version != null ? version.hashCode() : 0);
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (zoneName != null ? zoneName.hashCode() : 0);
      return result;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Host host = (Host) o;

      if (averageLoad != host.averageLoad) return false;
      if (clusterId != host.clusterId) return false;
      if (cpuAllocated != host.cpuAllocated) return false;
      if (cpuNumber != host.cpuNumber) return false;
      if (cpuSpeed != host.cpuSpeed) return false;
      if (cpuUsed != host.cpuUsed) return false;
      if (cpuWithOverProvisioning != host.cpuWithOverProvisioning) return false;
      if (disconnected != host.disconnected) return false;
      if (diskSizeAllocated != host.diskSizeAllocated) return false;
      if (diskSizeTotal != host.diskSizeTotal) return false;
      if (hasEnoughCapacity != host.hasEnoughCapacity) return false;
      if (id != host.id) return false;
      if (localStorageActive != host.localStorageActive) return false;
      if (jobId != host.jobId) return false;
      if (managementServerId != host.managementServerId) return false;
      if (memoryAllocated != host.memoryAllocated) return false;
      if (memoryTotal != host.memoryTotal) return false;
      if (memoryUsed != host.memoryUsed) return false;
      if (networkKbsRead != host.networkKbsRead) return false;
      if (networkKbsWrite != host.networkKbsWrite) return false;
      if (osCategoryId != host.osCategoryId) return false;
      if (osCategoryName != host.osCategoryName) return false;
      if (podId != host.podId) return false;
      if (zoneId != host.zoneId) return false;
      if (allocationState != null ? !allocationState.equals(host.allocationState) : host.allocationState != null)
         return false;
      if (capabilities != null ? !capabilities.equals(host.capabilities) : host.capabilities != null)
         return false;
      if (clusterName != null ? !clusterName.equals(host.clusterName) : host.clusterName != null)
         return false;
      if (clusterType != null ? !clusterType.equals(host.clusterType) : host.clusterType != null)
         return false;
      if (created != null ? !created.equals(host.created) : host.created != null)
         return false;
      if (events != null ? !events.equals(host.events) : host.events != null)
         return false;
      if (hostTags != null ? !hostTags.equals(host.hostTags) : host.hostTags != null)
         return false;
      if (hypervisor != null ? !hypervisor.equals(host.hypervisor) : host.hypervisor != null)
         return false;
      if (ipAddress != null ? !ipAddress.equals(host.ipAddress) : host.ipAddress != null)
         return false;
      if (jobStatus != host.jobStatus) return false;
      if (lastPinged != null ? !lastPinged.equals(host.lastPinged) : host.lastPinged != null)
         return false;
      if (name != null ? !name.equals(host.name) : host.name != null)
         return false;
      if (podName != null ? !podName.equals(host.podName) : host.podName != null)
         return false;
      if (removed != null ? !removed.equals(host.removed) : host.removed != null)
         return false;
      if (state != null ? !state.equals(host.state) : host.state != null)
         return false;
      if (type != null ? !type.equals(host.type) : host.type != null)
         return false;
      if (version != null ? !version.equals(host.version) : host.version != null)
         return false;
      if (zoneName != null ? !zoneName.equals(host.zoneName) : host.zoneName != null)
         return false;

      return true;
   }

   @Override
   public String toString() {
      return "Host{" +
         "id=" + id +
         ", allocationState='" + allocationState + '\'' +
         ", averageLoad=" + averageLoad +
         ", capabilities='" + capabilities + '\'' +
         ", clusterId=" + clusterId +
         ", clusterName='" + clusterName + '\'' +
         ", clusterType='" + clusterType + '\'' +
         ", cpuAllocated=" + cpuAllocated +
         ", cpuNumber=" + cpuNumber +
         ", cpuSpeed=" + cpuSpeed +
         ", cpuUsed=" + cpuUsed +
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
         ", state='" + state + '\'' +
         ", type='" + type + '\'' +
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
