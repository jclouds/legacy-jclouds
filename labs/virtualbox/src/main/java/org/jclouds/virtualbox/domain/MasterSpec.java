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

import com.google.common.base.Objects;

/**
 * A complete specification of a "master" node, including the ISO, networking setup and the physical
 * machine specification.
 */
public class MasterSpec {

   private VmSpec vmSpec;
   private IsoSpec isoSpec;
   private NetworkSpec networkSpec;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private VmSpec vmSpec;
      private IsoSpec isoSpec;
      private NetworkSpec networkSpec;

      public Builder vm(VmSpec vmSpec) {
         this.vmSpec = vmSpec;
         return this;
      }

      public Builder network(NetworkSpec networkSpec) {
         this.networkSpec = networkSpec;
         return this;
      }

      public Builder iso(IsoSpec isoSpec) {
         this.isoSpec = isoSpec;
         return this;
      }

      public MasterSpec build() {
         return new MasterSpec(vmSpec, isoSpec, networkSpec);
      }

   }

   private MasterSpec(VmSpec vmSpec, IsoSpec isoSpec, NetworkSpec networkSpec) {
      checkNotNull(vmSpec, "vmSpec");
      checkNotNull(isoSpec, "isoSpec");
      checkNotNull(networkSpec, "networkSpec");
      this.vmSpec = vmSpec;
      this.isoSpec = isoSpec;
      this.networkSpec = networkSpec;
   }

   public VmSpec getVmSpec() {
      return vmSpec;
   }

   public IsoSpec getIsoSpec() {
      return isoSpec;
   }

   public NetworkSpec getNetworkSpec() {
      return networkSpec;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o instanceof VmSpec) {
         MasterSpec other = (MasterSpec) o;
         return Objects.equal(vmSpec, other.vmSpec) && Objects.equal(isoSpec, other.isoSpec)
                  && Objects.equal(networkSpec, other.networkSpec);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vmSpec, isoSpec, networkSpec);
   }

   @Override
   public String toString() {
      return "IMachineSpec{" + "vmSpec=" + vmSpec + ", isoSpec=" + isoSpec + ", networkSpec=" + networkSpec + '}';
   }
}
