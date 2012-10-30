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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The NetworkSection element shall list all logical networks used in the OVF package.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "NetworkSection")
@XmlType(name = "NetworkSection_Type")
public class NetworkSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromNetworkSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {
      protected Set<Network> networks = Sets.newLinkedHashSet();

      /**
       * @see NetworkSection#getNetworks
       */
      public B network(Network network) {
         this.networks.add(checkNotNull(network, "network"));
         return self();
      }

      /**
       * @see NetworkSection#getNetworks
       */
      public B networks(Iterable<Network> networks) {
         this.networks = ImmutableSet.<Network> copyOf(checkNotNull(networks, "networks"));
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public NetworkSection build() {
         return new NetworkSection(this);
      }

      public B fromNetworkSection(NetworkSection in) {
         return networks(in.getNetworks()).info(in.getInfo());
      }
   }

   private Set<Network> networks;

   private NetworkSection(Builder<?> builder) {
      super(builder);
      this.networks = ImmutableSet.copyOf(checkNotNull(networks, "networks"));
   }
   
   private NetworkSection() {
      // for JAXB
   }

   /**
    * All networks referred to from Connection elements in all {@link VirtualHardwareSection}
    * elements shall be defined in the NetworkSection.
    */
   public Set<Network> getNetworks() {
      return networks;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), networks);
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
      return super.equals(other)
            && Objects.equal(networks, other.networks);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("networks", networks);
   }

}
