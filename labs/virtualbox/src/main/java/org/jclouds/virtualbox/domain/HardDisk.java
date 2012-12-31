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

package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.virtualbox_4_2.DeviceType;

import com.google.common.base.Objects;

/**
 * A representation of a hard disk in a VirtualBox VM.
 * <p/>
 * name is a description to identify the hard disk.
 * diskPath is an absolute path to the file that is the location of the storage for the hard disk.
 * diskFormat is any of the formats supported by ISystemProperties.getMediumFormats() in the VirtualBox API.
 * This call is platform-dependent so the supported formats differ from host to host. The default format used is VDI.
 * deviceDetails contains information about how the HardDisk is attached to the StorageController.
 */
public class HardDisk {

   public static final String DEFAULT_DISK_FORMAT = "vdi";

   // NB the name is not independent; the IMedium name is chosen based on the last part of diskPath
   private final String name;
   private final String diskFormat;
   private final String diskPath;
   private final DeviceDetails deviceDetails;
   private final boolean autoDelete;

   public HardDisk(DeviceDetails deviceDetails, String diskPath, String diskFormat, boolean autoDelete) {
      this.diskPath = checkNotNull(diskPath, "diskPath can't be null");
      this.diskFormat = checkNotNull(diskFormat, "diskFormat can't be null");
      this.deviceDetails = checkNotNull(deviceDetails, "deviceDetails can't be null");
      this.name = diskPath.substring(diskPath.lastIndexOf("/") + 1);
      this.autoDelete = autoDelete;
   }

   public String getDiskPath() {
      return diskPath;
   }

   public String getDiskFormat() {
      return diskFormat;
   }

   public DeviceDetails getDeviceDetails() {
      return deviceDetails;
   }

   public String getName() {
		return name;
	}

	public boolean isAutoDelete() {
      return autoDelete;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof HardDisk) {
         HardDisk hardDisk = (HardDisk) o;
         return Objects.equal(deviceDetails, hardDisk.deviceDetails) &&
                 Objects.equal(diskFormat, hardDisk.diskFormat) &&
                 Objects.equal(diskPath, hardDisk.diskPath) &&
                 Objects.equal(name, hardDisk.name);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(diskPath, diskFormat, deviceDetails, name);
   }

   @Override
   public String toString() {
      return "HardDisk{" +
              "diskFormat='" + diskFormat + '\'' +
              ", diskPath='" + diskPath + '\'' +
              ", deviceDetails=" + deviceDetails +
              ", name=" + name +
              '}';
   }
   
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String diskFormat = "vdi";
      private String diskPath;
      private int controllerPort;
      private int deviceSlot;
      private DeviceType deviceType = DeviceType.HardDisk;
      private boolean autoDelete = false;

      public Builder diskFormat(String diskFormat) {
         this.diskFormat = diskFormat;
         return this;
      }

      public Builder diskpath(String diskPath) {
         this.diskPath = diskPath;
         return this;
      }

      public Builder controllerPort(int controllerPort) {
         this.controllerPort = controllerPort;
         return this;
      }
      
      public Builder deviceSlot(int deviceSlot) {
         this.deviceSlot = deviceSlot;
         return this;
      }
      
      public Builder autoDelete(boolean autoDelete) {
         this.autoDelete = autoDelete;
         return this;
      }      

      public HardDisk build() {
         checkNotNull(diskPath);
         checkNotNull(controllerPort);
         checkNotNull(deviceSlot);
         return new HardDisk(new DeviceDetails(controllerPort, deviceSlot, deviceType), diskPath, diskFormat, autoDelete);
      }
   }
}
