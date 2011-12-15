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

/**
 * A representation of a hard disk in a VirtualBox VM.
 *
 * diskPath is an absolute path to the file that is the location of the storage for the hard disk.
 * diskFormat is any of the formats supported by ISystemProperties.getMediumFormats() in the VirtualBox API.
 * This call is platform-dependent so the supported formats differ from host to host. The default format used is VDI.
 * deviceDetails contains information about how the HardDisk is attached to the StorageController.
 *
 */
public class HardDisk {

   public static final String DEFAULT_DISK_FORMAT = "vdi";
   
   private final String diskFormat;
   private final String diskPath;
   private final DeviceDetails deviceDetails;

   public HardDisk(DeviceDetails deviceDetails, String diskPath, String diskFormat) {
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
      if (o == null || getClass() != o.getClass()) return false;

      HardDisk hardDisk = (HardDisk) o;

      if (deviceDetails != null ? !deviceDetails.equals(hardDisk.deviceDetails) : hardDisk.deviceDetails != null)
         return false;
      if (diskFormat != null ? !diskFormat.equals(hardDisk.diskFormat) : hardDisk.diskFormat != null) return false;
      if (diskPath != null ? !diskPath.equals(hardDisk.diskPath) : hardDisk.diskPath != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = diskFormat != null ? diskFormat.hashCode() : 0;
      result = 31 * result + (diskPath != null ? diskPath.hashCode() : 0);
      result = 31 * result + (deviceDetails != null ? deviceDetails.hashCode() : 0);
      return result;
   }
}
