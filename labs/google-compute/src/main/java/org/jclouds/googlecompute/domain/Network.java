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


import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a network used to enable instance communication.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/networks"/>
 */
@Beta
public final class Network extends Resource {

   private final String IPv4Range;
   private final Optional<String> gatewayIPv4;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "IPv4Range",
           "gatewayIPv4"
   })
   protected Network(String id, Date creationTimestamp, URI selfLink, String name, String description,
                     String IPv4Range, String gatewayIPv4) {
      super(Kind.NETWORK, checkNotNull(id, "id of %s", name), fromNullable(creationTimestamp), checkNotNull(selfLink,
              "selfLink of %s", name), checkNotNull(name, "name"), fromNullable(description));
      this.IPv4Range = checkNotNull(IPv4Range);
      this.gatewayIPv4 = fromNullable(gatewayIPv4);
   }

   /**
    * @return Required; The range of internal addresses that are legal on this network. This range is a CIDR
    *         specification, for example: 192.168.0.0/16.
    */
   public String getIPv4Range() {
      return IPv4Range;
   }

   /**
    * This must be within the range specified by IPv4Range, and is typically the first usable address in that range.
    * If not specified, the default value is the first usable address in IPv4Range.
    *
    * @return an optional address that is used for default routing to other networks.
    */
   public Optional<String> getGatewayIPv4() {
      return gatewayIPv4;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("IPv4Range", IPv4Range)
              .add("gatewayIPv4", gatewayIPv4.orNull());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetwork(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private String IPv4Range;
      private String gatewayIPv4;

      /**
       * @see Network#getIPv4Range()
       */
      public Builder IPv4Range(String IPv4Range) {
         this.IPv4Range = IPv4Range;
         return this;
      }

      /**
       * @see Network#getGatewayIPv4()
       */
      public Builder gatewayIPv4(String gatewayIPv4) {
         this.gatewayIPv4 = gatewayIPv4;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Network build() {
         return new Network(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, IPv4Range, gatewayIPv4);
      }

      public Builder fromNetwork(Network in) {
         return super.fromResource(in);
      }
   }

}
