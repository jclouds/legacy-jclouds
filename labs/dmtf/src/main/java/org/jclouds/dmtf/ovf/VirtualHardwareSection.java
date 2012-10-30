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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jclouds.dmtf.cim.ResourceAllocationSettingData;
import org.jclouds.dmtf.cim.VirtualSystemSettingData;

import com.google.common.base.Joiner;
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
 * @author grkvlt@apache.org
 */
public class VirtualHardwareSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromVirtualHardwareSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {

      private VirtualSystemSettingData virtualSystem;
      private String transport;
      private Set<ResourceAllocationSettingData> items = Sets.newLinkedHashSet();

      /**
       * @see VirtualHardwareSection#getSystem()
       */
      public B system(VirtualSystemSettingData virtualSystem) {
         this.virtualSystem = virtualSystem;
         return self();
      }

      /**
       * @see VirtualHardwareSection#getTransport()
       */
      public B transport(String transport) {
         this.transport = transport;
         return self();
      }

      /**
       * @see VirtualHardwareSection#getTransport()
       */
      public B transport(Iterable<String> transports) {
         this.transport = Joiner.on(',').join(transports);
         return self();
      }

      /**
       * @see VirtualHardwareSection#getTransport()
       */
      public B transport(String...transports) {
         this.transport = Joiner.on(',').join(transports);
         return self();
      }

      /**
       * @see VirtualHardwareSection#getItems()
       */
      public B item(ResourceAllocationSettingData item) {
         this.items.add(checkNotNull(item, "item"));
         return self();
      }

      /**
       * @see VirtualHardwareSection#getItems()
       */
      public B items(Iterable<? extends ResourceAllocationSettingData> items) {
         this.items = Sets.newLinkedHashSet(checkNotNull(items, "items"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public VirtualHardwareSection build() {
         return new VirtualHardwareSection(this);
      }

      public B fromVirtualHardwareSection(VirtualHardwareSection in) {
         return fromSectionType(in)
               .items(in.getItems())
               .transport(in.getTransport())
               .system(in.getSystem());
      }
   }

   @XmlElement(name = "System")
   protected VirtualSystemSettingData virtualSystem;
   @XmlAttribute(name = "transport")
   protected String transport;
   @XmlElement(name = "Item")
   protected Set<? extends ResourceAllocationSettingData> items = Sets.newLinkedHashSet();

   protected VirtualHardwareSection(Builder<?> builder) {
      super(builder);
      this.virtualSystem = builder.virtualSystem;
      this.transport = builder.transport;
      this.items = builder.items != null ? ImmutableSet.copyOf(builder.items) : Sets.<ResourceAllocationSettingData>newLinkedHashSet();
   }

   protected VirtualHardwareSection() {
      // For JAXB
   }

   /**
    * Comma-separated list of supported transports types for the OVF descriptor.
    *
    * Transport types define methods by which the environment document is communicated from the
    * deployment platform to the guest software.
    * <p>
    * To enable interoperability, this specification defines an "iso" transport type which all
    * implementations that support CD-ROM devices are required to support. The iso transport
    * communicates the environment 1346 document by making a dynamically generated ISO image
    * available to the guest software. To support the iso transport type, prior to booting a virtual
    * machine, an implementation shall make an ISO 9660 read-only disk image available as backing
    * for a disconnected CD-ROM. If the iso transport is selected for a VirtualHardwareSection, at
    * least one disconnected CD-ROM device shall be present in this section.
    * <p>
    * Support for the "iso" transport type is not a requirement for virtual hardware architectures
    * or guest 1351 operating systems which do not have CD-ROM device support.
    *
    * @return
    */
   public String getTransport() {
      return transport;
   }

   public VirtualSystemSettingData getSystem() {
      return virtualSystem;
   }

   public Set<? extends ResourceAllocationSettingData> getItems() {
      return ImmutableSet.copyOf(items);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), transport, virtualSystem, items);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      VirtualHardwareSection that = VirtualHardwareSection.class.cast(obj);
      return super.equals(that)
            && equal(this.transport, that.transport)
            && equal(this.virtualSystem, that.virtualSystem)
            && equal(this.items, that.items);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("transport", transport)
            .add("virtualSystem", virtualSystem)
            .add("items", items);
   }
}
