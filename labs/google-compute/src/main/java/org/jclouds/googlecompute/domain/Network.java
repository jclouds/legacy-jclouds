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
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a network used to enable instance communication.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/networks"/>
 */
public class Network extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromNetwork(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private String IPv4Range;
      private String gatewayIPv4;

      /**
       * @see Network#getIPv4Range()
       */
      public T IPv4Range(String IPv4Range) {
         this.IPv4Range = IPv4Range;
         return self();
      }

      /**
       * @see Network#getGatewayIPv4()
       */
      public T gatewayIPv4(String gatewayIPv4) {
         this.gatewayIPv4 = gatewayIPv4;
         return self();
      }

      public Network build() {
         return new Network(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, IPv4Range, gatewayIPv4);
      }

      public T fromNetwork(Network in) {
         return super.fromResource(in);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String IPv4Range;
   private final String gatewayIPv4;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "IPv4Range",
           "gatewayIPv4"
   })
   protected Network(String id, Date creationTimestamp, String selfLink, String name, String description,
                     String IPv4Range, String gatewayIPv4) {
      super(Resource.Kind.NETWORK, id, creationTimestamp, selfLink, name, description);
      this.IPv4Range = checkNotNull(IPv4Range);
      this.gatewayIPv4 = gatewayIPv4;
   }

   /**
    * @return Required; The range of internal addresses that are legal on this network. This range is a CIDR
    *         specification, for example: 192.168.0.0/16. Provided by the client when the network is created.
    */
   @Nullable
   public String getIPv4Range() {
      return IPv4Range;
   }

   /**
    * @return an optional address that is used for default routing to other networks. This must be within the range
    *         specified by IPv4Range, and is typically the first usable address in that range. If not specified,
    *         the default value is the first usable address in IPv4Range.
    */
   public String getGatewayIPv4() {
      return gatewayIPv4;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, IPv4Range,
              gatewayIPv4);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Network that = Network.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.IPv4Range, that.IPv4Range)
              && Objects.equal(this.gatewayIPv4, that.gatewayIPv4);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("IPv4Range", IPv4Range).add("gatewayIPv4", gatewayIPv4);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

}
