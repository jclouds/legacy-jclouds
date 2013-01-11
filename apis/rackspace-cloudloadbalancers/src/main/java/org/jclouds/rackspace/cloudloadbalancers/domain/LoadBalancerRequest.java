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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rackspace.cloudloadbalancers.domain.internal.BaseLoadBalancer;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Used to create Load Balancers.
 * 
 * @author Adrian Cole
 */
public class LoadBalancerRequest extends BaseLoadBalancer<NodeRequest, LoadBalancerRequest> {

   private final Set<Map<String, String>> virtualIps;

   public LoadBalancerRequest(String name, String protocol, @Nullable Integer port, Set<NodeRequest> nodes,
         @Nullable Algorithm algorithm, @Nullable Integer timeout, @Nullable Boolean halfClosed,
         @Nullable Map<String, SessionPersistenceType> sessionPersistenceType,
         @Nullable Map<String, Boolean> connectionLogging, @Nullable ConnectionThrottle connectionThrottle,
         @Nullable HealthMonitor healthMonitor, @Nullable Set<AccessRule> accessRules,
         @Nullable Set<Metadata> metadata, VirtualIP.Type virtualIPType, Integer virtualIPId) {
      this(name, protocol, port, nodes, algorithm, timeout, halfClosed, sessionPersistenceType, connectionLogging,
            connectionThrottle, healthMonitor, accessRules, metadata, getVirtualIPsFromOptions(virtualIPType,
                  virtualIPId));
   }

   public LoadBalancerRequest(String name, String protocol, @Nullable Integer port, Set<NodeRequest> nodes,
         @Nullable Algorithm algorithm, @Nullable Integer timeout, @Nullable Boolean halfClosed,
         @Nullable Map<String, SessionPersistenceType> sessionPersistenceType,
         @Nullable Map<String, Boolean> connectionLogging, @Nullable ConnectionThrottle connectionThrottle,
         @Nullable HealthMonitor healthMonitor, @Nullable Set<AccessRule> accessRules,
         @Nullable Set<Metadata> metadata, Set<Map<String, String>> virtualIPsFromOptions) {
      super(name, protocol, port, nodes, algorithm, timeout, halfClosed, sessionPersistenceType, connectionLogging,
            connectionThrottle, healthMonitor, accessRules, metadata);
      this.virtualIps = checkNotNull(virtualIPsFromOptions, "virtualIPsFromOptions");
   }

   static Set<Map<String, String>> getVirtualIPsFromOptions(VirtualIP.Type virtualIPType, Integer virtualIPId) {
      checkArgument(virtualIPType == null || virtualIPId == null,
            "virtualIPType and virtualIPId cannot both be specified");
      if (virtualIPType != null)
         return ImmutableSet.<Map<String, String>> of(ImmutableMap.of("type", virtualIPType.name()));
      else if (virtualIPId != null)
         return ImmutableSet.<Map<String, String>> of(ImmutableMap.of("id", virtualIPId.toString()));
      else
         throw new IllegalArgumentException("virtualIPType or virtualIPId must be specified");
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("protocol", protocol)
            .add("port", port).add("nodes", nodes).add("timeout", timeout).add("algorithm", algorithm)
            .add("timeout", timeout).add("sessionPersistenceType", getSessionPersistenceType())
            .add("connectionLogging", isConnectionLogging()).add("connectionThrottle", connectionThrottle)
            .add("healthMonitor", healthMonitor).add("accessRules", accessList).add("metadata", metadata)
            .add("virtualIps", virtualIps);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder extends BaseLoadBalancer.Builder<NodeRequest, LoadBalancerRequest> {
      private VirtualIP.Type virtualIPType;
      private Integer virtualIPId;
      private Set<Map<String, String>> virtualIps;

      public Builder virtualIPId(Integer virtualIPId) {
         this.virtualIPId = virtualIPId;
         return this;
      }

      public Builder virtualIPType(VirtualIP.Type virtualIPType) {
         this.virtualIPType = virtualIPType;
         return this;
      }

      private Builder virtualIPs(Set<Map<String, String>> virtualIPs) {
         this.virtualIps = virtualIPs;
         return this;
      }

      public LoadBalancerRequest build() {
         if (virtualIps == null) {
            return new LoadBalancerRequest(name, protocol, port, nodes, algorithm, timeout, halfClosed,
                  sessionPersistence, connectionLogging, connectionThrottle, healthMonitor, accessRules, metadata,
                  virtualIPType, virtualIPId);
         }
         else {
            return new LoadBalancerRequest(name, protocol, port, nodes, algorithm, timeout, halfClosed,
                  sessionPersistence, connectionLogging, connectionThrottle, healthMonitor, accessRules, metadata,
                  virtualIps);
         }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder nodes(Iterable<NodeRequest> nodes) {
         this.nodes = ImmutableSet.<NodeRequest> copyOf(checkNotNull(nodes, "nodes"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder node(NodeRequest nodes) {
         this.nodes.add(checkNotNull(nodes, "nodes"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder algorithm(Algorithm algorithm) {
         return Builder.class.cast(super.algorithm(algorithm));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder from(LoadBalancerRequest in) {
         return Builder.class.cast(super.from(in)).virtualIPs(in.virtualIps);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder port(Integer port) {
         return Builder.class.cast(super.port(port));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder protocol(String protocol) {
         return Builder.class.cast(super.protocol(protocol));
      }

   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
