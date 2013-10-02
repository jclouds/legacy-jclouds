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
import com.google.common.collect.ImmutableSet;

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

   public enum Type {
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

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromStoragePool(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String path;
      protected ImmutableSet.Builder<String> tags = ImmutableSet.<String>builder();
      protected StoragePool.State state;
      protected StoragePool.Type type;
      protected String zoneId;
      protected String zoneName;
      protected String podId;
      protected String podName;
      protected String clusterId;
      protected String clusterName;
      protected Date created;
      protected long diskSizeAllocated;
      protected long diskSizeTotal;
      protected String ipAddress;
      protected String jobId;
      protected String jobStatus;

      /**
       * @see StoragePool#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see StoragePool#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see StoragePool#getPath()
       */
      public T path(String path) {
         this.path = path;
         return self();
      }

      /**
       * @see DiskOffering#getTags()
       */
      public T tags(Iterable<String> tags) {
         this.tags = ImmutableSet.<String>builder().addAll(tags);
         return self();
      }
      
      /**
       * @see DiskOffering#getTags()
       */
      public T tag(String tag) {
         this.tags.add(tag);
         return self();
      }

      /**
       * @see StoragePool#getState()
       */
      public T state(StoragePool.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see StoragePool#getType()
       */
      public T type(StoragePool.Type type) {
         this.type = type;
         return self();
      }

      /**
       * @see StoragePool#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see StoragePool#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      /**
       * @see StoragePool#getPodId()
       */
      public T podId(String podId) {
         this.podId = podId;
         return self();
      }

      /**
       * @see StoragePool#getPodName()
       */
      public T podName(String podName) {
         this.podName = podName;
         return self();
      }

      /**
       * @see StoragePool#getClusterId()
       */
      public T clusterId(String clusterId) {
         this.clusterId = clusterId;
         return self();
      }

      /**
       * @see StoragePool#getClusterName()
       */
      public T clusterName(String clusterName) {
         this.clusterName = clusterName;
         return self();
      }

      /**
       * @see StoragePool#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see StoragePool#getDiskSizeAllocated()
       */
      public T diskSizeAllocated(long diskSizeAllocated) {
         this.diskSizeAllocated = diskSizeAllocated;
         return self();
      }

      /**
       * @see StoragePool#getDiskSizeTotal()
       */
      public T diskSizeTotal(long diskSizeTotal) {
         this.diskSizeTotal = diskSizeTotal;
         return self();
      }

      /**
       * @see StoragePool#getIpAddress()
       */
      public T ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return self();
      }

      /**
       * @see StoragePool#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see StoragePool#getJobStatus()
       */
      public T jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      public StoragePool build() {
         return new StoragePool(id, name, path, tags.build(), state, type, zoneId, zoneName, podId, podName, clusterId, clusterName, created, diskSizeAllocated, diskSizeTotal, ipAddress, jobId, jobStatus);
      }

      public T fromStoragePool(StoragePool in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .path(in.getPath())
               .tags(in.getTags())
               .state(in.getState())
               .type(in.getType())
               .zoneId(in.getZoneId())
               .zoneName(in.getZoneName())
               .podId(in.getPodId())
               .podName(in.getPodName())
               .clusterId(in.getClusterId())
               .clusterName(in.getClusterName())
               .created(in.getCreated())
               .diskSizeAllocated(in.getDiskSizeAllocated())
               .diskSizeTotal(in.getDiskSizeTotal())
               .ipAddress(in.getIpAddress())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final String path;
   private final Set<String> tags;
   private final StoragePool.State state;
   private final StoragePool.Type type;
   private final String zoneId;
   private final String zoneName;
   private final String podId;
   private final String podName;
   private final String clusterId;
   private final String clusterName;
   private final Date created;
   private final long diskSizeAllocated;
   private final long diskSizeTotal;
   private final String ipAddress;
   private final String jobId;
   private final String jobStatus;

   @ConstructorProperties({
         "id", "name", "path", "tags", "state", "type", "zoneid", "zonename", "podid", "podname", "clusterid", "clustername", "created", "disksizeallocated", "disksizetotal", "ipaddress", "jobid", "jobstatus"
   })
   protected StoragePool(String id, @Nullable String name, @Nullable String path, @Nullable Iterable<String> tags,
            @Nullable StoragePool.State state, @Nullable StoragePool.Type type, @Nullable String zoneId,
            @Nullable String zoneName, @Nullable String podId, @Nullable String podName, @Nullable String clusterId,
            @Nullable String clusterName, @Nullable Date created, long diskSizeAllocated, long diskSizeTotal,
            @Nullable String ipAddress, @Nullable String jobId, @Nullable String jobStatus) {
      this.id = checkNotNull(id, "id");
      this.name = name;
      this.path = path;
      this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.<String> of();
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

   public String getId() {
      return this.id;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getPath() {
      return this.path;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   @Nullable
   public StoragePool.State getState() {
      return this.state;
   }

   @Nullable
   public StoragePool.Type getType() {
      return this.type;
   }

   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   @Nullable
   public String getZoneName() {
      return this.zoneName;
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
   public String getClusterId() {
      return this.clusterId;
   }

   @Nullable
   public String getClusterName() {
      return this.clusterName;
   }

   @Nullable
   public Date getCreated() {
      return this.created;
   }

   public long getDiskSizeAllocated() {
      return this.diskSizeAllocated;
   }

   public long getDiskSizeTotal() {
      return this.diskSizeTotal;
   }

   @Nullable
   public String getIpAddress() {
      return this.ipAddress;
   }

   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   @Nullable
   public String getJobStatus() {
      return this.jobStatus;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, path, tags, state, type, zoneId, zoneName, podId, podName, clusterId, clusterName, created, diskSizeAllocated, diskSizeTotal, ipAddress, jobId, jobStatus);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      StoragePool that = StoragePool.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.path, that.path)
            && Objects.equal(this.tags, that.tags)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName)
            && Objects.equal(this.podId, that.podId)
            && Objects.equal(this.podName, that.podName)
            && Objects.equal(this.clusterId, that.clusterId)
            && Objects.equal(this.clusterName, that.clusterName)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.diskSizeAllocated, that.diskSizeAllocated)
            && Objects.equal(this.diskSizeTotal, that.diskSizeTotal)
            && Objects.equal(this.ipAddress, that.ipAddress)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("path", path).add("tags", tags).add("state", state).add("type", type).add("zoneId", zoneId).add("zoneName", zoneName).add("podId", podId).add("podName", podName).add("clusterId", clusterId).add("clusterName", clusterName).add("created", created).add("diskSizeAllocated", diskSizeAllocated).add("diskSizeTotal", diskSizeTotal).add("ipAddress", ipAddress).add("jobId", jobId).add("jobStatus", jobStatus);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(StoragePool other) {
      return this.id.compareTo(other.id);
   }
}
