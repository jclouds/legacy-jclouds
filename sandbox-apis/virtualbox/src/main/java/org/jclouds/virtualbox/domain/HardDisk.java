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

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A representation of a hard disk in a VirtualBox VM.
 * <p/>
 * diskPath is an absolute path to the file that is the location of the storage for the hard disk.
 * diskFormat is any of the formats supported by ISystemProperties.getMediumFormats() in the VirtualBox API.
 * This call is platform-dependent so the supported formats differ from host to host. The default format used is VDI.
 * deviceDetails contains information about how the HardDisk is attached to the StorageController.
 */
public class HardDisk {

   public static final String DEFAULT_DISK_FORMAT = "vdi";

   private final String diskFormat;
   private final String diskPath;
   private final DeviceDetails deviceDetails;

   public HardDisk(DeviceDetails deviceDetails, String diskPath, String diskFormat) {
      checkNotNull(deviceDetails, "deviceDetails");
      checkNotNull(diskPath, "diskPath");
      checkNotNull(diskFormat, "diskFormat");
      this.diskPath = diskPath;
      this.diskFormat = diskFormat;
      this.deviceDetails = deviceDetails;
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

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof HardDisk) {
         HardDisk hardDisk = (HardDisk) o;
         return Objects.equal(deviceDetails, hardDisk.deviceDetails) &&
                 Objects.equal(diskFormat, hardDisk.diskFormat) &&
                 Objects.equal(diskPath, hardDisk.diskPath);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(diskPath, diskFormat, deviceDetails);
   }

   @Override
   public String toString() {
      return "HardDisk{" +
              "diskFormat='" + diskFormat + '\'' +
              ", diskPath='" + diskPath + '\'' +
              ", deviceDetails=" + deviceDetails +
              '}';
   }
}
