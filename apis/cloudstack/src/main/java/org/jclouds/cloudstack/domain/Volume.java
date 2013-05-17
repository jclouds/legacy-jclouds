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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * @author Vijay Kiran
 */
public class Volume {

   /**
    */
   public static enum State {

      /**
       * indicates that the volume record is created in the DB, but not on the backend
       */
      ALLOCATED,
      /**
       * the volume is being created on the backend
       */
      CREATING,
      /**
       * the volume is ready to be used
       */
      READY,
      /**
       * the volume is destroyed (either as a result of deleteVolume command for DataDisk or as a part of destroyVm)
       */
      DESTROYED,
      /**
       * the volume has failed somehow, e.g. during creation (in cloudstack development)
       */
      FAILED,

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

   /**
    */
   public static enum Type {
      ROOT(0),
      DATADISK(1),
      UNRECOGNIZED(Integer.MAX_VALUE);

      private int code;

      private static final Map<Integer, Type> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(Type.values()),
            new Function<Type, Integer>() {

               @Override
               public Integer apply(Type input) {
                  return input.code;
               }

            });

      Type(int code) {
         this.code = code;
      }

      @Override
      public String toString() {
         return name().toLowerCase();
      }

      public static Type fromValue(String resourceType) {
         Integer code = Integer.valueOf(checkNotNull(resourceType, "resourcetype"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVolume(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected Date attached;
      protected Date created;
      protected boolean destroyed;
      protected String deviceId;
      protected String diskOfferingDisplayText;
      protected String diskOfferingId;
      protected String diskOfferingName;
      protected String domain;
      protected String domainId;
      protected String hypervisor;
      protected boolean isExtractable;
      protected String jobId;
      protected String jobStatus;
      protected String name;
      protected String serviceOfferingDisplayText;
      protected String serviceOfferingId;
      protected String serviceOfferingName;
      protected long size;
      protected String snapshotId;
      protected Volume.State state;
      protected String storage;
      protected String storageType;
      protected Volume.Type type;
      protected String virtualMachineId;
      protected String vmDisplayName;
      protected String vmName;
      protected VirtualMachine.State vmState;
      protected String zoneId;
      protected String zoneName;

      /**
       * @see Volume#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Volume#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see Volume#getAttached()
       */
      public T attached(Date attached) {
         this.attached = attached;
         return self();
      }

      /**
       * @see Volume#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see Volume#isDestroyed()
       */
      public T destroyed(boolean destroyed) {
         this.destroyed = destroyed;
         return self();
      }

      /**
       * @see Volume#getDeviceId()
       */
      public T deviceId(String deviceId) {
         this.deviceId = deviceId;
         return self();
      }

      /**
       * @see Volume#getDiskOfferingDisplayText()
       */
      public T diskOfferingDisplayText(String diskOfferingDisplayText) {
         this.diskOfferingDisplayText = diskOfferingDisplayText;
         return self();
      }

      /**
       * @see Volume#getDiskOfferingId()
       */
      public T diskOfferingId(String diskOfferingId) {
         this.diskOfferingId = diskOfferingId;
         return self();
      }

      /**
       * @see Volume#getDiskOfferingName()
       */
      public T diskOfferingName(String diskOfferingName) {
         this.diskOfferingName = diskOfferingName;
         return self();
      }

      /**
       * @see Volume#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Volume#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Volume#getHypervisor()
       */
      public T hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return self();
      }

      /**
       * @see Volume#isExtractable()
       */
      public T isExtractable(boolean isExtractable) {
         this.isExtractable = isExtractable;
         return self();
      }

      /**
       * @see Volume#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see Volume#getJobStatus()
       */
      public T jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      /**
       * @see Volume#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Volume#getServiceOfferingDisplayText()
       */
      public T serviceOfferingDisplayText(String serviceOfferingDisplayText) {
         this.serviceOfferingDisplayText = serviceOfferingDisplayText;
         return self();
      }

      /**
       * @see Volume#getServiceOfferingId()
       */
      public T serviceOfferingId(String serviceOfferingId) {
         this.serviceOfferingId = serviceOfferingId;
         return self();
      }

      /**
       * @see Volume#getServiceOfferingName()
       */
      public T serviceOfferingName(String serviceOfferingName) {
         this.serviceOfferingName = serviceOfferingName;
         return self();
      }

      /**
       * @see Volume#getSize()
       */
      public T size(long size) {
         this.size = size;
         return self();
      }

      /**
       * @see Volume#getSnapshotId()
       */
      public T snapshotId(String snapshotId) {
         this.snapshotId = snapshotId;
         return self();
      }

      /**
       * @see Volume#getState()
       */
      public T state(Volume.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see Volume#getStorage()
       */
      public T storage(String storage) {
         this.storage = storage;
         return self();
      }

      /**
       * @see Volume#getStorageType()
       */
      public T storageType(String storageType) {
         this.storageType = storageType;
         return self();
      }

      /**
       * @see Volume#getType()
       */
      public T type(Volume.Type type) {
         this.type = type;
         return self();
      }

      /**
       * @see Volume#getVirtualMachineId()
       */
      public T virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return self();
      }

      /**
       * @see Volume#getVmDisplayName()
       */
      public T vmDisplayName(String vmDisplayName) {
         this.vmDisplayName = vmDisplayName;
         return self();
      }

      /**
       * @see Volume#getVmName()
       */
      public T vmName(String vmName) {
         this.vmName = vmName;
         return self();
      }

      /**
       * @see Volume#getVmState()
       */
      public T vmState(VirtualMachine.State vmState) {
         this.vmState = vmState;
         return self();
      }

      /**
       * @see Volume#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see Volume#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      public Volume build() {
         return new Volume(id, account, attached, created, destroyed, deviceId, diskOfferingDisplayText, diskOfferingId, diskOfferingName, domain, domainId, hypervisor, isExtractable, jobId, jobStatus, name, serviceOfferingDisplayText, serviceOfferingId, serviceOfferingName, size, snapshotId, state, storage, storageType, type, virtualMachineId, vmDisplayName, vmName, vmState, zoneId, zoneName);
      }

      public T fromVolume(Volume in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .attached(in.getAttached())
               .created(in.getCreated())
               .destroyed(in.isDestroyed())
               .deviceId(in.getDeviceId())
               .diskOfferingDisplayText(in.getDiskOfferingDisplayText())
               .diskOfferingId(in.getDiskOfferingId())
               .diskOfferingName(in.getDiskOfferingName())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .hypervisor(in.getHypervisor())
               .isExtractable(in.isExtractable())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus())
               .name(in.getName())
               .serviceOfferingDisplayText(in.getServiceOfferingDisplayText())
               .serviceOfferingId(in.getServiceOfferingId())
               .serviceOfferingName(in.getServiceOfferingName())
               .size(in.getSize())
               .snapshotId(in.getSnapshotId())
               .state(in.getState())
               .storage(in.getStorage())
               .storageType(in.getStorageType())
               .type(in.getType())
               .virtualMachineId(in.getVirtualMachineId())
               .vmDisplayName(in.getVmDisplayName())
               .vmName(in.getVmName())
               .vmState(in.getVmState())
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
   private final String account;
   private final Date attached;
   private final Date created;
   private final boolean destroyed;
   private final String deviceId;
   private final String diskOfferingDisplayText;
   private final String diskOfferingId;
   private final String diskOfferingName;
   private final String domain;
   private final String domainId;
   private final String hypervisor;
   private final boolean isExtractable;
   private final String jobId;
   private final String jobStatus;
   private final String name;
   private final String serviceOfferingDisplayText;
   private final String serviceOfferingId;
   private final String serviceOfferingName;
   private final long size;
   private final String snapshotId;
   private final Volume.State state;
   private final String storage;
   private final String storageType;
   private final Volume.Type type;
   private final String virtualMachineId;
   private final String vmDisplayName;
   private final String vmName;
   private final VirtualMachine.State vmState;
   private final String zoneId;
   private final String zoneName;

   @ConstructorProperties({
         "id", "account", "attached", "created", "destroyed", "deviceid", "diskofferingdisplaytext", "diskofferingid", "diskofferingname", "domain", "domainid", "hypervisor", "isextractable", "jobid", "jobstatus", "name", "serviceofferingdisplaytext", "serviceofferingid", "serviceofferingname", "size", "snapshotid", "state", "storage", "storagetype", "type", "virtualmachineid", "vmdisplayname", "vmname", "vmstate", "zoneid", "zonename"
   })
   protected Volume(String id, @Nullable String account, @Nullable Date attached, @Nullable Date created, boolean destroyed,
                    @Nullable String deviceId, @Nullable String diskOfferingDisplayText, @Nullable String diskOfferingId,
                    @Nullable String diskOfferingName, @Nullable String domain, @Nullable String domainId, @Nullable String hypervisor,
                    boolean isExtractable, @Nullable String jobId, @Nullable String jobStatus, @Nullable String name,
                    @Nullable String serviceOfferingDisplayText, @Nullable String serviceOfferingId, @Nullable String serviceOfferingName,
                    long size, @Nullable String snapshotId, @Nullable Volume.State state, @Nullable String storage,
                    @Nullable String storageType, @Nullable Volume.Type type, @Nullable String virtualMachineId,
                    @Nullable String vmDisplayName, @Nullable String vmName, @Nullable VirtualMachine.State vmState,
                    @Nullable String zoneId, @Nullable String zoneName) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.attached = attached;
      this.created = created;
      this.destroyed = destroyed;
      this.deviceId = deviceId;
      this.diskOfferingDisplayText = diskOfferingDisplayText;
      this.diskOfferingId = diskOfferingId;
      this.diskOfferingName = diskOfferingName;
      this.domain = domain;
      this.domainId = domainId;
      this.hypervisor = hypervisor;
      this.isExtractable = isExtractable;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
      this.name = name;
      this.serviceOfferingDisplayText = serviceOfferingDisplayText;
      this.serviceOfferingId = serviceOfferingId;
      this.serviceOfferingName = serviceOfferingName;
      this.size = size;
      this.snapshotId = snapshotId;
      this.state = state;
      this.storage = storage;
      this.storageType = storageType;
      this.type = type;
      this.virtualMachineId = virtualMachineId;
      this.vmDisplayName = vmDisplayName;
      this.vmName = vmName;
      this.vmState = vmState;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public String getAccount() {
      return this.account;
   }

   @Nullable
   public Date getAttached() {
      return this.attached;
   }

   @Nullable
   public Date getCreated() {
      return this.created;
   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   @Nullable
   public String getDeviceId() {
      return this.deviceId;
   }

   @Nullable
   public String getDiskOfferingDisplayText() {
      return this.diskOfferingDisplayText;
   }

   @Nullable
   public String getDiskOfferingId() {
      return this.diskOfferingId;
   }

   @Nullable
   public String getDiskOfferingName() {
      return this.diskOfferingName;
   }

   @Nullable
   public String getDomain() {
      return this.domain;
   }

   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   @Nullable
   public String getHypervisor() {
      return this.hypervisor;
   }

   public boolean isExtractable() {
      return this.isExtractable;
   }

   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   @Nullable
   public String getJobStatus() {
      return this.jobStatus;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getServiceOfferingDisplayText() {
      return this.serviceOfferingDisplayText;
   }

   @Nullable
   public String getServiceOfferingId() {
      return this.serviceOfferingId;
   }

   @Nullable
   public String getServiceOfferingName() {
      return this.serviceOfferingName;
   }

   public long getSize() {
      return this.size;
   }

   @Nullable
   public String getSnapshotId() {
      return this.snapshotId;
   }

   @Nullable
   public Volume.State getState() {
      return this.state;
   }

   @Nullable
   public String getStorage() {
      return this.storage;
   }

   @Nullable
   public String getStorageType() {
      return this.storageType;
   }

   @Nullable
   public Volume.Type getType() {
      return this.type;
   }

   @Nullable
   public String getVirtualMachineId() {
      return this.virtualMachineId;
   }

   @Nullable
   public String getVmDisplayName() {
      return this.vmDisplayName;
   }

   @Nullable
   public String getVmName() {
      return this.vmName;
   }

   @Nullable
   public VirtualMachine.State getVmState() {
      return this.vmState;
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
      return Objects.hashCode(id, account, attached, created, destroyed, deviceId, diskOfferingDisplayText, diskOfferingId, diskOfferingName, domain, domainId, hypervisor, isExtractable, jobId, jobStatus, name, serviceOfferingDisplayText, serviceOfferingId, serviceOfferingName, size, snapshotId, state, storage, storageType, type, virtualMachineId, vmDisplayName, vmName, vmState, zoneId, zoneName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Volume that = Volume.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.attached, that.attached)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.destroyed, that.destroyed)
            && Objects.equal(this.deviceId, that.deviceId)
            && Objects.equal(this.diskOfferingDisplayText, that.diskOfferingDisplayText)
            && Objects.equal(this.diskOfferingId, that.diskOfferingId)
            && Objects.equal(this.diskOfferingName, that.diskOfferingName)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.hypervisor, that.hypervisor)
            && Objects.equal(this.isExtractable, that.isExtractable)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.serviceOfferingDisplayText, that.serviceOfferingDisplayText)
            && Objects.equal(this.serviceOfferingId, that.serviceOfferingId)
            && Objects.equal(this.serviceOfferingName, that.serviceOfferingName)
            && Objects.equal(this.size, that.size)
            && Objects.equal(this.snapshotId, that.snapshotId)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.storage, that.storage)
            && Objects.equal(this.storageType, that.storageType)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.virtualMachineId, that.virtualMachineId)
            && Objects.equal(this.vmDisplayName, that.vmDisplayName)
            && Objects.equal(this.vmName, that.vmName)
            && Objects.equal(this.vmState, that.vmState)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("attached", attached).add("created", created).add("destroyed", destroyed)
            .add("deviceId", deviceId).add("diskOfferingDisplayText", diskOfferingDisplayText).add("diskOfferingId", diskOfferingId)
            .add("diskOfferingName", diskOfferingName).add("domain", domain).add("domainId", domainId).add("hypervisor", hypervisor)
            .add("isExtractable", isExtractable).add("jobId", jobId).add("jobStatus", jobStatus).add("name", name)
            .add("serviceOfferingDisplayText", serviceOfferingDisplayText).add("serviceOfferingId", serviceOfferingId)
            .add("serviceOfferingName", serviceOfferingName).add("size", size).add("snapshotId", snapshotId).add("state", state)
            .add("storage", storage).add("storageType", storageType).add("type", type).add("virtualMachineId", virtualMachineId)
            .add("vmDisplayName", vmDisplayName).add("vmName", vmName).add("vmState", vmState).add("zoneId", zoneId).add("zoneName", zoneName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
