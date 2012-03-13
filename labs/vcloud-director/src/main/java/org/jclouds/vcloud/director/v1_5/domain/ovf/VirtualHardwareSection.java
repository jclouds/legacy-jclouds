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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.cim.ResourceAllocationSettingData;
import org.jclouds.vcloud.director.v1_5.domain.cim.VirtualSystemSettingData;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The virtual hardware required by a virtual machine is specified in VirtualHardwareSection.
 *
 * This specification supports abstract or incomplete hardware descriptions in which only the major
 * devices are described. The hypervisor is allowed to create additional virtual hardware
 * controllers and devices, as long as the required devices listed in the descriptor are realized.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "VirtualHardwareSection")
public class VirtualHardwareSection extends SectionType<VirtualHardwareSection> {

   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromVirtualHardwareSection(this);
   }

   public static class Builder extends SectionType.Builder<VirtualHardwareSection> {
      protected VirtualSystemSettingData virtualSystem;
      protected Set<String> transports = Sets.newLinkedHashSet();
      protected Set<ResourceAllocationSettingData> items = Sets.newLinkedHashSet();

      /**
       * @see VirtualHardwareSection#getSystem
       */
      public Builder system(VirtualSystemSettingData virtualSystem) {
         this.virtualSystem = virtualSystem;
         return this;
      }

      /**
       * @see VirtualHardwareSection#getTransports
       */
      public Builder transport(String transport) {
         this.transports.add(checkNotNull(transport, "transport"));
         return this;
      }

      /**
       * @see VirtualHardwareSection#getTransports
       */
      public Builder transports(Iterable<String> transports) {
         this.transports = ImmutableSet.<String>copyOf(checkNotNull(transports, "transports"));
         return this;
      }

      /**
       * @see VirtualHardwareSection#getItems
       */
      public Builder item(ResourceAllocationSettingData item) {
         this.items.add(checkNotNull(item, "item"));
         return this;
      }

      /**
       * @see VirtualHardwareSection#getItems
       */
      public Builder items(Iterable<? extends ResourceAllocationSettingData> items) {
         this.items = ImmutableSet.<ResourceAllocationSettingData>copyOf(checkNotNull(
               items, "items"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public VirtualHardwareSection build() {
         return new VirtualHardwareSection(info, required, transports, virtualSystem, items);
      }

      public Builder fromVirtualHardwareSection(VirtualHardwareSection in) {
         return fromSectionType(in).items(in.getItems()).transports(in.getTransports()).system(
               in.getSystem()).info(in.getInfo());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSectionType(SectionType<VirtualHardwareSection> in) {
         return Builder.class.cast(super.fromSectionType(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder required(Boolean required) {
         return Builder.class.cast(super.required(required));
      }
   }

   private VirtualSystemSettingData virtualSystem;
   private Set<String> transports;
   private Set<ResourceAllocationSettingData> items;

   private VirtualHardwareSection(@Nullable String info, @Nullable Boolean required, Iterable<String> transports, VirtualSystemSettingData virtualSystem,
                                  Iterable<? extends ResourceAllocationSettingData> items) {
      super(info, required);
      this.virtualSystem = virtualSystem;
      this.transports = ImmutableSet.<String>copyOf(checkNotNull(transports, "transports"));
      this.items = ImmutableSet.<ResourceAllocationSettingData>copyOf(checkNotNull(items, "items"));
   }

   private VirtualHardwareSection() {
      // For JAXB
   }

   /**
    * transport types define methods by which the environment document is communicated from the
    * deployment platform to the guest software.
    * <p/>
    * To enable interoperability, this specification defines an "iso" transport type which all
    * implementations that support CD-ROM devices are required to support. The iso transport
    * communicates the environment 1346 document by making a dynamically generated ISO image
    * available to the guest software. To support the iso transport type, prior to booting a virtual
    * machine, an implementation shall make an ISO 9660 read-only disk image available as backing
    * for a disconnected CD-ROM. If the iso transport is selected for a VirtualHardwareSection, at
    * least one disconnected CD-ROM device shall be present in this section.
    * <p/>
    * Support for the "iso" transport type is not a requirement for virtual hardware architectures
    * or guest 1351 operating systems which do not have CD-ROM device support.
    *
    * @return
    */
   public Set<String> getTransports() {
      return transports;
   }

   public VirtualSystemSettingData getSystem() {
      return virtualSystem;
   }

   public Set<? extends ResourceAllocationSettingData> getItems() {
      return items;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), transports, virtualSystem, items);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;

      VirtualHardwareSection other = (VirtualHardwareSection) obj;
      return super.equals(other) && Objects.equal(transports, other.transports)
            && Objects.equal(virtualSystem, other.virtualSystem)
            && Objects.equal(items, other.items);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("transports", transports).add("virtualSystem", virtualSystem).add("items", items);
   }
}