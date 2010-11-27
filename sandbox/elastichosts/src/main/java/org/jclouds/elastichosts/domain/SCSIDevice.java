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

package org.jclouds.elastichosts.domain;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 
 * @author Adrian Cole
 */
public class SCSIDevice extends Device {

   private final char bus = 0;
   private final char unit;

   public SCSIDevice(String driveUuid, MediaType mediaType, char unit) {
      super(driveUuid, mediaType);
      checkArgument(unit >= 0 && unit < 8, "unit must be between 0 and 7");
      this.unit = unit;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + bus;
      result = prime * result + unit;
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
      SCSIDevice other = (SCSIDevice) obj;
      if (bus != other.bus)
         return false;
      if (unit != other.unit)
         return false;
      return true;
   }

   public char getBus() {
      return bus;
   }

   public char getUnit() {
      return unit;
   }

   @Override
   public String getId() {
      return String.format("scsi:%d:%d", bus, unit);
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", driveUuid=" + driveUuid + ", mediaType=" + mediaType + "]";
   }
}