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
package org.jclouds.vcloud.director.v1_5.ovf;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The NetworkSection element shall list all logical networks used in the OVF package.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "NetworkSection", namespace = VCLOUD_OVF_NS)
public class NetworkSection extends SectionType<NetworkSection> {

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

   public static class Builder extends SectionType.Builder<NetworkSection> {
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
      public Builder fromSection(SectionType<NetworkSection> in) {
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

   private Set<Network> networks;

   private NetworkSection(String info, Iterable<Network> networks) {
      super(info);
      this.networks = ImmutableSet.<Network> copyOf(checkNotNull(networks, "networks"));
   }
   
   private NetworkSection() {
      // for JAXB
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
      return super.equals(other) && Objects.equal(networks, other.networks);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("networks", networks);
   }

}