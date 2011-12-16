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

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual VirtualMachineReference elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * <xs:complexType name="VirtualMachineReferencesType">
 * @author Jason King
 */
public class VirtualMachineReferences {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVirtualMachineReferencesReferences(this);
   }

   public static class Builder {

       private Set<VirtualMachineReference> virtualMachineReferences = Sets.newLinkedHashSet();

       /**
        * @see org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReferences#getVirtualMachineReferences
        */
       public Builder virtualMachineReferences(Set<VirtualMachineReference> virtualMachineReferences) {
          this.virtualMachineReferences = Sets.newLinkedHashSet(checkNotNull(virtualMachineReferences, "virtualMachineReferences"));
          return this;
       }

       public Builder addVirtualMachineReference(VirtualMachineReference virtualMachineReference) {
          virtualMachineReferences.add(checkNotNull(virtualMachineReference, "virtualMachineReference"));
          return this;
       }

       public VirtualMachineReferences build() {
           return new VirtualMachineReferences(virtualMachineReferences);
       }

       public Builder fromVirtualMachineReferencesReferences(VirtualMachineReferences in) {
         return virtualMachineReferences(in.getVirtualMachineReferences());
       }
   }

   private VirtualMachineReferences() {
      //For JAXB and builder use
   }

   private VirtualMachineReferences(Set<VirtualMachineReference> virtualMachineReference) {
      this.virtualMachineReferences = Sets.newLinkedHashSet(virtualMachineReference);
   }

   @XmlElement(name = "VirtualMachine")
   private Set<VirtualMachineReference> virtualMachineReferences = Sets.newLinkedHashSet();
   
   public Set<VirtualMachineReference> getVirtualMachineReferences() {
      return Collections.unmodifiableSet(virtualMachineReferences);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VirtualMachineReferences that = (VirtualMachineReferences) o;

      if (virtualMachineReferences != null ? !virtualMachineReferences.equals(that.virtualMachineReferences) : that.virtualMachineReferences != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return virtualMachineReferences != null ? virtualMachineReferences.hashCode() : 0;
   }

   public String toString() {
      return "["+ virtualMachineReferences.toString()+"]";
   }
}
