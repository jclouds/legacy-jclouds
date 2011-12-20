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
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="ComputePoolPerformanceStatistic">
 * @author Jason King
 * 
 */
public class ComputePoolPerformanceStatistic {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromComputePoolPerformanceStatistic(this);
   }

   public static class Builder {
      private NamedResource cpu;
      private NamedResource memory;

     /**
      * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolPerformanceStatistic#getCpu
      */
      public Builder cpu(NamedResource cpu) {
         this.cpu = cpu;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolPerformanceStatistic#getMemory
       */
      public Builder memory(NamedResource memory) {
         this.memory = memory;
         return this;
      }

      public ComputePoolPerformanceStatistic build() {
         return new ComputePoolPerformanceStatistic(cpu, memory);
      }

      public Builder fromComputePoolPerformanceStatistic(ComputePoolPerformanceStatistic in) {
         return cpu(in.getCpu()).memory(in.getMemory());
      }
   }

   @XmlElement(name = "Cpu", required = false)
   private NamedResource cpu;

   @XmlElement(name = "Memory", required = false)
   private NamedResource memory;

   private ComputePoolPerformanceStatistic(@Nullable NamedResource cpu, @Nullable NamedResource memory) {
      this.cpu = cpu;
      this.memory = memory;
    }

   private ComputePoolPerformanceStatistic() {
       //For JAXB
   }

   public NamedResource getCpu() {
      return cpu;
   }

   public NamedResource getMemory() {
      return memory;
   }

   @Override
   public String toString() {
      return "cpu="+cpu+", memory="+memory;
   }
}