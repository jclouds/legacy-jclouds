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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.nova.v1_1.domain.Address.Type;
import org.jclouds.openstack.nova.v1_1.util.NovaUtils;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * A server is a virtual machine instance in the compute system. Flavor and image are requisite
 * elements when creating a server.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Get_Server_Details-d1e2623.html"
 *      />
 */
public class Server extends Resource {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromServer(this);
   }

   public static class Builder extends Resource.Builder {
      private Multimap<Address.Type, Address> addresses = LinkedHashMultimap.create();

      /**
       * @see Server#getAddresses()
       */
      public Builder addresses(Multimap<Address.Type, Address> addresses) {
         this.addresses = ImmutableMultimap.copyOf(checkNotNull(addresses, "addresses"));
         return this;
      }

      /**
       * @see Server#getPrivateAddresses()
       */
      public Builder privateAddresses(Address... privateAddresses) {
         return privateAddresses(ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses")));
      }

      /**
       * @see Server#getPrivateAddresses()
       */
      public Builder privateAddresses(Set<Address> privateAddresses) {
         this.addresses.replaceValues(Address.Type.PRIVATE, ImmutableSet.copyOf(checkNotNull(privateAddresses,
                  "privateAddresses")));
         return this;
      }

      /**
       * @see Server#getPublicAddresses()
       */
      public Builder publicAddresses(Address... publicAddresses) {
         return publicAddresses(ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses")));
      }

      /**
       * @see Server#getPublicAddresses()
       */
      public Builder publicAddresses(Set<Address> publicAddresses) {
         this.addresses.replaceValues(Address.Type.PUBLIC, ImmutableSet.copyOf(checkNotNull(publicAddresses,
                  "publicAddresses")));
         return this;
      }

      public Server build() {
         return new Server(id, name, links, addresses);
      }

      public Builder fromServer(Server in) {
         return fromResource(in).addresses(in.getAddresses());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource in) {
         return Builder.class.cast(super.fromResource(in));
      }
   }

   // TODO: get gson multimap adapter!
   protected final Map<Address.Type, Set<Address>> addresses;

   protected Server(String id, String name, Set<Link> links, Multimap<Address.Type, Address> addresses) {
      super(id, name, links);
      this.addresses = NovaUtils.toOldSchool(ImmutableMultimap.copyOf(checkNotNull(addresses, "addresses")));
   }

   /**
    * @return the private ip addresses assigned to the server
    */
   public Set<Address> getPrivateAddresses() {
      return ImmutableSet.copyOf(addresses.get(Address.Type.PRIVATE));
   }

   /**
    * @return the public ip addresses assigned to the server
    */
   public Set<Address> getPublicAddresses() {
      return ImmutableSet.copyOf(addresses.get(Address.Type.PUBLIC));
   }

   /**
    * @return the ip addresses assigned to the server
    */
   public Multimap<Type, Address> getAddresses() {
      return NovaUtils.fromOldSchool(addresses);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("links", links).add("addresses", addresses)
               .toString();
   }

}
