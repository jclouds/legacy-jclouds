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
 * Represents a specification for a device attachment.
 * <p/>
 * <p/>
 * From the VirtualBox SDK:
 * <p/>
 * port:
 * For an IDE controller, 0 specifies the primary controller and 1 specifies the secondary controller.
 * For a SCSI controller, this must range from 0 to 15; for a SATA controller, from 0 to 29; for an SAS controller,
 * from 0 to 7
 */
public class DeviceDetails {

   private final int port;
   private final int deviceSlot;
   private final DeviceType deviceType;

   public DeviceDetails(int port, int deviceSlot, DeviceType deviceType) {
      this.port = port;
      this.deviceSlot = deviceSlot;
      this.deviceType = checkNotNull(deviceType, "deviceType can't be null");
   }

   public int getPort() {
      return port;
   }

   public int getDeviceSlot() {
      return deviceSlot;
   }

   public DeviceType getDeviceType() {
      return deviceType;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private int port;
      private int deviceSlot;
      private DeviceType deviceType;

      public Builder port(int port) {
         this.port = port;
         return this;
      }

      public Builder deviceType(DeviceType deviceType) {
         this.deviceType = deviceType;
         return this;
      }

      public Builder deviceSlot(int slot) {
         this.deviceSlot = slot;
         return this;
      }

      public DeviceDetails build() {
         checkNotNull(deviceType, "deviceType can't be null");
         return new DeviceDetails(port, deviceSlot, deviceType);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof DeviceDetails) {
         DeviceDetails other = (DeviceDetails) o;
         return Objects.equal(port, other.port) &&
                 Objects.equal(deviceSlot, other.deviceSlot)
                 && Objects.equal(deviceType, other.deviceType);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(port, deviceSlot, deviceType);
   }

   @Override
   public String toString() {
      return "DeviceDetails{" +
              "port=" + port +
              ", deviceSlot=" + deviceSlot +
              ", deviceType=" + deviceType +
              '}';
   }
}
