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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01s02.html"
 *      />
 */
public class LoadBalancerAttributes {
   protected String name;
   protected String protocol;
   protected Integer port;
   protected String algorithm;

   public LoadBalancerAttributes name(String name) {
      this.name = name;
      return this;
   }

   public LoadBalancerAttributes protocol(String protocol) {
      this.protocol = protocol;
      return this;
   }

   public LoadBalancerAttributes port(int port) {
      this.port = port;
      return this;
   }

   public LoadBalancerAttributes algorithm(String algorithm) {
      this.algorithm = algorithm;
      return this;
   }

   public static <T extends BaseLoadBalancer<?, T>> LoadBalancerAttributes fromLoadBalancer(T lb) {
      return Builder.name(lb.getName()).port(lb.getPort()).protocol(lb.getProtocol()).algorithm(lb.getAlgorithm());
   }

   public static class Builder {
      public static LoadBalancerAttributes name(String name) {
         return new LoadBalancerAttributes().name(name);
      }

      public static LoadBalancerAttributes protocol(String protocol) {
         return new LoadBalancerAttributes().protocol(protocol);
      }

      public static LoadBalancerAttributes port(int port) {
         return new LoadBalancerAttributes().port(port);
      }

      public static LoadBalancerAttributes algorithm(String algorithm) {
         return new LoadBalancerAttributes().algorithm(algorithm);
      }
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("name", name).add("algorithm", algorithm).add("port", port).add("protocol", protocol);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, algorithm, port, protocol);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      LoadBalancerAttributes that = LoadBalancerAttributes.class.cast(obj);
      return Objects.equal(this.name, that.name)
            && Objects.equal(this.algorithm, that.algorithm)
            && Objects.equal(this.port, that.port)
            && Objects.equal(this.protocol, that.protocol);
   }
}
