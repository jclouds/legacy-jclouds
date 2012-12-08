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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecompute.domain.Resource.nullCollectionOnNullOrEmpty;

/**
 * A network interface for an Instance.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances"/>
 */
public class InstanceNetworkInterface {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromNetworkInterface(this);
   }

   public static class Builder {

      private String name;
      private String network;
      private String networkIP;
      private ImmutableSet.Builder<InstanceNetworkInterfaceAccessConfig> accessConfigs = ImmutableSet.builder();

      /**
       * @see InstanceNetworkInterface#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see InstanceNetworkInterface#getNetwork()
       */
      public Builder network(String network) {
         this.network = network;
         return this;
      }

      /**
       * @see InstanceNetworkInterface#getNetworkIP()
       */
      public Builder networkIP(String networkIP) {
         this.networkIP = networkIP;
         return this;
      }

      /**
       * @see InstanceNetworkInterface#getAccessConfigs()
       */
      public Builder addAccessConfig(InstanceNetworkInterfaceAccessConfig accessConfig) {
         this.accessConfigs.add(accessConfig);
         return this;
      }

      /**
       * @see InstanceNetworkInterface#getAccessConfigs()
       */
      public Builder accessConfigs(Set<InstanceNetworkInterfaceAccessConfig> accessConfigs) {
         this.accessConfigs.addAll(accessConfigs);
         return this;
      }

      public InstanceNetworkInterface build() {
         return new InstanceNetworkInterface(this.name, this.network, this.networkIP, this.accessConfigs.build());
      }

      public Builder fromNetworkInterface(InstanceNetworkInterface in) {
         return this.network(in.getNetwork()).networkIP(in.getNetworkIP()).accessConfigs(in.getAccessConfigs());
      }
   }

   private final String name;
   private final String network;
   private final String networkIP;
   private final Set<InstanceNetworkInterfaceAccessConfig> accessConfigs;

   @ConstructorProperties({
           "name", "network", "networkIP", "accessConfigs"
   })
   public InstanceNetworkInterface(String name, String network, String networkIP,
                                   Set<InstanceNetworkInterfaceAccessConfig>
                                           accessConfigs) {
      this.name = name;
      this.network = checkNotNull(network);
      this.networkIP = networkIP;
      this.accessConfigs = nullCollectionOnNullOrEmpty(accessConfigs);
   }

   /**
    * @return the name of the network interface
    */
   public String getName() {
      return name;
   }

   /**
    * @return URL of the network resource attached to this interface.
    */
   public String getNetwork() {
      return network;
   }

   /**
    * @return An optional IPV4 internal network address to assign to this instance. If not specified,
    *         one will be assigned from the available range.
    */
   @Nullable
   public String getNetworkIP() {
      return networkIP;
   }

   /**
    * @return array of configurations for this interface. This specifies how this interface is configured to interact
    *         with other network services, such as connecting to the internet. Currently, ONE_TO_ONE_NAT
    *         is the only access config supported. If there are no accessConfigs specified, then this instance
    *         will have no external internet access.
    */
   public Set<InstanceNetworkInterfaceAccessConfig> getAccessConfigs() {
      return accessConfigs;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, network, networkIP, accessConfigs);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      InstanceNetworkInterface that = InstanceNetworkInterface.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.network, that.network)
              && equal(this.networkIP, that.networkIP)
              && equal(this.accessConfigs, that.accessConfigs);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("name", name).add("network", network).add("networkIP", networkIP).add("accessConfigs",
                      accessConfigs);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }


}
