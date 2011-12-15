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

import org.virtualbox_4_1.DeviceType;

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
      this.deviceType = deviceType;
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

}
