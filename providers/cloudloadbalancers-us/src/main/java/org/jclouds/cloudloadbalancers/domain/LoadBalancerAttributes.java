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
package org.jclouds.cloudloadbalancers.domain;

import org.jclouds.cloudloadbalancers.domain.internal.BaseLoadBalancer;

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

   @Override
   public String toString() {
      return String.format("[algorithm=%s, name=%s, port=%s, protocol=%s]", algorithm, name, port, protocol);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((port == null) ? 0 : port.hashCode());
      result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
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
      LoadBalancerAttributes other = (LoadBalancerAttributes) obj;
      if (algorithm == null) {
         if (other.algorithm != null)
            return false;
      } else if (!algorithm.equals(other.algorithm))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (port == null) {
         if (other.port != null)
            return false;
      } else if (!port.equals(other.port))
         return false;
      if (protocol == null) {
         if (other.protocol != null)
            return false;
      } else if (!protocol.equals(other.protocol))
         return false;
      return true;
   }
}
