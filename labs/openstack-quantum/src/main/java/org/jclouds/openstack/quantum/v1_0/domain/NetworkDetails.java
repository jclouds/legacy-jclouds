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
package org.jclouds.openstack.quantum.v1_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Details of a Quantum network
 * 
 * @author Adam Lowe
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content/Networks.html">api doc</a>
*/
public class NetworkDetails extends Network {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromNetworkDetails(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Network.Builder<T>  {
      protected Set<Port> ports = ImmutableSet.of();
   
      /** 
       * @see NetworkDetails#getPorts()
       */
      public T ports(Set<Port> ports) {
         this.ports = ImmutableSet.copyOf(checkNotNull(ports, "ports"));      
         return self();
      }

      public T ports(Port... in) {
         return ports(ImmutableSet.copyOf(in));
      }

      public NetworkDetails build() {
         return new NetworkDetails(id, name, ports);
      }
      
      public T fromNetworkDetails(NetworkDetails in) {
         return super.fromNetwork(in)
                  .ports(in.getPorts());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Set<Port> ports;

   @ConstructorProperties({
      "id", "name", "ports"
   })
   protected NetworkDetails(String id, String name, Set<Port> ports) {
      super(id, name);
      this.ports = ImmutableSet.copyOf(checkNotNull(ports, "ports"));      
   }

   public Set<Port> getPorts() {
      return this.ports;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ports);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      NetworkDetails that = NetworkDetails.class.cast(obj);
      return super.equals(that) && Objects.equal(this.ports, that.ports);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("ports", ports);
   }
   
}
