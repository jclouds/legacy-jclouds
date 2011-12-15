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
import org.jclouds.tmrk.enterprisecloud.domain.internal.AnonymousResource;
import org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutRequest;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.network.LinuxCustomization;
import org.jclouds.tmrk.enterprisecloud.domain.network.WindowsCustomization;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * <xs:complexType name="CreateVirtualMachineType">
 * @author Jason King
 */
@XmlRootElement(name = "CreateVirtualMachineRequest")
public class CreateVirtualMachine extends CreateVirtualMachineRequest {
   // Note that this class collapses both
   // CreateOsTemplateVirtualMachineRequestType and CreateVirtualMachineType
   // into the same class as the separate classes are not needed.

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

   public static class Builder extends CreateVirtualMachineRequest.Builder<CreateVirtualMachine> {

      private LinuxCustomization linuxCustomization;
      private WindowsCustomization windowsCustomization;
      private boolean poweredOn;
      private AnonymousResource template;

      /**
       * @see CreateVirtualMachine#getLinuxCustomization
       */
      public Builder linuxCustomization(LinuxCustomization linuxCustomization) {
         this.linuxCustomization = linuxCustomization;
         return this;
      }

      /**
       * @see CreateVirtualMachine#getWindowsCustomization
       */
      public Builder windowsCustomization(WindowsCustomization windowsCustomization) {
         this.windowsCustomization = windowsCustomization;
         return this;
      }


      /**
       * @see CreateVirtualMachine#isPoweredOn
       */
      public Builder poweredOn(boolean poweredOn) {
         this.poweredOn = poweredOn;
         return this;
      }

      /**
       * @see CreateVirtualMachine#getTemplate
       */
      public Builder template(AnonymousResource template) {
         this.template = template;
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
      public CreateVirtualMachine build() {
         return new CreateVirtualMachine(name, processorCount,memory,
               description,layout,tags,
               linuxCustomization,windowsCustomization,poweredOn, template);
      }

      public Builder fromCreateOsTemplateVirtualMachineRequest(CreateVirtualMachine in) {
         return fromCreateVirtualMachineRequest(in)
                .linuxCustomization(in.getLinuxCustomization())
                .windowsCustomization(in.getWindowsCustomization())
                .poweredOn(in.isPoweredOn())
                .template(in.getTemplate());
      }
   }

   @XmlElement(name = "LinuxCustomization", required = false)
   private LinuxCustomization linuxCustomization;

   @XmlElement(name = "WindowsCustomization", required = false)
   private WindowsCustomization windowsCustomization;

   @XmlElement(name = "PoweredOn", required = false)
   private boolean poweredOn;

   @XmlElement(name = "Template", required = false)
   private AnonymousResource template;


   private CreateVirtualMachine(String name, int processorCount, ResourceCapacity memory,
                                @Nullable String description, @Nullable LayoutRequest layout, @Nullable Set<String> tags,
                                @Nullable LinuxCustomization linuxCustomization, @Nullable WindowsCustomization windowsCustomization,
                                boolean poweredOn, @Nullable AnonymousResource template) {
      super(name,processorCount,memory,description,layout,tags);
      this.linuxCustomization = linuxCustomization;
      this.windowsCustomization = windowsCustomization;
      this.poweredOn = poweredOn;
      this.template = template;
   }

   protected CreateVirtualMachine() {
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

   public AnonymousResource getTemplate() {
      return template;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CreateVirtualMachine that = (CreateVirtualMachine) o;

      if (poweredOn != that.poweredOn) return false;
      if (linuxCustomization != null ? !linuxCustomization.equals(that.linuxCustomization) : that.linuxCustomization != null)
         return false;
      if (template != null ? !template.equals(that.template) : that.template != null)
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
      result = 31 * result + (template != null ? template.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", linuxCustomization="+linuxCustomization+", windowsCustomization="+windowsCustomization+", poweredOn="+poweredOn+", template="+template;
   }
}