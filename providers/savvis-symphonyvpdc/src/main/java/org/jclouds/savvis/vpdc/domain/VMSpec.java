/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.compute.domain.CIMOperatingSystem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A specification to launch a virtual machine
 * 
 * @author Adrian Cole
 */
public class VMSpec {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private CIMOperatingSystem operatingSystem;
      private int processorCount = 1;
      private int memoryInGig = 1;
      private String bootDeviceName = "/";
      // TODO doesn't seem to be changeable
      private int bootDriveSize = 25;
      private Map<String, Integer> dataDriveDeviceNameToSizeInGig = Maps.newLinkedHashMap();

      public Builder operatingSystem(CIMOperatingSystem operatingSystem) {
         this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
         return this;
      }

      public Builder memoryInGig(int memoryInGig) {
         checkArgument(memoryInGig > 0, "memoryInGig must be positive");
         this.memoryInGig = memoryInGig;
         return this;
      }

      public Builder processorCount(int processorCount) {
         checkProcessorCount(processorCount);
         this.processorCount = processorCount;
         return this;
      }

      public Builder bootDeviceName(String bootDeviceName) {
         this.bootDeviceName = checkNotNull(bootDeviceName, "bootDeviceName");
         return this;
      }

      public Builder bootDiskSize(int bootDriveSize) {
         checkArgument(bootDriveSize > 0, "bootDriveSize must be positive");
         this.bootDriveSize = bootDriveSize;
         return this;
      }

      public Builder addDataDrive(String dataDriveDeviceName, int sizeInGig) {
         checkArgument(sizeInGig > 0, "sizeInGig must be positive");
         this.dataDriveDeviceNameToSizeInGig.put(checkNotNull(dataDriveDeviceName, "dataDriveDeviceName"), sizeInGig);
         return this;
      }

      public Builder addDataDrives(Map<String, Integer> dataDriveDeviceNameToSizeInGig) {
         this.dataDriveDeviceNameToSizeInGig = ImmutableMap.copyOf(checkNotNull(dataDriveDeviceNameToSizeInGig,
               "dataDriveDeviceNameToSizeInGig"));
         return this;
      }

      public VMSpec build() {
         return new VMSpec(operatingSystem, processorCount, memoryInGig, bootDeviceName, bootDriveSize,
               dataDriveDeviceNameToSizeInGig);
      }

      public static Builder fromVMSpec(VMSpec in) {
         return new Builder().operatingSystem(in.getOperatingSystem()).memoryInGig(in.getMemoryInGig())
               .bootDeviceName(in.getBootDeviceName()).bootDiskSize(in.getBootDiskSize())
               .addDataDrives(in.getDataDiskDeviceNameToSizeInGig()).processorCount(in.getProcessorCount());
      }

   }

   static void checkProcessorCount(int processorCount) {
      checkArgument(processorCount > 0, "processorCount must be positive and an increment of 0.5");
      checkArgument(processorCount % .5 == 0, "processorCount must be an increment of 0.5");
   }

   private final CIMOperatingSystem operatingSystem;
   private final int processorCount;
   private final int memoryInGig;
   private final String bootDeviceName;
   private final int bootDriveSize;
   private final Map<String, Integer> dataDriveDeviceNameToSizeInGig;

   protected VMSpec(CIMOperatingSystem operatingSystem, int processorCount, int memoryInGig, String bootDeviceName,
         int bootDriveSize, Map<String, Integer> dataDriveDeviceNameToSizeInGig) {
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem not specified");
      checkProcessorCount(processorCount);
      this.processorCount = processorCount;
      checkArgument(memoryInGig > 0, "memoryInGig must be positive");
      this.memoryInGig = memoryInGig;
      this.bootDeviceName = checkNotNull(bootDeviceName, "bootDeviceName name not specified");
      checkArgument(bootDriveSize > 0, "bootDriveSize must be positive");
      this.bootDriveSize = bootDriveSize;
      this.dataDriveDeviceNameToSizeInGig = ImmutableMap.copyOf(checkNotNull(dataDriveDeviceNameToSizeInGig,
            "dataDriveDeviceNameToSizeInGig"));
   }

   public CIMOperatingSystem getOperatingSystem() {
      return operatingSystem;
   }

   public int getProcessorCount() {
      return processorCount;
   }

   public int getMemoryInGig() {
      return memoryInGig;
   }

   public Builder toBuilder() {
      return Builder.fromVMSpec(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bootDeviceName == null) ? 0 : bootDeviceName.hashCode());
      result = prime * result + bootDriveSize;
      result = prime * result
               + ((dataDriveDeviceNameToSizeInGig == null) ? 0 : dataDriveDeviceNameToSizeInGig.hashCode());
      result = prime * result + memoryInGig;
      result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
      result = prime * result + processorCount;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VMSpec other = (VMSpec) obj;
      if (bootDeviceName == null) {
         if (other.bootDeviceName != null)
            return false;
      } else if (!bootDeviceName.equals(other.bootDeviceName))
         return false;
      if (bootDriveSize != other.bootDriveSize)
         return false;
      if (dataDriveDeviceNameToSizeInGig == null) {
         if (other.dataDriveDeviceNameToSizeInGig != null)
            return false;
      } else if (!dataDriveDeviceNameToSizeInGig.equals(other.dataDriveDeviceNameToSizeInGig))
         return false;
      if (memoryInGig != other.memoryInGig)
         return false;
      if (operatingSystem == null) {
         if (other.operatingSystem != null)
            return false;
      } else if (!operatingSystem.equals(other.operatingSystem))
         return false;
      if (processorCount != other.processorCount)
         return false;
      return true;
   }

   public String getBootDeviceName() {
      return bootDeviceName;
   }

   public int getBootDiskSize() {
      return bootDriveSize;
   }

   public Map<String, Integer> getDataDiskDeviceNameToSizeInGig() {
      return dataDriveDeviceNameToSizeInGig;
   }

   @Override
   public String toString() {
      return "[operatingSystem=" + operatingSystem + ", processorCount=" + processorCount + ", memoryInGig="
            + memoryInGig + ", bootDeviceName=" + bootDeviceName + ", bootDriveSize=" + bootDriveSize
            + ", dataDriveDeviceNameToSizeInGig=" + dataDriveDeviceNameToSizeInGig + "]";
   }

}