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
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * @author Vijay Kiran
 */
public class Volume implements Comparable<Volume> {

   public static Builder builder() {
      return new Builder();
   }


   public static class Builder {

      private String id;
      private String account;
      private Date attached;
      private Date created;
      private boolean destroyed;
      private String deviceId;
      private String diskOfferingDisplayText;
      private String diskOfferingId;
      private String diskOfferingName;
      private String domain;
      private String domainId;
      private String hypervisor;
      private boolean isExtractable;
      private String jobId;
      private String jobStatus;
      private String name;
      private String serviceOfferingDisplayText;
      private String serviceOfferingId;
      private String serviceOfferingName;
      private long size;
      private String snapshotId;
      private State state;
      private String storage;
      private String storageType;
      private Type type;
      private String virtualMachineId;
      private String vmDisplayName;
      private String vmName;
      private VirtualMachine.State vmState;
      private String zoneId;
      private String zoneName;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder attached(Date attached) {
         this.attached = attached;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder destroyed(boolean destroyed) {
         this.destroyed = destroyed;
         return this;
      }

      public Builder deviceId(String deviceId) {
         this.deviceId = deviceId;
         return this;
      }

      public Builder diskOfferingDisplayText(String diskOfferingDisplayText) {
         this.diskOfferingDisplayText = diskOfferingDisplayText;
         return this;
      }

      public Builder diskOfferingId(String diskOfferingId) {
         this.diskOfferingId = diskOfferingId;
         return this;
      }

      public Builder diskOfferingName(String diskOfferingName) {
         this.diskOfferingName = diskOfferingName;
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

      public Builder hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      public Builder isExtractable(boolean isExtractable) {
         this.isExtractable = isExtractable;
         return this;
      }

      public Builder jobId(String jobId) {
         this.jobId = jobId;
         return this;
      }

      public Builder jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder serviceOfferingDisplayText(String serviceOfferingDisplayText) {
         this.serviceOfferingDisplayText = serviceOfferingDisplayText;
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

      public Builder size(long size) {
         this.size = size;
         return this;
      }

      public Builder snapshotId(String snapshotId) {
         this.snapshotId = snapshotId;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder storage(String storage) {
         this.storage = storage;
         return this;
      }

      public Builder storageType(String storageType) {
         this.storageType = storageType;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return this;
      }

      public Builder vmDisplayName(String vmDisplayName) {
         this.vmDisplayName = vmDisplayName;
         return this;
      }

      public Builder vmName(String vmName) {
         this.vmName = vmName;
         return this;
      }

      public Builder vmState(VirtualMachine.State vmState) {
         this.vmState = vmState;
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

      public Volume build() {
         return new Volume(id, account, attached, created, destroyed, deviceId, diskOfferingDisplayText, diskOfferingId,
               diskOfferingName, domain, domainId, hypervisor, isExtractable, jobId, jobStatus, name,
               serviceOfferingDisplayText, serviceOfferingId, serviceOfferingName, size, snapshotId, state, storage,
               storageType, type, virtualMachineId, vmDisplayName, vmName, vmState, zoneId, zoneName);
      }
   }

   private String id;
   private String account;
   private Date attached;
   private Date created;
   private boolean destroyed;
   @SerializedName("deviceid")
   private String deviceId;
   @SerializedName("diskofferingdisplaytext")
   private String diskOfferingDisplayText;
   @SerializedName("diskofferingid")
   private String diskOfferingId;
   @SerializedName("diskofferingname")
   private String diskOfferingName;
   private String domain;
   @SerializedName("domainid")
   private String domainId;
   private String hypervisor;
   @SerializedName("isextractable")
   private boolean isExtractable;
   @SerializedName("jobid")
   private String jobId;
   @SerializedName("jobstatus")
   private String jobStatus;
   private String name;
   @SerializedName("serviceofferingdisplaytext")
   private String serviceOfferingDisplayText;
   @SerializedName("serviceofferingid")
   private String serviceOfferingId;
   @SerializedName("serviceofferingname")
   private String serviceOfferingName;
   private long size;
   @SerializedName("snapshotid")
   private String snapshotId;
   private State state;
   private String storage;
   @SerializedName("storagetype")
   // MAYDO: this should perhaps be an enum; only value I have seen is "shared"
   private String storageType;
   private Type type;
   @SerializedName("virtualmachineid")
   private String virtualMachineId;
   @SerializedName("vmdisplayname")
   private String vmDisplayName;
   @SerializedName("vmname")
   private String vmName;
   @SerializedName("vmstate")
   private VirtualMachine.State vmState;
   @SerializedName("zoneid")
   private String zoneId;
   @SerializedName("zonename")
   private String zoneName;

   public Volume(String id,String account,  Date attached, Date created, boolean destroyed, String deviceId,
                 String diskOfferingDisplayText, String diskOfferingId, String diskOfferingName,
                 String domain, String domainId, String hypervisor, boolean extractable, String jobId,
                 String jobStatus, String name, String serviceOfferingDisplayText, String serviceOfferingId,
                 String serviceOfferingName, long size, String snapshotId, State state, String storage,
                 String storageType, Type type, String virtualMachineId, String vmDisplayName, String vmName,
                 VirtualMachine.State vmState, String zoneId, String zoneName) {
      this.id = id;
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
      isExtractable = extractable;
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

   // for deserialization
   Volume() {
   }

   public String getId() {
      return id;
   }

   public Date getAttached() {
      return attached;
   }

   public Date getCreated() {
      return created;
   }

   public boolean isDestroyed() {
      return destroyed;
   }

   public String getDeviceId() {
      return deviceId;
   }

   public String getDiskOfferingDisplayText() {
      return diskOfferingDisplayText;
   }

   public String getDiskOfferingId() {
      return diskOfferingId;
   }

   public String getDiskOfferingName() {
      return diskOfferingName;
   }

   public String getDomain() {
      return domain;
   }

   public String getDomainId() {
      return domainId;
   }

   public String getHypervisor() {
      return hypervisor;
   }

   public boolean isExtractable() {
      return isExtractable;
   }

   public String getJobId() {
      return jobId;
   }

   public String getJobStatus() {
      return jobStatus;
   }

   public String getName() {
      return name;
   }

   public String getServiceOfferingDisplayText() {
      return serviceOfferingDisplayText;
   }

   public String getServiceOfferingId() {
      return serviceOfferingId;
   }

   public String getServiceOfferingName() {
      return serviceOfferingName;
   }

   public long getSize() {
      return size;
   }

   public String getSnapshotId() {
      return snapshotId;
   }

   public State getState() {
      return state;
   }

   public String getStorage() {
      return storage;
   }

   public String getStorageType() {
      return storageType;
   }

   public Type getType() {
      return type;
   }

   public String getVirtualMachineId() {
      return virtualMachineId;
   }

   public String getVmDisplayName() {
      return vmDisplayName;
   }

   public String getVmName() {
      return vmName;
   }

   public VirtualMachine.State getVmState() {
      return vmState;
   }

   public String getZoneId() {
      return zoneId;
   }

   public String getZoneName() {
      return zoneName;
   }

   public String getAccount() {
      return account;
   }

   @Override
   public int compareTo(Volume volume) {
      return this.id.compareTo(volume.id);
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Volume that = (Volume) o;

      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(attached, that.attached)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(destroyed, that.destroyed)) return false;
      if (!Objects.equal(deviceId, that.deviceId)) return false;
      if (!Objects.equal(diskOfferingDisplayText, that.diskOfferingDisplayText)) return false;
      if (!Objects.equal(diskOfferingId, that.diskOfferingId)) return false;
      if (!Objects.equal(diskOfferingName, that.diskOfferingName)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(hypervisor, that.hypervisor)) return false;
      if (!Objects.equal(isExtractable, that.isExtractable)) return false;
      if (!Objects.equal(jobId, that.jobId)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(serviceOfferingDisplayText, that.serviceOfferingDisplayText)) return false;
      if (!Objects.equal(serviceOfferingId, that.serviceOfferingId)) return false;
      if (!Objects.equal(serviceOfferingName, that.serviceOfferingName)) return false;
      if (!Objects.equal(size, that.size)) return false;
      if (!Objects.equal(snapshotId, that.snapshotId)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(storage, that.storage)) return false;
      if (!Objects.equal(storageType, that.storageType)) return false;
      if (!Objects.equal(type, that.type)) return false;
      if (!Objects.equal(virtualMachineId, that.virtualMachineId)) return false;
      if (!Objects.equal(vmDisplayName, that.vmDisplayName)) return false;
      if (!Objects.equal(vmName, that.vmName)) return false;
      if (!Objects.equal(vmState, that.vmState)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(zoneName, that.zoneName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, account, attached, created, destroyed, deviceId, diskOfferingDisplayText,
                               diskOfferingId, diskOfferingName, domain, domainId, hypervisor,
                               isExtractable, jobId, jobStatus, name, serviceOfferingDisplayText,
                               serviceOfferingId, serviceOfferingName, size, snapshotId, state, storage,
                               storageType, type, virtualMachineId, vmDisplayName, vmName, vmState, zoneId,
                               zoneName);
   }

   @Override
   public String toString() {
      return "Volume{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", attached=" + attached +
            ", created=" + created +
            ", destroyed=" + destroyed +
            ", deviceId=" + deviceId +
            ", diskOfferingDisplayText='" + diskOfferingDisplayText + '\'' +
            ", diskOfferingId=" + diskOfferingId +
            ", diskOfferingName='" + diskOfferingName + '\'' +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", hypervisor='" + hypervisor + '\'' +
            ", isExtractable=" + isExtractable +
            ", jobId=" + jobId +
            ", jobStatus='" + jobStatus + '\'' +
            ", name='" + name + '\'' +
            ", serviceOfferingDisplayText='" + serviceOfferingDisplayText + '\'' +
            ", serviceOfferingId=" + serviceOfferingId +
            ", serviceOfferingName='" + serviceOfferingName + '\'' +
            ", size=" + size +
            ", snapshotId=" + snapshotId +
            ", state=" + state +
            ", storage='" + storage + '\'' +
            ", storageType='" + storageType + '\'' +
            ", type=" + type +
            ", virtualMachineId=" + virtualMachineId +
            ", vmDisplayName='" + vmDisplayName + '\'' +
            ", vmName='" + vmName + '\'' +
            ", vmState=" + vmState +
            ", zoneId=" + zoneId +
            ", zoneName='" + zoneName + '\'' +
            '}';
   }

   public enum State {

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

   public enum Type {
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
         Integer code = new Integer(checkNotNull(resourceType, "resourcetype"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }

}
