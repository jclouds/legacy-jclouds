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
package org.jclouds.tmrk.enterprisecloud.domain.resource;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="ComputePoolResourceSummary">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "ComputePoolResourceSummary")
public class ComputePoolResourceSummary extends Resource<ComputePoolResourceSummary> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromComputePoolResourceSummary(this);
   }

   public static class Builder extends Resource.Builder<ComputePoolResourceSummary> {
      private Date startTime;
      private Date endTime;
      private CpuComputeResourceSummary cpu;
      private MemoryComputeResourceSummary memory;
      private StorageResourceSummary storage;
      private VirtualMachineResourceSummary virtualMachines;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary#getStartTime
       */
      public Builder startTime(Date startTime) {
         this.startTime = startTime;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary#getEndTime
       */
      public Builder endTime(Date endTime) {
         this.endTime = endTime;
         return this;
      }


     /**
      * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary#getCpu
      */
      public Builder cpu(CpuComputeResourceSummary cpu) {
         this.cpu = cpu;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary#getMemory
       */
      public Builder memory(MemoryComputeResourceSummary memory) {
         this.memory = memory;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary#getStorage
       */
      public Builder storage(StorageResourceSummary storage) {
         this.storage = storage;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary#getVirtualMachines
       */
      public Builder virtualMachines(VirtualMachineResourceSummary virtualMachines) {
         this.virtualMachines = virtualMachines;
         return this;
      }

      @Override
      public ComputePoolResourceSummary build() {
         return new ComputePoolResourceSummary(href, type, name, links, actions,
               startTime, endTime, cpu, memory, storage, virtualMachines);
      }

      public Builder fromComputePoolResourceSummary(ComputePoolResourceSummary in) {
         return fromResource(in).startTime(in.getStartTime()).endTime(in.getEndTime());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<ComputePoolResourceSummary> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<ComputePoolResourceSummary> in) {
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
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
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
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "StartTime", required = true)
   private Date startTime;

   @XmlElement(name = "EndTime", required = true)
   private Date endTime;

   @XmlElement(name = "Cpu", required = false)
   private CpuComputeResourceSummary cpu;

   @XmlElement(name = "Memory", required = false)
   private MemoryComputeResourceSummary memory;

   @XmlElement(name = "Storage", required = false)
   private StorageResourceSummary storage;

   @XmlElement(name = "VirtualMachines", required = false)
   private VirtualMachineResourceSummary virtualMachines;

   private ComputePoolResourceSummary(URI href, String type, String name, Set<Link> links, Set<Action> actions, Date startTime, Date completedTime,
                                      @Nullable CpuComputeResourceSummary cpu, @Nullable MemoryComputeResourceSummary memory, @Nullable StorageResourceSummary storage, @Nullable VirtualMachineResourceSummary virtualMachines) {
      super(href, type, name, links, actions);
      this.startTime = checkNotNull(startTime, "startTime");
      this.endTime = checkNotNull(endTime, "endTime");
      this.cpu = cpu;
      this.memory = memory;
      this.storage = storage;
      this.virtualMachines = virtualMachines;
   }

   private ComputePoolResourceSummary() {
       //For JAXB
   }

   public Date getStartTime() {
      return startTime;
   }

   public Date getEndTime() {
      return endTime;
   }

   public CpuComputeResourceSummary getCpu() {
      return cpu;
   }

   public MemoryComputeResourceSummary getMemory() {
      return memory;
   }

   public StorageResourceSummary getStorage() {
      return storage;
   }

   public VirtualMachineResourceSummary getVirtualMachines() {
      return virtualMachines;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      ComputePoolResourceSummary that = (ComputePoolResourceSummary) o;

      if (cpu != null ? !cpu.equals(that.cpu) : that.cpu != null) return false;
      if (!endTime.equals(that.endTime)) return false;
      if (memory != null ? !memory.equals(that.memory) : that.memory != null)
         return false;
      if (!startTime.equals(that.startTime)) return false;
      if (storage != null ? !storage.equals(that.storage) : that.storage != null)
         return false;
      if (virtualMachines != null ? !virtualMachines.equals(that.virtualMachines) : that.virtualMachines != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + startTime.hashCode();
      result = 31 * result + endTime.hashCode();
      result = 31 * result + (cpu != null ? cpu.hashCode() : 0);
      result = 31 * result + (memory != null ? memory.hashCode() : 0);
      result = 31 * result + (storage != null ? storage.hashCode() : 0);
      result = 31 * result + (virtualMachines != null ? virtualMachines.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", startTime="+startTime+", endTime="+endTime+", cpu="+cpu+", memory="+memory+", storage="+storage+", virtualMachines="+virtualMachines;
   }

}