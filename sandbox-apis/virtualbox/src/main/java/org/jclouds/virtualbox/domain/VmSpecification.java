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

package org.jclouds.virtualbox.domain;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A description of a Virtual Machine in VirtualBox.
 */
public class VmSpecification {

   private final String vmName;
   private final String osTypeId;
   private final String vmId;
   private final boolean forceOverwrite;
   private final Map<Long, NatAdapter> natNetworkAdapters;
   private final Set<StorageController> controllers;

   public VmSpecification(String vmId, String vmName, String osTypeId, boolean forceOverwrite, Set<StorageController> controllers, Map<Long, NatAdapter> natNetworkAdapters) {
      this.vmId = vmId;
      this.vmName = vmName;
      this.osTypeId = osTypeId;
      this.controllers = controllers;
      this.forceOverwrite = forceOverwrite;
      this.natNetworkAdapters = natNetworkAdapters;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private Set<StorageController> controllers = new HashSet<StorageController>();
      private String name;
      private String id;
      private String osTypeId = "";
      private boolean forceOverwrite;
      private Map<Long, NatAdapter> natNetworkAdapters = new HashMap<Long, NatAdapter>();

      public Builder controller(StorageController controller) {
         controllers.add(controller);
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder osTypeId(String osTypeId) {
         this.osTypeId = osTypeId;
         return this;
      }

      public Builder forceOverwrite(boolean forceOverwrite) {
         this.forceOverwrite = forceOverwrite;
         return this;
      }

      public Builder natNetworkAdapter(int slot, NatAdapter adapter) {
         this.natNetworkAdapters.put((long) slot, adapter);
         return this;
      }


      public VmSpecification build() {
         checkNotNull(name, "name");
         checkNotNull(id, "id");
         return new VmSpecification(id, name, osTypeId, forceOverwrite, controllers, natNetworkAdapters);
      }
   }

   public String getVmName() {
      return vmName;
   }

   public String getOsTypeId() {
      return osTypeId;
   }

   public String getVmId() {
      return vmId;
   }

   public boolean isForceOverwrite() {
      return forceOverwrite;
   }

   public Set<StorageController> getControllers() {
      return Collections.unmodifiableSet(controllers);
   }

   public Map<Long, NatAdapter> getNatNetworkAdapters() {
      return Collections.unmodifiableMap(natNetworkAdapters);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VmSpecification that = (VmSpecification) o;

      if (forceOverwrite != that.forceOverwrite) return false;
      if (controllers != null ? !controllers.equals(that.controllers) : that.controllers != null) return false;
      if (natNetworkAdapters != null ? !natNetworkAdapters.equals(that.natNetworkAdapters) : that.natNetworkAdapters != null)
         return false;
      if (osTypeId != null ? !osTypeId.equals(that.osTypeId) : that.osTypeId != null) return false;
      if (vmId != null ? !vmId.equals(that.vmId) : that.vmId != null) return false;
      if (vmName != null ? !vmName.equals(that.vmName) : that.vmName != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = vmName != null ? vmName.hashCode() : 0;
      result = 31 * result + (osTypeId != null ? osTypeId.hashCode() : 0);
      result = 31 * result + (vmId != null ? vmId.hashCode() : 0);
      result = 31 * result + (forceOverwrite ? 1 : 0);
      result = 31 * result + (natNetworkAdapters != null ? natNetworkAdapters.hashCode() : 0);
      result = 31 * result + (controllers != null ? controllers.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "VmSpecification{" +
              "vmName='" + vmName + '\'' +
              ", osTypeId='" + osTypeId + '\'' +
              ", vmId='" + vmId + '\'' +
              ", forceOverwrite=" + forceOverwrite +
              ", natNetworkAdapters=" + natNetworkAdapters +
              ", controllers=" + controllers +
              '}';
   }
}
