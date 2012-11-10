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
package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import org.virtualbox_4_2.CleanupMode;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * A description of a Virtual Machine in VirtualBox.
 */
public class VmSpec {

   private final String vmName;
   private final String osTypeId;
   private final String vmId;
   private final long memory;
   private final boolean forceOverwrite;
   private final Set<StorageController> controllers;
   private final CleanupMode cleanupMode;

   public VmSpec(String vmId, String vmName, String osTypeId, long memory, String guestUser, String guestPassword, boolean forceOverwrite,
            Set<StorageController> controllers, CleanupMode cleanupMode) {
      this.vmId = checkNotNull(vmId, "vmId can't be null");
      this.vmName = checkNotNull(vmName, "vmName can't be null");
      this.osTypeId = checkNotNull(osTypeId, "osTypeId can't be null");
      checkArgument(memory > 0, "memory must be > 0");
      this.memory = memory;
      this.controllers = checkNotNull(controllers, "controllers can't be null");
      this.cleanupMode = checkNotNull(cleanupMode, "cleanupMode can't be null");
      this.forceOverwrite = forceOverwrite;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private Set<StorageController> controllers = Sets.newHashSet();

      private String name;
      private String id;
      private String osTypeId = "";
      private boolean forceOverwrite = true;
      private long memory;
      private String guestUser;
      private String guestPassword;
      private CleanupMode cleanUpMode;

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

      public Builder memoryMB(int memorySize) {
         this.memory = (long) memorySize;
         return this;
      }

      public Builder cleanUpMode(CleanupMode cleanupMode) {
         this.cleanUpMode = cleanupMode;
         return this;
      }
      
      public Builder guestUser(String guestUser) {
         this.guestUser = guestUser;
         return this;
      }  
      
      public Builder guestPassword(String guestPassword) {
         this.guestPassword = guestPassword;
         return this;
      }    

      public VmSpec build() {
         checkNotNull(name, "name");
         checkNotNull(id, "id");
         checkArgument(memory > 0, "Memory must be set");
         return new VmSpec(id, name, osTypeId, memory, guestUser, guestPassword, forceOverwrite, controllers, cleanUpMode);
      }

   }

   public String getVmId() {
      return vmId;
   }

   public String getVmName() {
      return vmName;
   }

   public String getOsTypeId() {
      return osTypeId;
   }

   public long getMemory() {
      return memory;
   }

   public boolean isForceOverwrite() {
      return forceOverwrite;
   }

   public Set<StorageController> getControllers() {
      return Collections.unmodifiableSet(controllers);
   }

   public CleanupMode getCleanupMode() {
      return cleanupMode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o instanceof VmSpec) {
         VmSpec other = (VmSpec) o;
         return Objects.equal(vmId, other.vmId) && Objects.equal(vmName, other.vmName)
                  && Objects.equal(osTypeId, other.osTypeId) && Objects.equal(memory, other.memory)
                  && Objects.equal(forceOverwrite, other.forceOverwrite)
                  && Objects.equal(controllers, other.controllers) && Objects.equal(cleanupMode, other.cleanupMode);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vmId, vmName, osTypeId, memory, forceOverwrite, controllers);
   }

   @Override
   public String toString() {
      return "VmSpecification{" + "vmName='" + vmName + '\'' + ", osTypeId='" + osTypeId + '\'' + ", memory='" + memory
            + '\'' + ", vmId='" + vmId + '\'' + '\''
            + ", forceOverwrite=" + forceOverwrite + ", controllers="
            + controllers + ", cleanupMode=" + cleanupMode + '}';
   }
}
