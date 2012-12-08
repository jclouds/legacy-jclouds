/*
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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;

import java.beans.ConstructorProperties;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

/**
 * An ephemeral disk of a MachineType
 *
 * @author David Alves
 */
public class MachineTypeEphemeralDisk {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromEphemeralDisk(this);
   }

   public static class Builder {

      private int diskGb;

      /**
       * @see MachineTypeEphemeralDisk#getDiskGb()
       */
      public Builder diskGb(int diskGb) {
         this.diskGb = diskGb;
         return this;
      }

      public MachineTypeEphemeralDisk build() {
         return new MachineTypeEphemeralDisk(diskGb);
      }

      public Builder fromEphemeralDisk(MachineTypeEphemeralDisk in) {
         return new Builder().diskGb(in.getDiskGb());
      }
   }

   private final int diskGb;

   @ConstructorProperties({
           "diskGb"
   })
   private MachineTypeEphemeralDisk(int diskGb) {
      this.diskGb = diskGb;
   }

   /**
    * @return size of the ephemeral disk, defined in GB.
    */
   public int getDiskGb() {
      return diskGb;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(diskGb);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      MachineTypeEphemeralDisk that = MachineTypeEphemeralDisk.class.cast(obj);
      return equal(this.diskGb, that.diskGb);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("diskGb", diskGb);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
