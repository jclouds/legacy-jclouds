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

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a machine type used to host an instance.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/machineTypes"/>
 */
@Beta
public final class MachineType extends Resource {

   private final Integer guestCpus;
   private final Integer memoryMb;
   private final Integer imageSpaceGb;
   private final List<EphemeralDisk> ephemeralDisks;
   private final Integer maximumPersistentDisks;
   private final Long maximumPersistentDisksSizeGb;
   private final Set<String> availableZone;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "guestCpus", "memoryMb",
           "imageSpaceGb", "ephemeralDisks", "maximumPersistentDisks", "maximumPersistentDisksSizeGb", "availableZone"
   })
   private MachineType(String id, Date creationTimestamp, URI selfLink, String name, String description,
                       int guestCpus, int memoryMb, int imageSpaceGb, List<EphemeralDisk> ephemeralDisks,
                       int maximumPersistentDisks, long maximumPersistentDisksSizeGb, Set<String> availableZone) {
      super(Kind.MACHINE_TYPE, checkNotNull(id, "id of %s", name), fromNullable(creationTimestamp),
              checkNotNull(selfLink, "selfLink of %s", name), checkNotNull(name, "name"), fromNullable(description));
      this.guestCpus = checkNotNull(guestCpus, "guestCpus of %s", name);
      this.memoryMb = checkNotNull(memoryMb, "memoryMb of %s", name);
      this.imageSpaceGb = checkNotNull(imageSpaceGb, "imageSpaceGb of %s", name);
      this.ephemeralDisks = ephemeralDisks == null ? ImmutableList.<EphemeralDisk>of() : ephemeralDisks;
      this.maximumPersistentDisks = checkNotNull(maximumPersistentDisks, "maximumPersistentDisks of %s", name);
      this.maximumPersistentDisksSizeGb = maximumPersistentDisksSizeGb;
      this.availableZone = availableZone == null ? ImmutableSet.<String>of() : availableZone;
   }

   /**
    * @return count of CPUs exposed to the instance.
    */
   public int getGuestCpus() {
      return guestCpus;
   }

   /**
    * @return physical memory assigned to the instance, defined in MB.
    */
   public int getMemoryMb() {
      return memoryMb;
   }

   /**
    * @return space allotted for the image, defined in GB.
    */
   public int getImageSpaceGb() {
      return imageSpaceGb;
   }

   /**
    * @return extended ephemeral disks assigned to the instance.
    */
   public List<EphemeralDisk> getEphemeralDisks() {
      return ephemeralDisks;
   }

   /**
    * @return maximum persistent disks allowed.
    */
   public int getMaximumPersistentDisks() {
      return maximumPersistentDisks;
   }

   /**
    * @return maximum total persistent disks size (GB) allowed.
    */
   public long getMaximumPersistentDisksSizeGb() {
      return maximumPersistentDisksSizeGb;
   }

   /**
    * @return the zones that this machine type can run in.
    */
   public Set<String> getAvailableZone() {
      return availableZone;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("guestCpus", guestCpus)
              .add("memoryMb", memoryMb)
              .add("imageSpaceGb", imageSpaceGb)
              .add("ephemeralDisks", ephemeralDisks)
              .add("maximumPersistentDisks", maximumPersistentDisks)
              .add("maximumPersistentDisksSizeGb", maximumPersistentDisksSizeGb)
              .add("availableZone", availableZone);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMachineType(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Integer guestCpus;
      private Integer memoryMb;
      private Integer imageSpaceGb;
      private ImmutableList.Builder<EphemeralDisk> ephemeralDisks = ImmutableList.builder();
      private Integer maximumPersistentDisks;
      private Long maximumPersistentDisksSizeGb;
      private ImmutableSet.Builder<String> availableZone = ImmutableSet.builder();

      /**
       * @see MachineType#getGuestCpus()
       */
      public Builder guestCpus(int guesCpus) {
         this.guestCpus = guesCpus;
         return this;
      }

      /**
       * @see MachineType#getMemoryMb()
       */
      public Builder memoryMb(int memoryMb) {
         this.memoryMb = memoryMb;
         return this;
      }

      /**
       * @see MachineType#getImageSpaceGb()
       */
      public Builder imageSpaceGb(int imageSpaceGb) {
         this.imageSpaceGb = imageSpaceGb;
         return this;
      }

      /**
       * @see MachineType#getEphemeralDisks()
       */
      public Builder addEphemeralDisk(int diskGb) {
         this.ephemeralDisks.add(EphemeralDisk.builder().diskGb(diskGb).build());
         return this;
      }

      /**
       * @see MachineType#getEphemeralDisks()
       */
      public Builder ephemeralDisks(List<EphemeralDisk> ephemeralDisks) {
         this.ephemeralDisks.addAll(ephemeralDisks);
         return this;
      }

      /**
       * @see MachineType#getMaximumPersistentDisks()
       */
      public Builder maximumPersistentDisks(int maximumPersistentDisks) {
         this.maximumPersistentDisks = maximumPersistentDisks;
         return this;
      }

      /**
       * @see MachineType#getMaximumPersistentDisksSizeGb()
       */
      public Builder maximumPersistentDisksSizeGb(long maximumPersistentDisksSizeGb) {
         this.maximumPersistentDisksSizeGb = maximumPersistentDisksSizeGb;
         return this;
      }

      /**
       * @see MachineType#getAvailableZone()
       */
      public Builder addAvailableZone(String availableZone) {
         this.availableZone.add(availableZone);
         return this;
      }

      /**
       * @see MachineType#getAvailableZone()
       */
      public Builder availableZones(Set<String> availableZone) {
         this.availableZone.addAll(availableZone);
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public MachineType build() {
         return new MachineType(id, creationTimestamp, selfLink, name, description, guestCpus, memoryMb,
                 imageSpaceGb, ephemeralDisks.build(), maximumPersistentDisks, maximumPersistentDisksSizeGb,
                 availableZone.build());
      }


      public Builder fromMachineType(MachineType in) {
         return super.fromResource(in).memoryMb(in.getMemoryMb()).imageSpaceGb(in.getImageSpaceGb()).ephemeralDisks(in
                 .getEphemeralDisks()).maximumPersistentDisks(in.getMaximumPersistentDisks())
                 .maximumPersistentDisksSizeGb(in.getMaximumPersistentDisksSizeGb()).availableZones(in
                         .getAvailableZone());
      }
   }

   /**
    * An ephemeral disk of a MachineType
    */
   public static final class EphemeralDisk {

      private final int diskGb;

      @ConstructorProperties({
              "diskGb"
      })
      private EphemeralDisk(int diskGb) {
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
         EphemeralDisk that = EphemeralDisk.class.cast(obj);
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

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromEphemeralDisk(this);
      }

      public static class Builder {

         private int diskGb;

         /**
          * @see org.jclouds.googlecompute.domain.MachineType.EphemeralDisk#getDiskGb()
          */
         public Builder diskGb(int diskGb) {
            this.diskGb = diskGb;
            return this;
         }

         public EphemeralDisk build() {
            return new EphemeralDisk(diskGb);
         }

         public Builder fromEphemeralDisk(EphemeralDisk in) {
            return new Builder().diskGb(in.getDiskGb());
         }
      }
   }
}
