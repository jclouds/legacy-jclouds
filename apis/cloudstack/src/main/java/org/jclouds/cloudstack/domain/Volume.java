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

      private long id;
      private String account;
      private Date attached;
      private Date created;
      private boolean destroyed;
      private long deviceId;
      private String diskOfferingDisplayText;
      private long diskOfferingId;
      private String diskOfferingName;
      private String domain;
      private long domainId;
      private String hypervisor;
      private boolean isExtractable;
      private long jobId;
      private String jobStatus;
      private String name;
      private String serviceOfferingDisplayText;
      private long serviceOfferingId;
      private String serviceOfferingName;
      private long size;
      private long snapshotId;
      private State state;
      private String storage;
      private String storageType;
      private VolumeType type;
      private long virtualMachineId;
      private String vmDisplayName;
      private String vmName;
      private VirtualMachine.State vmState;
      private long zoneId;
      private String zoneName;

      public Builder id(long id) {
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

      public Builder deviceId(long deviceId) {
         this.deviceId = deviceId;
         return this;
      }

      public Builder diskOfferingDisplayText(String diskOfferingDisplayText) {
         this.diskOfferingDisplayText = diskOfferingDisplayText;
         return this;
      }

      public Builder diskOfferingId(long diskOfferingId) {
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

      public Builder domainId(long domainId) {
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

      public Builder jobId(long jobId) {
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

      public Builder serviceOfferingId(long serviceOfferingId) {
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

      public Builder snapshotId(long snapshotId) {
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

      public Builder type(VolumeType type) {
         this.type = type;
         return this;
      }

      public Builder virtualMachineId(long virtualMachineId) {
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

      public Builder zoneId(long zoneId) {
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

   private long id;
   private String account;
   private Date attached;
   private Date created;
   private boolean destroyed;
   @SerializedName("deviceid")
   private long deviceId;
   @SerializedName("diskofferingdisplaytext")
   private String diskOfferingDisplayText;
   @SerializedName("diskofferingid")
   private long diskOfferingId;
   @SerializedName("diskofferingname")
   private String diskOfferingName;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   private String hypervisor;
   @SerializedName("isextractable")
   private boolean isExtractable;
   @SerializedName("jobid")
   private long jobId;
   @SerializedName("jobstatus")
   private String jobStatus;
   private String name;
   @SerializedName("serviceofferingdisplaytext")
   private String serviceOfferingDisplayText;
   @SerializedName("serviceofferingid")
   private long serviceOfferingId;
   @SerializedName("serviceofferingname")
   private String serviceOfferingName;
   private long size;
   @SerializedName("snapshotid")
   private long snapshotId;
   private State state;
   private String storage;
   @SerializedName("storagetype")
   // MAYDO: this should perhaps be an enum; only value I have seen is "shared"
   private String storageType;
   private VolumeType type;
   @SerializedName("virtualmachineid")
   private long virtualMachineId;
   @SerializedName("vmdisplayname")
   private String vmDisplayName;
   @SerializedName("vmname")
   private String vmName;
   @SerializedName("vmstate")
   private VirtualMachine.State vmState;
   @SerializedName("zoneid")
   private long zoneId;
   @SerializedName("zonename")
   private String zoneName;

   public Volume(long id,String account,  Date attached, Date created, boolean destroyed, long deviceId,
                 String diskOfferingDisplayText, long diskOfferingId, String diskOfferingName,
                 String domain, long domainId, String hypervisor, boolean extractable, long jobId,
                 String jobStatus, String name, String serviceOfferingDisplayText, long serviceOfferingId,
                 String serviceOfferingName, long size, long snapshotId, State state, String storage,
                 String storageType, VolumeType type, long virtualMachineId, String vmDisplayName, String vmName,
                 VirtualMachine.State vmState, long zoneId, String zoneName) {
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

   public long getId() {
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

   public long getDeviceId() {
      return deviceId;
   }

   public String getDiskOfferingDisplayText() {
      return diskOfferingDisplayText;
   }

   public long getDiskOfferingId() {
      return diskOfferingId;
   }

   public String getDiskOfferingName() {
      return diskOfferingName;
   }

   public String getDomain() {
      return domain;
   }

   public long getDomainId() {
      return domainId;
   }

   public String getHypervisor() {
      return hypervisor;
   }

   public boolean isExtractable() {
      return isExtractable;
   }

   public long getJobId() {
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

   public long getServiceOfferingId() {
      return serviceOfferingId;
   }

   public String getServiceOfferingName() {
      return serviceOfferingName;
   }

   public long getSize() {
      return size;
   }

   public long getSnapshotId() {
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

   public VolumeType getType() {
      return type;
   }

   public long getVirtualMachineId() {
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

   public long getZoneId() {
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
      return Long.valueOf(this.id).compareTo(volume.id);
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Volume volume = (Volume) o;

      if (deviceId != volume.deviceId) return false;

      if (diskOfferingId != volume.diskOfferingId) return false;
      if (domainId != volume.domainId) return false;
      if (id != volume.id) return false;
      if (isExtractable != volume.isExtractable) return false;
      if (jobId != volume.jobId) return false;
      if (serviceOfferingId != volume.serviceOfferingId) return false;
      if (size != volume.size) return false;
      if (snapshotId != volume.snapshotId) return false;
      if (virtualMachineId != volume.virtualMachineId) return false;
      if (zoneId != volume.zoneId) return false;
      if (attached != null ? !attached.equals(volume.attached) : volume.attached != null) return false;
      if (!created.equals(volume.created)) return false;
      if (diskOfferingDisplayText != null ? !diskOfferingDisplayText.equals(volume.diskOfferingDisplayText) :
            volume.diskOfferingDisplayText != null)
         return false;
      if (diskOfferingName != null ? !diskOfferingName.equals(volume.diskOfferingName) : volume.diskOfferingName != null)
         return false;
      if (!domain.equals(volume.domain)) return false;
      if (hypervisor != null ? !hypervisor.equals(volume.hypervisor) : volume.hypervisor != null) return false;
      if (jobStatus != null ? !jobStatus.equals(volume.jobStatus) : volume.jobStatus != null) return false;
      if (name != null ? !name.equals(volume.name) : volume.name != null) return false;
      if (serviceOfferingDisplayText != null ? !serviceOfferingDisplayText.equals(volume.serviceOfferingDisplayText) :
            volume.serviceOfferingDisplayText != null)
         return false;
      if (serviceOfferingName != null ? !serviceOfferingName.equals(volume.serviceOfferingName) :
            volume.serviceOfferingName != null)
         return false;
      if (state != null ? !state.equals(volume.state) : volume.state != null) return false;
      if (storage != null ? !storage.equals(volume.storage) : volume.storage != null) return false;
      if (storageType != null ? !storageType.equals(volume.storageType) : volume.storageType != null) return false;
      if (type != null ? !type.equals(volume.type) : volume.type != null) return false;
      if (vmDisplayName != null ? !vmDisplayName.equals(volume.vmDisplayName) : volume.vmDisplayName != null)
         return false;
      if (vmName != null ? !vmName.equals(volume.vmName) : volume.vmName != null) return false;
      if (vmState != volume.vmState) return false;
      if (zoneName != null ? !zoneName.equals(volume.zoneName) : volume.zoneName != null) return false;
      if (account == null) {
         if (volume.account != null)
            return false;
      } else if (!account.equals(volume.account))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (attached != null ? attached.hashCode() : 0);
      result = 31 * result + created.hashCode();
      result = 31 * result + (int) (deviceId ^ (deviceId >>> 32));
      result = 31 * result + (diskOfferingDisplayText != null ? diskOfferingDisplayText.hashCode() : 0);
      result = 31 * result + (int) (diskOfferingId ^ (diskOfferingId >>> 32));
      result = 31 * result + (diskOfferingName != null ? diskOfferingName.hashCode() : 0);
      result = 31 * result + domain.hashCode();
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (hypervisor != null ? hypervisor.hashCode() : 0);
      result = 31 * result + (isExtractable ? 1 : 0);
      result = 31 * result + (int) (jobId ^ (jobId >>> 32));
      result = 31 * result + (jobStatus != null ? jobStatus.hashCode() : 0);
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (serviceOfferingDisplayText != null ? serviceOfferingDisplayText.hashCode() : 0);
      result = 31 * result + (int) (serviceOfferingId ^ (serviceOfferingId >>> 32));
      result = 31 * result + (serviceOfferingName != null ? serviceOfferingName.hashCode() : 0);
      result = 31 * result + (int) (size ^ (size >>> 32));
      result = 31 * result + (int) (snapshotId ^ (snapshotId >>> 32));
      result = 31 * result + (state != null ? state.hashCode() : 0);
      result = 31 * result + (storage != null ? storage.hashCode() : 0);
      result = 31 * result + (storageType != null ? storageType.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      result = 31 * result + (int) (virtualMachineId ^ (virtualMachineId >>> 32));
      result = 31 * result + (vmDisplayName != null ? vmDisplayName.hashCode() : 0);
      result = 31 * result + (vmName != null ? vmName.hashCode() : 0);
      result = 31 * result + (vmState != null ? vmState.hashCode() : 0);
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (zoneName != null ? zoneName.hashCode() : 0);
      return result;
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

   public enum VolumeType {
      ROOT(0),
      DATADISK(1),
      UNRECOGNIZED(Integer.MAX_VALUE);

      private int code;

      private static final Map<Integer, VolumeType> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(VolumeType.values()),
            new Function<VolumeType, Integer>() {

               @Override
               public Integer apply(VolumeType input) {
                  return input.code;
               }

            });

      VolumeType(int code) {
         this.code = code;
      }

      @Override
      public String toString() {
         return name();
      }

      public static VolumeType fromValue(String resourceType) {
         Integer code = new Integer(checkNotNull(resourceType, "resourcetype"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }

}
