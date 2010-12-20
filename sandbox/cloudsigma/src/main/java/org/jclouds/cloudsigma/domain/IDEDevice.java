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

package org.jclouds.cloudsigma.domain;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 
 * @author Adrian Cole
 */
public class IDEDevice extends Device {
   public static class Builder extends Device.Builder {
      private final int bus;
      private final int unit;

      public Builder(int bus, int unit) {
         this.bus = bus;
         this.unit = unit;
      }

      @Override
      public Device build() {
         return new IDEDevice(uuid, mediaType, bus, unit);
      }

   }

   private final int bus;
   private final int unit;

   public IDEDevice(String driveUuid, MediaType mediaType, int bus, int unit) {
      super(driveUuid, mediaType);
      checkArgument(bus == 0 || bus == 1, "bus must be 0 or 1");
      checkArgument(unit == 0 || unit == 1, "unit must be 0 or 1");
      this.bus = bus;
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
      IDEDevice other = (IDEDevice) obj;
      if (bus != other.bus)
         return false;
      if (unit != other.unit)
         return false;
      return true;
   }

   @Override
   public String getId() {
      return String.format("ide:%d:%d", bus, unit);
   }

   public int getBus() {
      return bus;
   }

   public int getUnit() {
      return unit;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", driveUuid=" + driveUuid + ", mediaType=" + mediaType + "]";
   }
}