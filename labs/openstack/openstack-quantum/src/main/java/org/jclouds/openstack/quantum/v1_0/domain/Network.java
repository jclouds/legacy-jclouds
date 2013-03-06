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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A Quantum network
 * 
 * @author Adam Lowe
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content/Networks.html">api doc</a>
*/
public class Network extends Reference {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromNetwork(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Reference.Builder<T>  {
      protected String name;
   
      /** 
       * @see Network#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      public Network build() {
         return new Network(id, name);
      }
      
      public T fromNetwork(Network in) {
         return super.fromReference(in)
                  .name(in.getName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;

   @ConstructorProperties({
      "id", "name"
   })
   protected Network(String id, String name) {
      super(id);
      this.name = checkNotNull(name, "name");
   }

   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Network that = Network.class.cast(obj);
      return super.equals(that) && Objects.equal(this.name, that.name);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("name", name);
   }
   
}
