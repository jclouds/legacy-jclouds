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

import static com.google.common.base.Preconditions.checkNotNull;

import org.virtualbox_4_2.IMachine;

import com.google.common.base.Objects;

/**
 * A complete specification of a "clone" node with networking setup and the physical machine
 * specification.
 */
public class CloneSpec {

   private final VmSpec vmSpec;
   private final NetworkSpec networkSpec;
   private final IMachine master;
   private final boolean isLinked;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private VmSpec vmSpec;
      private NetworkSpec networkSpec;
      private IMachine master;
      private boolean isLinked;

      public Builder vm(VmSpec vmSpec) {
         this.vmSpec = vmSpec;
         return this;
      }

      public Builder network(NetworkSpec networkSpec) {
         this.networkSpec = networkSpec;
         return this;
      }

      public Builder master(IMachine master) {
         this.master = master;
         return this;
      }

      public Builder linked(boolean isLinked) {
         this.isLinked = isLinked;
         return this;
      }

      public CloneSpec build() {
         return new CloneSpec(vmSpec, networkSpec, master, isLinked);
      }

   }

   public CloneSpec(VmSpec vmSpec, NetworkSpec networkSpec, IMachine master, boolean isLinked) {
      this.vmSpec = checkNotNull(vmSpec, "vmSpec can't be null");
      this.networkSpec =  checkNotNull(networkSpec, "networkSpec can't be null");
      this.master =  checkNotNull(master, "master can't be null");
      this.isLinked = isLinked;
   }

   public VmSpec getVmSpec() {
      return vmSpec;
   }

   public NetworkSpec getNetworkSpec() {
      return networkSpec;
   }

   public IMachine getMaster() {
      return master;
   }

   public boolean isLinked() {
      return isLinked;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o instanceof VmSpec) {
         CloneSpec other = (CloneSpec) o;
         return Objects.equal(vmSpec, other.vmSpec) && Objects.equal(networkSpec, other.networkSpec);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vmSpec, networkSpec);
   }

   @Override
   public String toString() {
      return "CloneSpec{" + "vmSpec= " + vmSpec + ", networkSpec= " + networkSpec + '}';
   }
}
