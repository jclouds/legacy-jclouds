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
package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents features of a network.
 *
 * @author danikov
 */
@XmlRootElement(name = "Features")
public class NetworkFeatures {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkFeatures(this);
   }

   public static class Builder {

      private Set<NetworkServiceType<?>> services = Sets.newLinkedHashSet();

      /**
       * @see NetworkFeatures#getNetworkServices()
       */
      public Builder services(Set<? extends NetworkServiceType<?>> services) {
         this.services = Sets.newLinkedHashSet(checkNotNull(services, "services"));
         return this;
      }

      /**
       * @see NetworkFeatures#getNetworkServices()
       */
      public Builder service(NetworkServiceType<?> service) {
         services.add(checkNotNull(service, "service"));
         return this;
      }

      public NetworkFeatures build() {
         return new NetworkFeatures(services);
      }

      public Builder fromNetworkFeatures(NetworkFeatures in) {
         return services(in.getNetworkServices());
      }
   }

   NetworkFeatures() {
      // for JAXB
   }

   public NetworkFeatures(Set<? extends NetworkServiceType<?>> services) {
      this.services = ImmutableSet.copyOf(services);
   }

   @XmlElementRef
   private Set<? extends NetworkServiceType<?>> services = Sets.newLinkedHashSet();

   /**
    * @return a Network service. May be any of DhcpService, NatService, IpsecVpnService,
    *         DhcpService, or StaticRoutingService.
    */
   public Set<? extends NetworkServiceType<?>> getNetworkServices() {
      return Collections.unmodifiableSet(services);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkFeatures that = NetworkFeatures.class.cast(o);
      return equal(services, that.services);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(services);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("services", services).toString();
   }
}
