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

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Describes the network configuration for a VirtualBox machine.
 */
public class NetworkSpec {

   private final List<NetworkInterfaceCard> networkInterfaceCards;

   public NetworkSpec(final List<NetworkInterfaceCard> networkInterfaceCards) {
      this.networkInterfaceCards = ImmutableList.copyOf(checkNotNull(networkInterfaceCards, "networkInterfaceCards"));
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private List<NetworkInterfaceCard> networkInterfaceCards = Lists.newArrayList();

      public Builder addNIC(NetworkInterfaceCard networkInterfaceCard) {
         this.networkInterfaceCards.add(networkInterfaceCard);
         return this;
      }

      public NetworkSpec build() {
         return new NetworkSpec(networkInterfaceCards);
      }
   }

   public List<NetworkInterfaceCard> getNetworkInterfaceCards() {
      return networkInterfaceCards;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o instanceof VmSpec) {
         NetworkSpec other = (NetworkSpec) o;
         return Objects.equal(networkInterfaceCards, other.networkInterfaceCards);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkInterfaceCards);
   }

   @Override
   public String toString() {
      return "NetworkSpec{" + "networkInterfaceCards= " + networkInterfaceCards + '}';
   }
}
