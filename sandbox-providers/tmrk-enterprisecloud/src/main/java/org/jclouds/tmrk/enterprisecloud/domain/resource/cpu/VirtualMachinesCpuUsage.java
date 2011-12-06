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
package org.jclouds.tmrk.enterprisecloud.domain.resource.cpu;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual VirtualMachineCpuUsageDetail elements.
 * <xs:complexType name="CpuUsage_VirtualMachines">
 * @author Jason King
 */
public class VirtualMachinesCpuUsage {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromCpuUsageDetails(this);
   }

   public static class Builder {

       private Set<VirtualMachineCpuUsageDetail> virtualMachinesCpuUsageDetail = Sets.newLinkedHashSet();

       /**
        * @see VirtualMachinesCpuUsage#getVirtualMachinesCpuUsageDetail
        */
       public Builder vmCpuUsageDetails(Set<VirtualMachineCpuUsageDetail> vmCpuUsageDetails) {
          this.virtualMachinesCpuUsageDetail = Sets.newLinkedHashSet(checkNotNull(vmCpuUsageDetails, "vmCpuUsageDetails"));
          return this;
       }

       public Builder addVmCpuUsageDetail(VirtualMachineCpuUsageDetail vmCpuUsageDetail) {
          virtualMachinesCpuUsageDetail.add(checkNotNull(vmCpuUsageDetail,"vmCpuUsageDetail"));
          return this;
       }

       public VirtualMachinesCpuUsage build() {
           return new VirtualMachinesCpuUsage(virtualMachinesCpuUsageDetail);
       }

       public Builder fromCpuUsageDetails(VirtualMachinesCpuUsage in) {
         return vmCpuUsageDetails(in.getVirtualMachinesCpuUsageDetail());
       }
   }

   private VirtualMachinesCpuUsage() {
      //For JAXB and builder use
   }

   private VirtualMachinesCpuUsage(Set<VirtualMachineCpuUsageDetail> virtualMachinesCpuUsageDetail) {
      this.virtualMachinesCpuUsageDetail = Sets.newLinkedHashSet(virtualMachinesCpuUsageDetail);
   }

   @XmlElement(name = "VirtualMachine", required=false)
   private Set<VirtualMachineCpuUsageDetail> virtualMachinesCpuUsageDetail = Sets.newLinkedHashSet();

   public Set<VirtualMachineCpuUsageDetail> getVirtualMachinesCpuUsageDetail() {
      return Collections.unmodifiableSet(virtualMachinesCpuUsageDetail);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualMachinesCpuUsage that = (VirtualMachinesCpuUsage) o;

      if (virtualMachinesCpuUsageDetail != null ? !virtualMachinesCpuUsageDetail.equals(that.virtualMachinesCpuUsageDetail) : that.virtualMachinesCpuUsageDetail != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return virtualMachinesCpuUsageDetail != null ? virtualMachinesCpuUsageDetail.hashCode() : 0;
   }

   public String toString() {
      return "["+ virtualMachinesCpuUsageDetail.toString()+"]";
   }
}
