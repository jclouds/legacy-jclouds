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

import com.google.common.base.Objects;

/**
 * Represents an optical medium (DVD) in a VirtualBox VM.
 * <p/>
 * sourcePath is the location of the .iso file to load the medium from.
 * deviceDetails contains information about how the Dvd is attached to the StorageController.
 */
public class IsoImage {
   private DeviceDetails deviceDetails;
   private String sourcePath;

   public IsoImage(DeviceDetails deviceDetails, String sourcePath) {
      this.deviceDetails = checkNotNull(deviceDetails, "deviceDetails  can't be null");
      this.sourcePath = checkNotNull(sourcePath, "sourcePath can't be null");
   }

   public DeviceDetails getDeviceDetails() {
      return deviceDetails;
   }

   public String getSourcePath() {
      return sourcePath;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof IsoImage) {
         IsoImage other = (IsoImage) o;
         return Objects.equal(deviceDetails, other.deviceDetails) &&
                 Objects.equal(sourcePath, other.sourcePath);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(deviceDetails, sourcePath);
   }

   @Override
   public String toString() {
      return "Dvd{" +
              "deviceDetails=" + deviceDetails +
              ", sourcePath='" + sourcePath + '\'' +
              '}';
   }
}
