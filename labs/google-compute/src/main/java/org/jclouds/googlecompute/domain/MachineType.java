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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Represents a machine type used to host an instance.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/machineTypes"/>
 */
public class MachineType extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromMachineType(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private int guestCpus;
      private int memoryMb;
      private int imageSpaceGb;
      private ImmutableList.Builder<MachineTypeEphemeralDisk> ephemeralDisks = ImmutableList.builder();
      private int maximumPersistentDisks;
      private long maximumPersistentDisksSizeGb;
      private ImmutableSet.Builder<String> availableZone = ImmutableSet.builder();

      /**
       * @see MachineType#getGuestCpus()
       */
      public T guestCpus(int guesCpus) {
         this.guestCpus = guesCpus;
         return self();
      }

      /**
       * @see MachineType#getMemoryMb()
       */
      public T memoryMb(int memoryMb) {
         this.memoryMb = memoryMb;
         return self();
      }

      /**
       * @see MachineType#getImageSpaceGb()
       */
      public T imageSpaceGb(int imageSpaceGb) {
         this.imageSpaceGb = imageSpaceGb;
         return self();
      }

      /**
       * @see MachineType#getEphemeralDisks()
       */
      public T addEphemeralDisk(int diskGb) {
         this.ephemeralDisks.add(MachineTypeEphemeralDisk.builder().diskGb(diskGb).build());
         return self();
      }

      /**
       * @see MachineType#getEphemeralDisks()
       */
      public T ephemeralDisks(List<MachineTypeEphemeralDisk> ephemeralDisks) {
         this.ephemeralDisks.addAll(ephemeralDisks);
         return self();
      }

      /**
       * @see MachineType#getMaximumPersistentDisks()
       */
      public T maximumPersistentDisks(int maximumPersistentDisks) {
         this.maximumPersistentDisks = maximumPersistentDisks;
         return self();
      }

      /**
       * @see MachineType#getMaximumPersistentDisksSizeGb()
       */
      public T maximumPersistentDisksSizeGb(long maximumPersistentDisksSizeGb) {
         this.maximumPersistentDisksSizeGb = maximumPersistentDisksSizeGb;
         return self();
      }

      /**
       * @see MachineType#getAvailableZone()
       */
      public T addAvailableZone(String availableZone) {
         this.availableZone.add(availableZone);
         return self();
      }

      /**
       * @see MachineType#getAvailableZone()
       */
      public T availableZones(Set<String> availableZone) {
         this.availableZone.addAll(availableZone);
         return self();
      }

      public MachineType build() {
         return new MachineType(id, creationTimestamp, selfLink, name, description, guestCpus, memoryMb,
                 imageSpaceGb, ephemeralDisks.build(), maximumPersistentDisks, maximumPersistentDisksSizeGb,
                 availableZone.build());
      }


      public Builder<?> fromMachineType(MachineType in) {
         return super.fromResource(in).memoryMb(in.getMemoryMb()).imageSpaceGb(in.getImageSpaceGb()).ephemeralDisks(in
                 .getEphemeralDisks()).maximumPersistentDisks(in.getMaximumPersistentDisks())
                 .maximumPersistentDisksSizeGb(in.getMaximumPersistentDisksSizeGb()).availableZones(in
                         .getAvailableZone());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int guestCpus;
   private final int memoryMb;
   private final int imageSpaceGb;
   private final List<MachineTypeEphemeralDisk> ephemeralDisks;
   private final int maximumPersistentDisks;
   private final long maximumPersistentDisksSizeGb;
   private final Set<String> availableZone;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "guestCpus", "memoryMb",
           "imageSpaceGb", "ephemeralDisks", "maximumPersistentDisks", "maximumPersistentDisksSizeGb", "availableZone"
   })
   public MachineType(String id, Date creationTimestamp, String selfLink, String name, String description,
                      int guestCpus, int memoryMb, int imageSpaceGb, List<MachineTypeEphemeralDisk> ephemeralDisks,
                      int maximumPersistentDisks, long maximumPersistentDisksSizeGb, Set<String> availableZone) {
      super(Kind.MACHINE_TYPE, id, creationTimestamp, selfLink, name, description);
      this.guestCpus = guestCpus;
      this.memoryMb = memoryMb;
      this.imageSpaceGb = imageSpaceGb;
      this.ephemeralDisks = nullCollectionOnNullOrEmpty(ephemeralDisks);
      this.maximumPersistentDisks = maximumPersistentDisks;
      this.maximumPersistentDisksSizeGb = maximumPersistentDisksSizeGb;
      this.availableZone = nullCollectionOnNullOrEmpty(availableZone);
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
   @Nullable
   public List<MachineTypeEphemeralDisk> getEphemeralDisks() {
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
   @Nullable
   public Set<String> getAvailableZone() {
      return availableZone;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, guestCpus, memoryMb,
              imageSpaceGb, ephemeralDisks, maximumPersistentDisks, maximumPersistentDisksSizeGb,
              availableZone);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      MachineType that = MachineType.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.guestCpus, that.guestCpus)
              && Objects.equal(this.memoryMb, that.memoryMb)
              && Objects.equal(this.imageSpaceGb, that.imageSpaceGb)
              && Objects.equal(this.ephemeralDisks, that.ephemeralDisks)
              && Objects.equal(this.maximumPersistentDisks, that.maximumPersistentDisks)
              && Objects.equal(this.maximumPersistentDisksSizeGb, that.maximumPersistentDisksSizeGb)
              && Objects.equal(this.availableZone, that.availableZone);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("guestCpus", guestCpus).add("memoryMb",
                      memoryMb).add("imageSpaceGb", imageSpaceGb).add("ephemeralDisks",
                      ephemeralDisks).add("maximumPersistentDisks",
                      maximumPersistentDisks).add("maximumPersistentDisksSizeGb", maximumPersistentDisksSizeGb).add
                      ("availableZone", availableZone);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
