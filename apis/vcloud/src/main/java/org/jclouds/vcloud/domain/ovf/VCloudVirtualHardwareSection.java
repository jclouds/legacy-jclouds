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
package org.jclouds.vcloud.domain.ovf;

import java.net.URI;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.ovf.Section;
import org.jclouds.ovf.VirtualHardwareSection;

/**
 * A description of the virtual hardware supported by a virtual machine.
 */
public class VCloudVirtualHardwareSection extends VirtualHardwareSection {
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromVCloudVirtualHardwareSection(this);
   }

   public static class Builder extends VirtualHardwareSection.Builder {
      protected String type;
      protected URI href;

      /**
       * @see VCloudVirtualHardware#getType
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see VCloudVirtualHardware#getHref
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder system(VirtualSystemSettingData virtualSystem) {
         return Builder.class.cast(super.system(virtualSystem));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder transport(String transport) {
         return Builder.class.cast(super.transport(transport));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder transports(Iterable<String> transports) {
         return Builder.class.cast(super.transports(transports));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder item(ResourceAllocationSettingData item) {
         return Builder.class.cast(super.item(item));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder items(Iterable<? extends ResourceAllocationSettingData> items) {
         return Builder.class.cast(super.items(items));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public VCloudVirtualHardwareSection build() {
         return new VCloudVirtualHardwareSection(type, href, info, transports, virtualSystem, items);
      }

      public Builder fromVCloudVirtualHardwareSection(VCloudVirtualHardwareSection in) {
         return fromVirtualHardwareSection(in).type(in.getType()).href(in.getHref());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromVirtualHardwareSection(VirtualHardwareSection in) {
         return Builder.class.cast(super.fromVirtualHardwareSection(in));
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

   protected final String type;
   protected final URI href;

   public VCloudVirtualHardwareSection(String type, URI href, String info, Iterable<String> transports,
         VirtualSystemSettingData virtualSystem, Iterable<? extends ResourceAllocationSettingData> resourceAllocations) {
      super(info, transports, virtualSystem, resourceAllocations);
      this.type = type;
      this.href = href;
   }

   public String getType() {
      return type;
   }

   public URI getHref() {
      return href;
   }

   @Override
   public int hashCode() {
      return href.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      VCloudVirtualHardwareSection other = (VCloudVirtualHardwareSection) obj;
      return href.equals(other.href);
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", type=" + getType() + ", info=" + getInfo() + ", virtualSystem=" + getSystem()
            + "]";
   }
}