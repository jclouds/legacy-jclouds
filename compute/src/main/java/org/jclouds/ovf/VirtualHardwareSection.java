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

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The virtual hardware required by a virtual machine is specified in VirtualHardwareSection.
 * <p/>
 * This specification supports abstract or incomplete hardware descriptions in which only the major
 * devices are described. The hypervisor is allowed to create additional virtual hardware
 * controllers and devices, as long as the required devices listed in the descriptor are realized.
 * 
 * @author Adrian Cole
 */
public class VirtualHardwareSection extends Section<VirtualHardwareSection> {

   @SuppressWarnings("unchecked")
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

   public static class Builder extends Section.Builder<VirtualHardwareSection> {
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
         this.transports = ImmutableSet.<String> copyOf(checkNotNull(transports, "transports"));
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
         this.items = ImmutableSet.<ResourceAllocationSettingData> copyOf(checkNotNull(
                  items, "items"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public VirtualHardwareSection build() {
         return new VirtualHardwareSection(info, transports, virtualSystem, items);
      }

      public Builder fromVirtualHardwareSection(VirtualHardwareSection in) {
         return fromSection(in).items(in.getItems()).transports(in.getTransports()).system(
                  in.getSystem()).info(in.getInfo());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<VirtualHardwareSection> in) {
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

   protected final VirtualSystemSettingData virtualSystem;
   protected final Set<String> transports;
   protected final Set<ResourceAllocationSettingData> items;

   public VirtualHardwareSection(String info, Iterable<String> transports, VirtualSystemSettingData virtualSystem,
            Iterable<? extends ResourceAllocationSettingData> items) {
      super(info);
      this.virtualSystem = virtualSystem;
      this.transports = ImmutableSet.<String> copyOf(checkNotNull(transports, "transports"));
      this.items = ImmutableSet.<ResourceAllocationSettingData> copyOf(checkNotNull(items,
               "items"));
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
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((items == null) ? 0 : items.hashCode());
      result = prime * result + ((transports == null) ? 0 : transports.hashCode());
      result = prime * result + ((virtualSystem == null) ? 0 : virtualSystem.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      VirtualHardwareSection other = (VirtualHardwareSection) obj;
      if (items == null) {
         if (other.items != null)
            return false;
      } else if (!items.equals(other.items))
         return false;
      if (transports == null) {
         if (other.transports != null)
            return false;
      } else if (!transports.equals(other.transports))
         return false;
      if (virtualSystem == null) {
         if (other.virtualSystem != null)
            return false;
      } else if (!virtualSystem.equals(other.virtualSystem))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[info=%s, items=%s, transports=%s, virtualSystem=%s]", info,
               items, transports, virtualSystem);
   }

}
