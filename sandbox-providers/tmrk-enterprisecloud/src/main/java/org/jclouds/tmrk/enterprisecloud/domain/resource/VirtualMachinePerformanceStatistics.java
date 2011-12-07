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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual VirtualMachinePerformanceStatistic elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * @author Jason King
 */
public class VirtualMachinePerformanceStatistics {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVirtualMachinePerformanceStatistics(this);
   }

   public static class Builder {

       private Set<VirtualMachinePerformanceStatistic> virtualMachines = Sets.newLinkedHashSet();

       /**
        * @see VirtualMachinePerformanceStatistics#getVirtualMachinesPerformanceStatistics
        */
       public Builder virtualMachines(Set<VirtualMachinePerformanceStatistic> virtualMachines) {
          this.virtualMachines = Sets.newLinkedHashSet(checkNotNull(virtualMachines, "virtualMachines"));
          return this;
       }

       public VirtualMachinePerformanceStatistics build() {
           return new VirtualMachinePerformanceStatistics(virtualMachines);
       }

       public Builder fromVirtualMachinePerformanceStatistics(VirtualMachinePerformanceStatistics in) {
         return virtualMachines(in.getVirtualMachinesPerformanceStatistics());
       }
   }

   @XmlElement(name = "VirtualMachine", required = false)
   private Set<VirtualMachinePerformanceStatistic> virtualMachines = Sets.newLinkedHashSet();

   private VirtualMachinePerformanceStatistics() {
      //For JAXB
   }

   private VirtualMachinePerformanceStatistics(Set<VirtualMachinePerformanceStatistic> virtualMachines) {
      this.virtualMachines = ImmutableSet.copyOf(virtualMachines);
   }

   public Set<VirtualMachinePerformanceStatistic> getVirtualMachinesPerformanceStatistics() {
      return Collections.unmodifiableSet(virtualMachines);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualMachinePerformanceStatistics links1 = (VirtualMachinePerformanceStatistics) o;

      if (!virtualMachines.equals(links1.virtualMachines)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return virtualMachines.hashCode();
   }

   public String toString() {
      return "["+ virtualMachines.toString()+"]";
   }
}
