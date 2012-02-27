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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.Disk;
import org.jclouds.vcloud.director.v1_5.domain.SectionType;

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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DiskSection", namespace = VCLOUD_OVF_NS)
@XmlType(propOrder = {
      "disks"
})
public class DiskSection extends SectionType<DiskSection> {

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

   public static class Builder extends SectionType.Builder<DiskSection> {
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
         this.disks = ImmutableSet.<Disk>copyOf(checkNotNull(disks, "disks"));
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
      public Builder fromSection(SectionType<DiskSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   private Set<Disk> disks;

   private DiskSection(String info, Iterable<Disk> disks) {
      super(info);
      this.disks = ImmutableSet.<Disk>copyOf(checkNotNull(disks, "disks"));
   }
   
   private DiskSection() {
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