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

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A complete specification of a "clone" node with networking setup
 * and the physical machine specification.
 */
public class CloneSpec {

   private VmSpec vmSpec;
   private NetworkSpec networkSpec;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private VmSpec vmSpec;
      private NetworkSpec networkSpec;

      public Builder vm(VmSpec vmSpec) {
         this.vmSpec = vmSpec;
         return this;
      }

      public Builder network(NetworkSpec networkSpec) {
         this.networkSpec = networkSpec;
         return this;
      }

      public CloneSpec build() {
         return new CloneSpec(vmSpec, networkSpec);
      }

   }

   public CloneSpec(VmSpec vmSpec, NetworkSpec networkSpec) {
      checkNotNull(vmSpec, "vmSpec");
      checkNotNull(networkSpec, "networkSpec");
      this.vmSpec = vmSpec;
      this.networkSpec = networkSpec;
   }

   public VmSpec getVmSpec() {
      return vmSpec;
   }

   public NetworkSpec getNetworkSpec() {
      return networkSpec;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof VmSpec) {
         CloneSpec other = (CloneSpec) o;
         return Objects.equal(vmSpec, other.vmSpec) &&
                 Objects.equal(networkSpec, other.networkSpec);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vmSpec,networkSpec);
   }

   @Override
   public String toString() {
      return "IMachineSpec{" +
              "vmSpec= " + vmSpec +
              ", networkSpec= " + networkSpec +
              '}';
   }
}
