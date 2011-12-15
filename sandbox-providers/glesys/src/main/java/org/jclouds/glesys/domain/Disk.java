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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Detailed information on Server disk usage
 *
 * @author Adam Lowe
 * @see ServerStatus
 */
public class Disk {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long used;
      private long size;
      private String unit;

      public Builder used(long used) {
         this.used = used;
         return this;
      }

      public Builder size(long size) {
         this.size = size;
         return this;
      }

      public Builder unit(String unit) {
         this.unit = unit;
         return this;
      }

      public Disk build() {
         return new Disk(used, size, unit);
      }

      public Builder fromDisk(Disk in) {
         return used(in.getUsed()).size(in.getSize()).unit(in.getUnit());
      }
   }

   private final long used;
   private final long size;
   private final String unit;

   public Disk(long used, long size, String unit) {
      this.used = used;
      this.size = size;
      this.unit = unit;
   }

   /**
    * @return the disk used in #unit
    */
   public long getUsed() {
      return used;
   }

   /**
    * @return the disk size allocated in #unit
    */
   public long getSize() {
      return size;
   }

   /**
    * @return the unit used
    */
   public String getUnit() {
      return unit;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Disk) {
         Disk other = (Disk) object;
         return Objects.equal(used, other.used)
               && Objects.equal(size, other.size)
               && Objects.equal(unit, other.unit);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(used, size, unit);
   }
   
   @Override
   public String toString() {
      return String.format("[used=%d, size=%d, unit=%s]", used, size, unit);
   }

}
