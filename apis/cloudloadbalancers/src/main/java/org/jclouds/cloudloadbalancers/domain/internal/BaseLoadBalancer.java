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
package org.jclouds.cloudloadbalancers.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.SortedSet;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01s02.html"
 *      />
 */
public class BaseLoadBalancer<N extends BaseNode<N>, T extends BaseLoadBalancer<N, T>> implements
         Comparable<BaseLoadBalancer<N, T>> {

   public static <N extends BaseNode<N>, T extends BaseLoadBalancer<N, T>> Builder<N, T> builder() {
      return new Builder<N, T>();
   }

   @SuppressWarnings("unchecked")
   public Builder<N, T> toBuilder() {
      return new Builder<N, T>().from((T) this);
   }

   public static class Builder<N extends BaseNode<N>, T extends BaseLoadBalancer<N, T>> {
      protected String name;
      protected String protocol;
      protected Integer port;
      protected String algorithm;
      protected Set<N> nodes = Sets.newLinkedHashSet();

      public Builder<N, T> name(String name) {
         this.name = name;
         return this;
      }

      public Builder<N, T> protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder<N, T> port(Integer port) {
         this.port = port;
         return this;
      }

      public Builder<N, T> algorithm(String algorithm) {
         this.algorithm = algorithm;
         return this;
      }

      public Builder<N, T> nodes(Iterable<N> nodes) {
         this.nodes = ImmutableSet.<N> copyOf(checkNotNull(nodes, "nodes"));
         return this;
      }

      @SuppressWarnings("unchecked")
      public Builder<N, T> node(N node) {
         this.nodes.add((N) checkNotNull(nodes, "nodes"));
         return this;
      }

      public BaseLoadBalancer<N, T> build() {
         return new BaseLoadBalancer<N, T>(name, protocol, port, algorithm, nodes);
      }

      public Builder<N, T> from(T baseLoadBalancer) {
         return name(baseLoadBalancer.getName()).port(baseLoadBalancer.getPort()).protocol(
                  baseLoadBalancer.getProtocol()).algorithm(baseLoadBalancer.getAlgorithm()).nodes(
                  baseLoadBalancer.getNodes());
      }
   }

   // for serialization only
   protected BaseLoadBalancer() {

   }

   protected String name;
   protected String protocol;
   protected Integer port;
   protected String algorithm;
   // so tests will come out consistently
   protected SortedSet<N> nodes = ImmutableSortedSet.of();

   public BaseLoadBalancer(String name, String protocol, Integer port, @Nullable String algorithm, Iterable<N> nodes) {
      this.name = checkNotNull(name, "name");
      this.protocol = protocol;// null on deleted LB
      this.port = port;// null on deleted LB
      this.algorithm = algorithm;// null on deleted LB
      this.nodes = ImmutableSortedSet.copyOf(checkNotNull(nodes, "nodes"));
   }

   @Override
   public int compareTo(BaseLoadBalancer<N, T> arg0) {
      return name.compareTo(arg0.name);
   }

   public String getName() {
      return name;
   }

   public String getProtocol() {
      return protocol;
   }

   public Integer getPort() {
      return port;
   }

   /**
    * 
    * @return algorithm, which may be null if the load balancer is deleted
    */
   @Nullable
   public String getAlgorithm() {
      return algorithm;
   }

   public Set<N> getNodes() {
      return nodes;
   }

   @Override
   public int hashCode() {
      final Integer prime = 31;
      Integer result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseLoadBalancer<?, ?> other = (BaseLoadBalancer<?, ?>) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[name=%s, port=%s, protocol=%s, algorithm=%s, nodes=%s]", name, port, protocol, algorithm,
               nodes);
   }
}
