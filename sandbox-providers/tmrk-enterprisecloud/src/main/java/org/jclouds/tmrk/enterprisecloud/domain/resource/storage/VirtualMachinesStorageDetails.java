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
package org.jclouds.tmrk.enterprisecloud.domain.resource.storage;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual VirtualMachineStorageUsageDetail elements.
 * <xs:complexType name="StorageDetails_VirtualMachines">
 * @author Jason King
 */
public class VirtualMachinesStorageDetails {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromStorageUsageDetails(this);
   }

   public static class Builder {

       private Set<VirtualMachineStorageUsageDetail> virtualMachinesStorageUsageDetail = Sets.newLinkedHashSet();

       /**
        * @see org.jclouds.tmrk.enterprisecloud.domain.resource.storage.VirtualMachinesStorageDetails#getVirtualMachinesStorageUsageDetail
        */
       public Builder vmStorageUsageDetails(Set<VirtualMachineStorageUsageDetail> vmStorageUsageDetails) {
          this.virtualMachinesStorageUsageDetail = Sets.newLinkedHashSet(checkNotNull(vmStorageUsageDetails, "vmStorageUsageDetails"));
          return this;
       }

       public Builder addVmStorageUsageDetail(VirtualMachineStorageUsageDetail vmStorageUsageDetails) {
          virtualMachinesStorageUsageDetail.add(checkNotNull(vmStorageUsageDetails,"vmStorageUsageDetails"));
          return this;
       }

       public VirtualMachinesStorageDetails build() {
           return new VirtualMachinesStorageDetails(virtualMachinesStorageUsageDetail);
       }

       public Builder fromStorageUsageDetails(VirtualMachinesStorageDetails in) {
         return vmStorageUsageDetails(in.getVirtualMachinesStorageUsageDetail());
       }
   }

   private VirtualMachinesStorageDetails() {
      //For JAXB and builder use
   }

   private VirtualMachinesStorageDetails(Set<VirtualMachineStorageUsageDetail> virtualMachinesStorageUsageDetail) {
      this.virtualMachinesStorageUsageDetail = Sets.newLinkedHashSet(virtualMachinesStorageUsageDetail);
   }

   @XmlElement(name = "VirtualMachine", required=false)
   private Set<VirtualMachineStorageUsageDetail> virtualMachinesStorageUsageDetail = Sets.newLinkedHashSet();

   public Set<VirtualMachineStorageUsageDetail> getVirtualMachinesStorageUsageDetail() {
      return Collections.unmodifiableSet(virtualMachinesStorageUsageDetail);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualMachinesStorageDetails that = (VirtualMachinesStorageDetails) o;

      if (virtualMachinesStorageUsageDetail != null ? !virtualMachinesStorageUsageDetail.equals(that.virtualMachinesStorageUsageDetail) : that.virtualMachinesStorageUsageDetail != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return virtualMachinesStorageUsageDetail != null ? virtualMachinesStorageUsageDetail.hashCode() : 0;
   }

   public String toString() {
      return "["+ virtualMachinesStorageUsageDetail.toString()+"]";
   }
}
