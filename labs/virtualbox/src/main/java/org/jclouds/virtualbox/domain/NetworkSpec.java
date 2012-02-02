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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes the network configuration for a VirtualBox machine.
 */
public class NetworkSpec {

   private final Map<Long, NatAdapter> natNetworkAdapters;

   public NetworkSpec(final Map<Long, NatAdapter> natNetworkAdapters) {
      this.natNetworkAdapters = checkNotNull(natNetworkAdapters, "natNetworkAdapters");
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private Map<Long, NatAdapter> natNetworkAdapters = new HashMap<Long, NatAdapter>();

      public Builder natNetworkAdapter(int slot, NatAdapter adapter) {
         this.natNetworkAdapters.put((long) slot, adapter);
         return this;
      }

      public NetworkSpec build() {
         return new NetworkSpec(natNetworkAdapters);
      }
   }


   public Map<Long, NatAdapter> getNatNetworkAdapters() {
      return Collections.unmodifiableMap(natNetworkAdapters);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof VmSpec) {
         NetworkSpec other = (NetworkSpec) o;
         return Objects.equal(natNetworkAdapters, other.natNetworkAdapters);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(natNetworkAdapters);
   }


   @Override
   public String toString() {
      return "NetworkSpec{" +
              "natNetworkAdapters=" + natNetworkAdapters +
              '}';
   }
}
