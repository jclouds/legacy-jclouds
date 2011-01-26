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
public class BlockDevice extends Device {
   public static class Builder extends Device.Builder {
      private final int index;

      public Builder(int index) {
         this.index = index;
      }

      @Override
      public Device build() {
         return new BlockDevice(uuid, mediaType, index);
      }

   }

   private final int index;

   public BlockDevice(String driveUuid, MediaType mediaType, int index) {
      super(driveUuid, mediaType);
      checkArgument(index >= 0 && index < 8, "index must be between 0 and 7");
      this.index = index;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + index;
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
      BlockDevice other = (BlockDevice) obj;
      if (index != other.index)
         return false;
      return true;
   }

   @Override
   public String getId() {
      return String.format("block:%d", index);
   }

   public int getIndex() {
      return index;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", driveUuid=" + driveUuid + ", mediaType=" + mediaType + "]";
   }
}