/*
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
package org.jclouds.opsource.servers.domain;

import static com.google.common.base.Objects.equal;
import static org.jclouds.opsource.servers.OpSourceNameSpaces.SERVER;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * 
 * 
 */
@XmlRootElement(name = "machineSpecification", namespace = SERVER)
public class MachineSpecification {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMachineSpecification(this);
   }

   public static class Builder {

      private int cpuCount;
      private long memoryMb;
      private long osStorageGb;
      private long additionalLocalStorageGb;
      private OperatingSystem operatingSystem;

      public Builder cpuCount(int cpuCount) {
         this.cpuCount = cpuCount;
         return this;
      }

      public Builder memoryMb(long memoryMb) {
         this.memoryMb = memoryMb;
         return this;
      }      
     
      public Builder osStorageGb(long osStorageGb) {
         this.osStorageGb = osStorageGb;
         return this;
      }

      public Builder additionalLocalStorageGb(long additionalLocalStorageGb) {
         this.additionalLocalStorageGb = additionalLocalStorageGb;
         return this;
      }
      
      public Builder operatingSystem(OperatingSystem operatingSystem) {
         this.operatingSystem = operatingSystem;
         return this;
      }
      
      public MachineSpecification build() {
         return new MachineSpecification(cpuCount, memoryMb, osStorageGb, additionalLocalStorageGb, operatingSystem);
      }

      public Builder fromMachineSpecification(MachineSpecification in) {
         return new Builder().cpuCount(cpuCount).memoryMb(memoryMb).osStorageGb(osStorageGb).additionalLocalStorageGb(additionalLocalStorageGb)
         	.operatingSystem(operatingSystem);
      }
   }

   private MachineSpecification() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = SERVER)
   private int cpuCount;
   @XmlElement(namespace = SERVER)
   private long memoryMb;
   @XmlElement(namespace = SERVER)
   private long osStorageGb;
   @XmlElement(namespace = SERVER)
   private long additionalLocalStorageGb;
   @XmlElement(namespace = SERVER)
   private OperatingSystem operatingSystem;

   private MachineSpecification(int cpuCount, long memoryMb, long osStorageGb, long additionalLocalStorageGb, OperatingSystem operatingSystem) {
      this.cpuCount = cpuCount;
      this.memoryMb = memoryMb;
      this.osStorageGb = osStorageGb;
      this.additionalLocalStorageGb = additionalLocalStorageGb;
      this.operatingSystem = operatingSystem;
   }

   public int getCpuCount() {
      return cpuCount;
   }
   
   public long getMemoryMb() {
      return memoryMb;
   }

   public long getOsStorageGb() {
      return osStorageGb;
   }
   
   public long getAdditionalLocalStorageGb() {
      return additionalLocalStorageGb;
   }  
   
   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      MachineSpecification that = MachineSpecification.class.cast(o);
      return equal(this, that);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(cpuCount, memoryMb, osStorageGb, additionalLocalStorageGb, operatingSystem);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("cpuCount", cpuCount).add("memoryMb", memoryMb).add("osStorageGb", osStorageGb)
      	.add("additionalLocalStorageGb", additionalLocalStorageGb).add("operatingSystem", operatingSystem).toString();
   }

}
