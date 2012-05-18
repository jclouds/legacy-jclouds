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

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a storage pool in CloudStack
 *
 * @author Richard Downer
 */
public class StoragePool implements Comparable<StoragePool> {

   public enum State {
      UP,
      PREPARE_FOR_MAINTENANCE,
      ERROR_IN_MAINTENANCE,
      CANCEL_MAINTENANCE,
      MAINTENANCE,
      REMOVED,
      UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static State fromValue(String type) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static enum Type {
      FILESYSTEM,
      NETWORK_FILESYSTEM,
      ISCSI_LUN,
      ISCSI,
      ISO,
      LVM,
      CLVM,
      SHARED_MOUNT_POINT,
      VMFS,
      PRE_SETUP,
      EXT,
      OCFS2,
      UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static Type fromValue(String type) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private Builder() {
      }

      private long id;
      private String name;
      private String path;
      private String tags;
      private State state;
      private Type type;
      private long zoneId;
      private String zoneName;
      private long podId;
      private String podName;
      private long clusterId;
      private String clusterName;
      private Date created;
      private long diskSizeAllocated;
      private long diskSizeTotal;
      private String ipAddress;
      private Long jobId;
      private String jobStatus;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder path(String path) {
         this.path = path;
         return this;
      }

      public Builder tags(String tags) {
         this.tags = tags;
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

      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
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

      public Builder clusterId(long clusterId) {
         this.clusterId = clusterId;
         return this;
      }

      public Builder clusterName(String clusterName) {
         this.clusterName = clusterName;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
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

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder jobId(Long jobId) {
         this.jobId = jobId;
         return this;
      }

      public Builder jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      public StoragePool build() {
         return new StoragePool(id, name, path, tags, state, type, zoneId, zoneName, podId, podName, clusterId, clusterName, created, diskSizeAllocated, diskSizeTotal, ipAddress, jobId, jobStatus);
      }
   }

   private long id;
   private String name;
   private String path;
   private String tags;
   private State state;
   private Type type;
   @SerializedName("zoneid") private long zoneId;
   @SerializedName("zonename") private String zoneName;
   @SerializedName("podid") private long podId;
   @SerializedName("podname") private String podName;
   @SerializedName("clusterid") private long clusterId;
   @SerializedName("clustername") private String clusterName;
   private Date created;
   @SerializedName("disksizeallocated") private long diskSizeAllocated;
   @SerializedName("disksizetotal") private long diskSizeTotal;
   @SerializedName("ipaddress") private String ipAddress;
   @SerializedName("jobid") private Long jobId;
   @SerializedName("jobstatus") private String jobStatus;

   /* Exists only for the serializer */
   StoragePool() {
   }

   public StoragePool(long id, String name, String path, String tags, State state, Type type, long zoneId, String zoneName, long podId, String podName, long clusterId, String clusterName, Date created, long diskSizeAllocated, long diskSizeTotal, String ipAddress, Long jobId, String jobStatus) {
      this.id = id;
      this.name = name;
      this.path = path;
      this.tags = tags;
      this.state = state;
      this.type = type;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
      this.podId = podId;
      this.podName = podName;
      this.clusterId = clusterId;
      this.clusterName = clusterName;
      this.created = created;
      this.diskSizeAllocated = diskSizeAllocated;
      this.diskSizeTotal = diskSizeTotal;
      this.ipAddress = ipAddress;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
   }

   public long getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getPath() {
      return path;
   }

   public String getTags() {
      return tags;
   }

   public State getState() {
      return state;
   }

   public Type getType() {
      return type;
   }

   public long getZoneId() {
      return zoneId;
   }

   public String getZoneName() {
      return zoneName;
   }

   public long getPodId() {
      return podId;
   }

   public String getPodName() {
      return podName;
   }

   public long getClusterId() {
      return clusterId;
   }

   public String getClusterName() {
      return clusterName;
   }

   public Date getCreated() {
      return created;
   }

   public long getDiskSizeAllocated() {
      return diskSizeAllocated;
   }

   public long getDiskSizeTotal() {
      return diskSizeTotal;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public Long getJobId() {
      return jobId;
   }

   public String getJobStatus() {
      return jobStatus;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StoragePool that = (StoragePool) o;

      if (!Objects.equal(clusterId, that.clusterId)) return false;
      if (!Objects.equal(diskSizeAllocated, that.diskSizeAllocated)) return false;
      if (!Objects.equal(diskSizeTotal, that.diskSizeTotal)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(podId, that.podId)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(clusterName, that.clusterName)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(ipAddress, that.ipAddress)) return false;
      if (!Objects.equal(jobId, that.jobId)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(path, that.path)) return false;
      if (!Objects.equal(podName, that.podName)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(tags, that.tags)) return false;
      if (!Objects.equal(type, that.type)) return false;
      if (!Objects.equal(zoneName, that.zoneName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(clusterId, diskSizeAllocated, diskSizeTotal, id, podId, zoneId,
                               clusterName, created, ipAddress, jobId, jobStatus, name, path,
                               podName, state, tags, type, zoneName);
   }

   @Override
   public String toString() {
      return "StoragePool{" +
         "id=" + id +
         ", name='" + name + '\'' +
         ", path='" + path + '\'' +
         ", tags='" + tags + '\'' +
         ", state=" + state +
         ", type=" + type +
         ", zoneId=" + zoneId +
         ", zoneName='" + zoneName + '\'' +
         ", podId=" + podId +
         ", podName='" + podName + '\'' +
         ", clusterId=" + clusterId +
         ", clusterName='" + clusterName + '\'' +
         ", created=" + created +
         ", diskSizeAllocated=" + diskSizeAllocated +
         ", diskSizeTotal=" + diskSizeTotal +
         ", ipAddress='" + ipAddress + '\'' +
         ", jobId=" + jobId +
         ", jobStatus='" + jobStatus + '\'' +
         '}';
   }

   @Override
   public int compareTo(StoragePool other) {
      return Long.valueOf(this.id).compareTo(other.id);
   }
}
