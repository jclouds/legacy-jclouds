/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The NetworkSection element shall list all logical networks used in the OVF package.
 * 
 * @author Adrian Cole
 */
public class NetworkSection extends Section<NetworkSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromNetworkSection(this);
   }

   public static class Builder extends Section.Builder<NetworkSection> {
      protected Set<Network> networks = Sets.newLinkedHashSet();

      /**
       * @see NetworkSection#getNetworks
       */
      public Builder network(Network network) {
         this.networks.add(checkNotNull(network, "network"));
         return this;
      }

      /**
       * @see NetworkSection#getNetworks
       */
      public Builder networks(Iterable<Network> networks) {
         this.networks = ImmutableSet.<Network> copyOf(checkNotNull(networks, "networks"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public NetworkSection build() {
         return new NetworkSection(info, networks);
      }

      public Builder fromNetworkSection(NetworkSection in) {
         return networks(in.getNetworks()).info(in.getInfo());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<NetworkSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   private final Set<Network> networks;

   public NetworkSection(String info, Iterable<Network> networks) {
      super(info);
      this.networks = ImmutableSet.<Network> copyOf(checkNotNull(networks, "networks"));
   }

   /**
    * All networks referred to from Connection elements in all {@link VirtualHardwareSection}
    * elements shall be defined in the NetworkSection.
    * 
    * @return
    */
   public Set<Network> getNetworks() {
      return networks;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result + ((networks == null) ? 0 : networks.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      NetworkSection other = (NetworkSection) obj;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (networks == null) {
         if (other.networks != null)
            return false;
      } else if (!networks.equals(other.networks))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[info=%s, networks=%s]", info, networks);
   }

}
