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
import org.jclouds.tmrk.enterprisecloud.domain.LayoutRequest;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.network.LinuxCustomization;
import org.jclouds.tmrk.enterprisecloud.domain.network.WindowsCustomization;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * <xs:complexType name="CreateOsTemplateVirtualMachineRequestType">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "CreateVirtualMachineRequest")
public class CreateOsTemplateVirtualMachineRequest extends CreateVirtualMachineRequest {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromCreateOsTemplateVirtualMachineRequest(this);
   }

   public static class Builder extends CreateVirtualMachineRequest.Builder<CreateOsTemplateVirtualMachineRequest> {

      private LinuxCustomization linuxCustomization;
      private WindowsCustomization windowsCustomization;
      private boolean poweredOn;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.CreateOsTemplateVirtualMachineRequest#getLinuxCustomization
       */
      public Builder linuxCustomization(LinuxCustomization linuxCustomization) {
         this.linuxCustomization = linuxCustomization;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.CreateOsTemplateVirtualMachineRequest#getWindowsCustomization
       */
      public Builder windowsCustomization(WindowsCustomization windowsCustomization) {
         this.windowsCustomization = windowsCustomization;
         return this;
      }


      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.CreateOsTemplateVirtualMachineRequest#isPoweredOn
       */
      public Builder poweredOn(boolean poweredOn) {
         this.poweredOn = poweredOn;
         return this;
      }

      /**
       * @see CreateVirtualMachineRequest#getName()
       */
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * @see CreateVirtualMachineRequest#getDescription()
       */
      public Builder description(String description) {
         return Builder.class.cast(super.description(description));
      }

      /**
       * @see CreateVirtualMachineRequest#getProcessorCount()
       */
      public Builder processorCount(int processorCount) {
         return Builder.class.cast(super.processorCount(processorCount));
      }

      /**
       * @see CreateVirtualMachineRequest#getMemory()
       */
      public Builder memory(ResourceCapacity memory) {
         return Builder.class.cast(super.memory(memory));
      }

      /**
       * @see CreateVirtualMachineRequest#getLayout()
       */
      public Builder layout(LayoutRequest layout) {
         return Builder.class.cast(super.layout(layout));
      }

      /**
       * @see CreateVirtualMachineRequest#getTags()
       */
      public Builder tags(Set<String> tags) {
         return Builder.class.cast(super.tags(tags));
      }
      
      public Builder fromCreateVirtualMachineRequest(CreateVirtualMachineRequest request) {
         return Builder.class.cast(super.fromCreateVirtualMachineRequest(request));
      }

      @Override
      public CreateOsTemplateVirtualMachineRequest build() {
         return new CreateOsTemplateVirtualMachineRequest(name, processorCount,memory,
               description,layout,tags,
               linuxCustomization,windowsCustomization,poweredOn);
      }

      public Builder fromCreateOsTemplateVirtualMachineRequest(CreateOsTemplateVirtualMachineRequest in) {
         return fromCreateVirtualMachineRequest(in)
                .linuxCustomization(in.getLinuxCustomization())
                .windowsCustomization(in.getWindowsCustomization())
                .poweredOn(in.isPoweredOn());
      }
   }

   @XmlElement(name = "LinuxCustomization", required = false)
   private LinuxCustomization linuxCustomization;

   @XmlElement(name = "WindowsCustomization", required = false)
   private WindowsCustomization windowsCustomization;

   @XmlElement(name = "PoweredOn", required = false)
   private boolean poweredOn;
         
         
   private CreateOsTemplateVirtualMachineRequest(String name, int processorCount, ResourceCapacity memory,
                                                 @Nullable String description,@Nullable LayoutRequest layout,@Nullable Set<String> tags,
                                                 @Nullable LinuxCustomization linuxCustomization, @Nullable WindowsCustomization windowsCustomization, boolean poweredOn) {
      super(name,processorCount,memory,description,layout,tags);
      this.linuxCustomization = linuxCustomization;
      this.windowsCustomization = windowsCustomization;
      this.poweredOn = poweredOn;
   }

   protected CreateOsTemplateVirtualMachineRequest() {
       //For JAXB
   }

   public LinuxCustomization getLinuxCustomization() {
      return linuxCustomization;
   }

   public WindowsCustomization getWindowsCustomization() {
      return windowsCustomization;
   }

   public boolean isPoweredOn() {
      return poweredOn;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CreateOsTemplateVirtualMachineRequest that = (CreateOsTemplateVirtualMachineRequest) o;

      if (poweredOn != that.poweredOn) return false;
      if (linuxCustomization != null ? !linuxCustomization.equals(that.linuxCustomization) : that.linuxCustomization != null)
         return false;
      if (windowsCustomization != null ? !windowsCustomization.equals(that.windowsCustomization) : that.windowsCustomization != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = linuxCustomization != null ? linuxCustomization.hashCode() : 0;
      result = 31 * result + (windowsCustomization != null ? windowsCustomization.hashCode() : 0);
      result = 31 * result + (poweredOn ? 1 : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", linuxCustomization="+linuxCustomization+", windowsCustomization="+windowsCustomization+", poweredOn="+poweredOn;
   }
}