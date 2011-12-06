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
package org.jclouds.tmrk.enterprisecloud.domain.resource.memory;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual VirtualMachineMemoryUsageDetail elements.
 * <xs:complexType name="MemoryUsage_VirtualMachines">
 * @author Jason King
 */
public class VirtualMachinesMemoryUsage {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMemoryUsageDetails(this);
   }

   public static class Builder {

       private Set<VirtualMachineMemoryUsageDetail> virtualMachinesMemoryUsageDetail = Sets.newLinkedHashSet();

       /**
        * @see VirtualMachinesMemoryUsage#getVirtualMachinesMemoryUsageDetail
        */
       public Builder vmMemoryUsageDetails(Set<VirtualMachineMemoryUsageDetail> vmMemoryUsageDetails) {
          this.virtualMachinesMemoryUsageDetail = Sets.newLinkedHashSet(checkNotNull(vmMemoryUsageDetails, "vmMemoryUsageDetails"));
          return this;
       }

       public Builder addVmMemoryUsageDetail(VirtualMachineMemoryUsageDetail vmMemoryUsageDetail) {
          virtualMachinesMemoryUsageDetail.add(checkNotNull(vmMemoryUsageDetail,"vmMemoryUsageDetail"));
          return this;
       }

       public VirtualMachinesMemoryUsage build() {
           return new VirtualMachinesMemoryUsage(virtualMachinesMemoryUsageDetail);
       }

       public Builder fromMemoryUsageDetails(VirtualMachinesMemoryUsage in) {
         return vmMemoryUsageDetails(in.getVirtualMachinesMemoryUsageDetail());
       }
   }

   private VirtualMachinesMemoryUsage() {
      //For JAXB and builder use
   }

   private VirtualMachinesMemoryUsage(Set<VirtualMachineMemoryUsageDetail> virtualMachinesMemoryUsageDetail) {
      this.virtualMachinesMemoryUsageDetail = Sets.newLinkedHashSet(virtualMachinesMemoryUsageDetail);
   }

   @XmlElement(name = "VirtualMachine", required=false)
   private Set<VirtualMachineMemoryUsageDetail> virtualMachinesMemoryUsageDetail = Sets.newLinkedHashSet();

   public Set<VirtualMachineMemoryUsageDetail> getVirtualMachinesMemoryUsageDetail() {
      return Collections.unmodifiableSet(virtualMachinesMemoryUsageDetail);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualMachinesMemoryUsage that = (VirtualMachinesMemoryUsage) o;

      if (virtualMachinesMemoryUsageDetail != null ? !virtualMachinesMemoryUsageDetail.equals(that.virtualMachinesMemoryUsageDetail) : that.virtualMachinesMemoryUsageDetail != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return virtualMachinesMemoryUsageDetail != null ? virtualMachinesMemoryUsageDetail.hashCode() : 0;
   }

   public String toString() {
      return "["+ virtualMachinesMemoryUsageDetail.toString()+"]";
   }
}
