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
package org.jclouds.rackspace.cloudloadbalancers.domain;

import org.jclouds.rackspace.cloudloadbalancers.domain.internal.BaseLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.internal.BaseLoadBalancer.Algorithm;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Used to update Load Balancers.
 * 
 * @author Adrian Cole
 */
public class LoadBalancerAttributes {

   protected String name;
   protected String protocol;
   protected Integer port;
   protected Algorithm algorithm;
   protected Integer timeout;
   protected Boolean halfClosed;

   public LoadBalancerAttributes name(String name) {
      this.name = name;
      return this;
   }

   public LoadBalancerAttributes protocol(String protocol) {
      this.protocol = protocol;
      return this;
   }

   public LoadBalancerAttributes port(Integer port) {
      this.port = port;
      return this;
   }

   public LoadBalancerAttributes algorithm(Algorithm algorithm) {
      this.algorithm = algorithm;
      return this;
   }

   public LoadBalancerAttributes timeout(Integer timeout) {
      this.timeout = timeout;
      return this;
   }

   public LoadBalancerAttributes halfClosed(Boolean halfClosed) {
      this.halfClosed = halfClosed;
      return this;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("algorithm", algorithm)
            .add("port", port).add("protocol", protocol).add("timeout", timeout).add("halfClosed", halfClosed);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, algorithm, port, protocol, timeout, halfClosed);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;

      LoadBalancerAttributes that = LoadBalancerAttributes.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.algorithm, that.algorithm)
            && Objects.equal(this.port, that.port) && Objects.equal(this.protocol, that.protocol)
            && Objects.equal(this.timeout, that.timeout) && Objects.equal(this.halfClosed, that.halfClosed);
   }

   public static class Builder {
      /**
       * @see BaseLoadBalancer.Builder#name(String)
       */
      public static LoadBalancerAttributes name(String name) {
         return new LoadBalancerAttributes().name(name);
      }

      /**
       * @see BaseLoadBalancer.Builder#protocol(String)
       */
      public static LoadBalancerAttributes protocol(String protocol) {
         return new LoadBalancerAttributes().protocol(protocol);
      }

      /**
       * @see BaseLoadBalancer.Builder#port(Integer)
       */
      public static LoadBalancerAttributes port(Integer port) {
         return new LoadBalancerAttributes().port(port);
      }

      /**
       * @see BaseLoadBalancer.Builder#algorithm(Algorithm)
       */
      public static LoadBalancerAttributes algorithm(Algorithm algorithm) {
         return new LoadBalancerAttributes().algorithm(algorithm);
      }

      /**
       * @see BaseLoadBalancer.Builder#timeout(Integer)
       */
      public static LoadBalancerAttributes timeout(Integer timeout) {
         return new LoadBalancerAttributes().timeout(timeout);
      }

      /**
       * @see BaseLoadBalancer.Builder#halfClosed(Boolean)
       */
      public static LoadBalancerAttributes halfClosed(Boolean halfClosed) {
         return new LoadBalancerAttributes().halfClosed(halfClosed);
      }
   }

   public static <T extends BaseLoadBalancer<?, T>> LoadBalancerAttributes fromLoadBalancer(T lb) {
      return Builder.name(lb.getName()).port(lb.getPort()).protocol(lb.getProtocol()).algorithm(lb.getAlgorithm())
            .timeout(lb.getTimeout()).halfClosed(lb.isHalfClosed());
   }
}
