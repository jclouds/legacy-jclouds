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

import com.google.gson.annotations.SerializedName;

/**
 * Detailed information on Server memory usage
 *
 * @author Adam Lowe
 * @see ServerStatus
 */
public class Memory {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long usage;
      private long size;
      private String unit;

      public Builder usage(long usage) {
         this.usage = usage;
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

      public Memory build() {
         return new Memory(usage, size, unit);
      }

      public Builder fromMemory(Memory in) {
         return usage(in.getUsage()).size(in.getSize()).unit(in.getUnit());
      }
   }

   @SerializedName("memusage")
   private final long usage;
   @SerializedName("memsize")
   private final long size;
   private final String unit;

   public Memory(long usage, long size, String unit) {
      this.usage = usage;
      this.size = size;
      this.unit = unit;
   }

   /**
    * @return the memory usage in #unit
    */
   public long getUsage() {
      return usage;
   }

   /**
    * @return the memory size allocated in #unit
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
   public String toString() {
      return String.format("Memory[usage=%d, size=%d, unit=%s]", usage, size, unit);
   }
}
