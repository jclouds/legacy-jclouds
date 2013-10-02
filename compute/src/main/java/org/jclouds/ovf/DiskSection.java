/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A DiskSection describes meta-information about virtual disks in the OVF package. Virtual disks
 * and their metadata are described outside the virtual hardware to facilitate sharing between
 * virtual machines within an OVF package.
 * 
 * @author Adrian Cole
 */
public class DiskSection extends Section<DiskSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromDiskSection(this);
   }

   public static class Builder extends Section.Builder<DiskSection> {
      protected Set<Disk> disks = Sets.newLinkedHashSet();

      /**
       * @see DiskSection#getDisks
       */
      public Builder disk(Disk disk) {
         this.disks.add(checkNotNull(disk, "disk"));
         return this;
      }

      /**
       * @see DiskSection#getDisks
       */
      public Builder disks(Iterable<Disk> disks) {
         this.disks = ImmutableSet.<Disk> copyOf(checkNotNull(disks, "disks"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public DiskSection build() {
         return new DiskSection(info, disks);
      }

      public Builder fromDiskSection(DiskSection in) {
         return disks(in.getDisks()).info(in.getInfo());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<DiskSection> in) {
         return (Builder) super.fromSection(in);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return (Builder) super.info(info);
      }

   }

   private final Set<Disk> disks;

   public DiskSection(String info, Iterable<Disk> disks) {
      super(info);
      this.disks = ImmutableSet.<Disk> copyOf(checkNotNull(disks, "disks"));
   }

   /**
    * All disks referred to from Connection elements in all {@link VirtualHardwareSection} elements
    * shall be defined in the DiskSection.
    * 
    * @return
    */
   public Set<Disk> getDisks() {
      return disks;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result + ((disks == null) ? 0 : disks.hashCode());
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
      DiskSection other = (DiskSection) obj;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (disks == null) {
         if (other.disks != null)
            return false;
      } else if (!disks.equals(other.disks))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[info=%s, disks=%s]", info, disks);
   }

}
