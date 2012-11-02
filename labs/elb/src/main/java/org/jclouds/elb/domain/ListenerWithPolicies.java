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
package org.jclouds.elb.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class ListenerWithPolicies extends Listener {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromListenerWithPolicies(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Listener.Builder<T> {

      private ImmutableSet.Builder<String> policyNames = ImmutableSet.<String> builder();

      /**
       * @see ListenerWithPolicies#getPolicyNames()
       */
      public T policyNames(Iterable<String> policyNames) {
         this.policyNames.addAll(checkNotNull(policyNames, "policyNames"));
         return self();
      }

      /**
       * @see ListenerWithPolicies#getPolicyNames()
       */
      public T policyName(String policyName) {
         this.policyNames.add(checkNotNull(policyName, "policyName"));
         return self();
      }

      @Override
      public ListenerWithPolicies build() {
         return new ListenerWithPolicies(instancePort, instanceProtocol, port, protocol, SSLCertificateId, policyNames
                  .build());
      }

      public T fromListenerWithPolicies(ListenerWithPolicies in) {
         return fromListener(in).policyNames(in.getPolicyNames());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Set<String> policyNames;

   protected ListenerWithPolicies(int instancePort, Protocol instanceProtocol, int port, Protocol protocol,
            Optional<String> SSLCertificateId, Iterable<String> policyNames) {
      super(instancePort, instanceProtocol, port, protocol, SSLCertificateId);
      this.policyNames = ImmutableSet.copyOf(checkNotNull(policyNames, "policyNames"));
   }

   /**
    * A list of policies enabled for this listener. An empty list indicates that no policies are
    * enabled.
    */
   public Set<String> getPolicyNames() {
      return policyNames;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ListenerWithPolicies that = ListenerWithPolicies.class.cast(o);
      return super.equals(that) && equal(this.policyNames, that.policyNames);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), policyNames);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("policyNames", policyNames);
   }
}
