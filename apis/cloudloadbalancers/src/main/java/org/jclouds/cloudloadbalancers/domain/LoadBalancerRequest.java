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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.jclouds.cloudloadbalancers.domain.internal.BaseLoadBalancer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01s02.html"
 *      />
 */
public class LoadBalancerRequest extends BaseLoadBalancer<NodeRequest, LoadBalancerRequest> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().from(this);
   }

   public static class Builder extends BaseLoadBalancer.Builder<NodeRequest, LoadBalancerRequest> {
      private VirtualIP.Type virtualIPType;
      private Integer virtualIPId;
      private List<Map<String, String>> virtualIps;

      public Builder virtualIPId(Integer virtualIPId) {
         this.virtualIPId = virtualIPId;
         return this;
      }

      public Builder virtualIPType(VirtualIP.Type virtualIPType) {
         this.virtualIPType = virtualIPType;
         return this;
      }

      public LoadBalancerRequest build() {
         return virtualIps == null ? new LoadBalancerRequest(name, protocol, port, algorithm, nodes, virtualIPType,
                  virtualIPId) : new LoadBalancerRequest(name, protocol, port, algorithm, nodes, virtualIps);
      }

      @Override
      public Builder nodes(Iterable<NodeRequest> nodes) {
         this.nodes = ImmutableSet.<NodeRequest> copyOf(checkNotNull(nodes, "nodes"));
         return this;
      }

      @Override
      public Builder node(NodeRequest nodes) {
         this.nodes.add(checkNotNull(nodes, "nodes"));
         return this;
      }

      private Builder virtualIPs(List<Map<String, String>> virtualIPs) {
         this.virtualIps = virtualIPs;
         return this;
      }

      @Override
      public Builder algorithm(String algorithm) {
         return Builder.class.cast(super.algorithm(algorithm));
      }

      @Override
      public Builder from(LoadBalancerRequest in) {
         return Builder.class.cast(super.from(in)).virtualIPs(in.virtualIps);
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      @Override
      public Builder port(Integer port) {
         return Builder.class.cast(super.port(port));
      }

      @Override
      public Builder protocol(String protocol) {
         return Builder.class.cast(super.protocol(protocol));
      }

   }

   private final List<Map<String, String>> virtualIps;

   public LoadBalancerRequest(String name, String protocol, int port, String algorithm, Iterable<NodeRequest> nodes,
            VirtualIP.Type virtualIPType, Integer virtualIPId) {
      this(name, protocol, port, algorithm, nodes, getVirtualIPsFromOptions(virtualIPType, virtualIPId));
   }

   private LoadBalancerRequest(String name, String protocol, int port, String algorithm, Iterable<NodeRequest> nodes,
            List<Map<String, String>> virtualIPsFromOptions) {
      super(name, protocol, port, algorithm, nodes);
      this.virtualIps = checkNotNull(virtualIPsFromOptions, "virtualIPsFromOptions");
   }

   static List<Map<String, String>> getVirtualIPsFromOptions(VirtualIP.Type virtualIPType, Integer virtualIPId) {
      checkArgument(virtualIPType == null || virtualIPId == null,
               "virtualIPType and virtualIPId cannot both be specified");
      if (virtualIPType != null)
         return ImmutableList.<Map<String, String>> of(ImmutableMap.of("type", virtualIPType.name()));
      else if (virtualIPId != null)
         return ImmutableList.<Map<String, String>> of(ImmutableMap.of("id", virtualIPId.toString()));
      else
         throw new IllegalArgumentException("virtualIPType or virtualIPId must be specified");
   }

   @Override
   public String toString() {
      return String.format("[algorithm=%s, name=%s, nodes=%s, port=%s, protocol=%s, virtualIps=%s]", algorithm, name,
               nodes, port, protocol, virtualIps);
   }

}
