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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A DiskSection describes meta-information about virtual disks in the OVF package. Virtual disks
 * and their metadata are described outside the virtual hardware to facilitate sharing between
 * virtual machines within an OVF package.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "DiskSection")
@XmlType(propOrder = {
      "disks"
})
public class DiskSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromDiskSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {
      private Set<Disk> disks = Sets.newLinkedHashSet();

      /**
       * @see DiskSection#getDisks
       */
      public B disk(Disk disk) {
         this.disks.add(checkNotNull(disk, "disk"));
         return self();
      }

      /**
       * @see DiskSection#getDisks
       */
      public B disks(Iterable<Disk> disks) {
         this.disks = ImmutableSet.<Disk>copyOf(checkNotNull(disks, "disks"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public DiskSection build() {
         return new DiskSection(this);
      }

      public B fromDiskSection(DiskSection in) {
         return disks(in.getDisks()).info(in.getInfo());
      }
  }

   @XmlElement(name = "Disk")
   private Set<Disk> disks;

   protected DiskSection(Builder<?> builder) {
      super(builder);
      this.disks = ImmutableSet.<Disk>copyOf(checkNotNull(builder.disks, "disks"));
   }
   
   protected DiskSection() {
      // for JAXB
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
      return Objects.hashCode(super.hashCode(), disks);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;

      DiskSection other = (DiskSection) obj;
      return super.equals(other) && Objects.equal(disks, other.disks);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("disks", disks);
   }
}
