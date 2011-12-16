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
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;
import org.jclouds.tmrk.enterprisecloud.domain.software.ToolsStatus;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="VirtualMachineReferenceType">
 * @author Jason King
 */
public class VirtualMachineReference extends Resource<VirtualMachineReference> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachineReference(this);
   }

   public static class Builder extends Resource.Builder<VirtualMachineReference> {

      private VirtualMachine.VirtualMachineStatus status;
      private int processorCount;
      private ResourceCapacity memory;
      private ResourceCapacity storage;
      private NamedResource operatingSystem;
      private boolean poweredOn;
      private ToolsStatus toolsStatus;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference#getStatus() 
       */
      public Builder status(VirtualMachine.VirtualMachineStatus status) {
         this.status = status;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference#getProcessorCount() 
       */
      public Builder processorCount(int processorCount) {
         this.processorCount = processorCount;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference#getMemory() 
       */
      public Builder memory(ResourceCapacity memory) {
         this.memory = memory;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference#getStorage() 
       */
      public Builder storage(ResourceCapacity storage) {
         this.storage = storage;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference#getOperatingSystem() 
       */
      public Builder operatingSystem(NamedResource operatingSystem) {
         this.operatingSystem = operatingSystem;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference#isPoweredOn() 
       */
      public Builder poweredOn(boolean poweredOn) {
         this.poweredOn = poweredOn;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference#getToolsStatus() 
       */
      public Builder toolsStatus(ToolsStatus toolsStatus) {
         this.toolsStatus = toolsStatus;
         return this;
      }

      @Override
      public VirtualMachineReference build() {
         return new VirtualMachineReference(href, type, name, links, actions,
                                            status,processorCount,memory,storage,operatingSystem,
                                            poweredOn,toolsStatus);
      }

      public Builder fromVirtualMachineReference(VirtualMachineReference in) {
        return fromResource(in).status(in.getStatus())
              .processorCount(in.getProcessorCount())
              .memory(in.getMemory())
              .storage(in.getStorage())
              .operatingSystem(in.getOperatingSystem())
              .poweredOn(in.isPoweredOn())
              .toolsStatus(in.getToolsStatus());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<VirtualMachineReference> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<VirtualMachineReference> in) {
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

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         super.fromAttributes(attributes);
         return this;
      }

   }

   @XmlElement(name = "Status", required = false)
   private VirtualMachine.VirtualMachineStatus status;
   
   @XmlElement(name = "ProcessorCount", required = false)
   private int processorCount;
   
   @XmlElement(name = "Memory", required = false)
   private ResourceCapacity memory;
   
   @XmlElement(name = "Storage", required = false)
   private ResourceCapacity storage;
   
   @XmlElement(name = "OperatingSystem", required = false)
   private NamedResource operatingSystem;
   
   @XmlElement(name = "PoweredOn", required = false)
   private boolean poweredOn;
   
   @XmlElement(name = "ToolsStatus", required = false)
   private ToolsStatus toolsStatus;

   private VirtualMachineReference(URI href, String type, String name, Set<Link> links, Set<Action> actions,
                                   @Nullable VirtualMachine.VirtualMachineStatus status, int processorCount, @Nullable ResourceCapacity memory,
                                   @Nullable ResourceCapacity storage, @Nullable NamedResource operatingSystem, boolean poweredOn,
                                   @Nullable ToolsStatus toolsStatus) {
      super(href, type, name, links, actions);
      this.status = status;
      this.processorCount = processorCount;
      this.memory = memory;
      this.storage = storage;
      this.operatingSystem = operatingSystem;
      this.poweredOn = poweredOn;
      this.toolsStatus = toolsStatus;
   }

   private VirtualMachineReference() {
        //For JAXB
   }

   public VirtualMachine.VirtualMachineStatus getStatus() {
      return status;
   }

   public int getProcessorCount() {
      return processorCount;
   }

   public ResourceCapacity getMemory() {
      return memory;
   }

   public ResourceCapacity getStorage() {
      return storage;
   }

   public NamedResource getOperatingSystem() {
      return operatingSystem;
   }

   public boolean isPoweredOn() {
      return poweredOn;
   }

   public ToolsStatus getToolsStatus() {
      return toolsStatus;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      VirtualMachineReference that = (VirtualMachineReference) o;

      if (poweredOn != that.poweredOn) return false;
      if (processorCount != that.processorCount) return false;
      if (memory != null ? !memory.equals(that.memory) : that.memory != null)
         return false;
      if (operatingSystem != null ? !operatingSystem.equals(that.operatingSystem) : that.operatingSystem != null)
         return false;
      if (status != that.status) return false;
      if (storage != null ? !storage.equals(that.storage) : that.storage != null)
         return false;
      if (toolsStatus != that.toolsStatus) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (status != null ? status.hashCode() : 0);
      result = 31 * result + processorCount;
      result = 31 * result + (memory != null ? memory.hashCode() : 0);
      result = 31 * result + (storage != null ? storage.hashCode() : 0);
      result = 31 * result + (operatingSystem != null ? operatingSystem.hashCode() : 0);
      result = 31 * result + (poweredOn ? 1 : 0);
      result = 31 * result + (toolsStatus != null ? toolsStatus.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", status="+status+", processorCount="+processorCount+", memory="+memory+
                            ", storage="+storage+", operatingSystem="+operatingSystem+", poweredOn="+poweredOn+
                            ", toolsStatus="+toolsStatus;
   }
}