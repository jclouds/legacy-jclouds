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
 * A Quantum port
 * 
 * @author Adam Lowe
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content/Ports.html">api doc</a>
*/
public class Port extends Reference {

   /**
    */
   public static enum State {
      ACTIVE, DOWN
   }

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromPort(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Reference.Builder<T>  {
      protected Port.State state;
   
      /** 
       * @see Port#getState()
       */
      public T state(Port.State state) {
         this.state = state;
         return self();
      }

      public Port build() {
         return new Port(id, state);
      }
      
      public T fromPort(Port in) {
         return super.fromReference(in)
                  .state(in.getState());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Port.State state;

   @ConstructorProperties({
      "id", "state"
   })
   protected Port(String id, Port.State state) {
      super(id);
      this.state = checkNotNull(state, "state");
   }

   public Port.State getState() {
      return this.state;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(state);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Port that = Port.class.cast(obj);
      return super.equals(that) && Objects.equal(this.state, that.state);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("state", state);
   }
   
}
