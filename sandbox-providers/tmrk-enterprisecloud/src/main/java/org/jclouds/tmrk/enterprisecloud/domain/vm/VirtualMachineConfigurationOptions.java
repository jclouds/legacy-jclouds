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
package org.jclouds.tmrk.enterprisecloud.domain.vm;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.*;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.DiskConfigurationOption;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * <xs:complexType name="VirtualMachineConfigurationOptions">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "VirtualMachineConfigurationOptions")
public class VirtualMachineConfigurationOptions extends BaseResource<VirtualMachineConfigurationOptions> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachineConfigurationOptions(this);
   }

   public static class Builder extends BaseResource.Builder<VirtualMachineConfigurationOptions> {

      protected ConfigurationOptionRange processor;
      protected ResourceCapacityRange memory;
      protected DiskConfigurationOption disk;
      protected ConfigurationOptionRange networkAdapter;
      protected CustomizationOption customization;
      //TODO ComputeMatrix field

      /**
       * @see VirtualMachineConfigurationOptions#getProcessor
       */
      public Builder processor(ConfigurationOptionRange processor) {
         this.processor = processor;
         return this;
      }

      /**
       * @see VirtualMachineConfigurationOptions#getMemory
       */
      public Builder memory(ResourceCapacityRange memory) {
         this.memory = memory;
         return this;
      }

      /**
       * @see VirtualMachineConfigurationOptions#getDisk
       */
      public Builder disk(DiskConfigurationOption disk) {
         this.disk = disk;
         return this;
      }

      /**
       * @see VirtualMachineConfigurationOptions#getNetworkAdapter
       */
      public Builder networkAdapter(ConfigurationOptionRange networkAdapter) {
         this.networkAdapter = networkAdapter;
         return this;
      }

      /**
       * @see VirtualMachineConfigurationOptions#getCustomization
       */
      public Builder customization(CustomizationOption customization) {
         this.customization = customization;
         return this;
      }

      @Override
      public VirtualMachineConfigurationOptions build() {
         return new VirtualMachineConfigurationOptions(href, type, processor, memory, disk, networkAdapter, customization);
      }

      public Builder fromVirtualMachineConfigurationOptions(VirtualMachineConfigurationOptions in) {
         return fromResource(in).processor(in.getProcessor())
                                .memory(in.getMemory())
                                .disk(in.getDisk())
                                .networkAdapter(in.getNetworkAdapter())
                                .customization(in.getCustomization());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<VirtualMachineConfigurationOptions> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

   }

   @XmlElement(name = "Processor", required = false)
   private ConfigurationOptionRange processor;

   @XmlElement(name = "Memory", required = false)
   private ResourceCapacityRange memory;

   @XmlElement(name = "Disk", required = false)
   private DiskConfigurationOption disk;

   @XmlElement(name = "NetworkAdapter", required = false)
   private ConfigurationOptionRange networkAdapter;

   @XmlElement(name = "Customization", required = false)
   private CustomizationOption customization;

   private VirtualMachineConfigurationOptions(URI href, String type, @Nullable ConfigurationOptionRange processor, @Nullable ResourceCapacityRange memory,
                                              @Nullable DiskConfigurationOption disk, @Nullable ConfigurationOptionRange networkAdapter, @Nullable CustomizationOption customization) {
      super(href, type);
      this.processor = processor;
      this.memory = memory;
      this.disk = disk;
      this.networkAdapter = networkAdapter;
      this.customization = customization;
   }

   private VirtualMachineConfigurationOptions() {
       //For JAXB
   }

   /**
    *
    * @return processor configuration option range
    */
   public ConfigurationOptionRange getProcessor() {
      return processor;
   }

   /**
    *
    * @return memory capacity configuration range
    */
   public ResourceCapacityRange getMemory() {
      return memory;
   }

   /**
    *
    * @return disk configuration option
    */
   public DiskConfigurationOption getDisk() {
      return disk;
   }

   /**
    *
    * @return network adapter configuration range
    */
   public ConfigurationOptionRange getNetworkAdapter() {
      return networkAdapter;
   }

   /**
    *
    * @return customization option
    */
   public CustomizationOption getCustomization() {
      return customization;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      VirtualMachineConfigurationOptions that = (VirtualMachineConfigurationOptions) o;

      if (customization != null ? !customization.equals(that.customization) : that.customization != null)
         return false;
      if (disk != null ? !disk.equals(that.disk) : that.disk != null)
         return false;
      if (memory != null ? !memory.equals(that.memory) : that.memory != null)
         return false;
      if (networkAdapter != null ? !networkAdapter.equals(that.networkAdapter) : that.networkAdapter != null)
         return false;
      if (processor != null ? !processor.equals(that.processor) : that.processor != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (processor != null ? processor.hashCode() : 0);
      result = 31 * result + (memory != null ? memory.hashCode() : 0);
      result = 31 * result + (disk != null ? disk.hashCode() : 0);
      result = 31 * result + (networkAdapter != null ? networkAdapter.hashCode() : 0);
      result = 31 * result + (customization != null ? customization.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", memory="+memory+", disk="+disk+", processor="+processor+", networkAdapter="+networkAdapter+", customization="+customization;
   }
}