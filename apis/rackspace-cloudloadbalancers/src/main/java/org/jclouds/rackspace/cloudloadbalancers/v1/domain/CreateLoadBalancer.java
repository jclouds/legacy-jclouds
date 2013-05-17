/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseLoadBalancer;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Used to create Load Balancers.
 * 
 * @author Adrian Cole
 */
public class CreateLoadBalancer extends BaseLoadBalancer<AddNode, CreateLoadBalancer> {

   private final Set<Map<String, String>> virtualIps;
   private final Set<AccessRule> accessRules;
   private final Map<String, String> metadata;

   public CreateLoadBalancer(String name, String protocol, @Nullable Integer port, Set<AddNode> addNodes,
         @Nullable Algorithm algorithm, @Nullable Integer timeout, @Nullable Boolean halfClosed,
         @Nullable Map<String, SessionPersistence> sessionPersistenceType,
         @Nullable Map<String, Boolean> connectionLogging, @Nullable ConnectionThrottle connectionThrottle,
         @Nullable HealthMonitor healthMonitor, @Nullable Set<AccessRule> accessRules,
         @Nullable Map<String, String> metadata, VirtualIP.Type virtualIPType, Integer virtualIPId) {
      this(name, protocol, port, addNodes, algorithm, timeout, halfClosed, sessionPersistenceType, connectionLogging,
            connectionThrottle, healthMonitor, accessRules, metadata, 
            getVirtualIPsFromOptions(virtualIPType, virtualIPId));
   }

   public CreateLoadBalancer(String name, String protocol, @Nullable Integer port, Set<AddNode> addNodes,
         @Nullable Algorithm algorithm, @Nullable Integer timeout, @Nullable Boolean halfClosed,
         @Nullable Map<String, SessionPersistence> sessionPersistenceType,
         @Nullable Map<String, Boolean> connectionLogging, @Nullable ConnectionThrottle connectionThrottle,
         @Nullable HealthMonitor healthMonitor, @Nullable Set<AccessRule> accessRules,
         @Nullable Map<String, String> metadata, Set<Map<String, String>> virtualIPsFromOptions) {
      super(name, protocol, port, addNodes, algorithm, timeout, halfClosed, sessionPersistenceType, connectionLogging,
            connectionThrottle, healthMonitor);
      this.virtualIps = checkNotNull(virtualIPsFromOptions, "virtualIPsFromOptions");
      this.accessRules = accessRules;
      this.metadata = metadata;
   }   

   public Map<String, String> getMetadata() {
      return metadata != null ? metadata : ImmutableMap.<String, String> of();
   }

   public Set<AccessRule> getAccessRules() {
      return accessRules != null ? accessRules : ImmutableSet.<AccessRule> of();
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
            .add("healthMonitor", healthMonitor).add("accessRules", accessRules).add("metadata", metadata)
            .add("virtualIps", virtualIps);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder extends BaseLoadBalancer.Builder<AddNode, CreateLoadBalancer> {
      private VirtualIP.Type virtualIPType;
      private Integer virtualIPId;
      private Set<Map<String, String>> virtualIps;
      private Set<AccessRule> accessRules;
      private Map<String, String> metadata;

      /**
       * @see VirtualIP
       */
      public Builder virtualIPId(Integer virtualIPId) {
         this.virtualIPId = virtualIPId;
         return this;
      }

      /**
       * @see VirtualIP
       */
      public Builder virtualIPType(VirtualIP.Type virtualIPType) {
         this.virtualIPType = virtualIPType;
         return this;
      }

      /**
       * @see VirtualIP
       */
      private Builder virtualIPs(Set<Map<String, String>> virtualIPs) {
         this.virtualIps = virtualIPs;
         return this;
      }

      /**
       * The access list management feature allows fine-grained network access controls to be applied to the load 
       * balancer's virtual IP address.
       * 
       * @see AccessRule
       */
      public Builder accessRules(Iterable<AccessRule> accessRules) {
         this.accessRules = ImmutableSet.<AccessRule> copyOf(checkNotNull(accessRules, "accessRules"));
         return this;
      }

      /**
       * Information (metadata) that can be associated with each load balancer for the client's personal use.
       */
      public Builder metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.<String, String> copyOf(checkNotNull(metadata, "metadata"));
         return this;
      }

      public CreateLoadBalancer build() {
         if (virtualIps == null) {
            return new CreateLoadBalancer(name, protocol, port, nodes, algorithm, timeout, halfClosed,
                  sessionPersistence, connectionLogging, connectionThrottle, healthMonitor, accessRules, metadata,
                  virtualIPType, virtualIPId);
         }
         else {
            return new CreateLoadBalancer(name, protocol, port, nodes, algorithm, timeout, halfClosed,
                  sessionPersistence, connectionLogging, connectionThrottle, healthMonitor, accessRules, metadata,
                  virtualIps);
         }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder nodes(Iterable<AddNode> addNodes) {
         this.nodes = ImmutableSet.<AddNode> copyOf(checkNotNull(addNodes, "addNodes"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder node(AddNode node) {
         this.nodes.add(checkNotNull(node, "node"));
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

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder from(CreateLoadBalancer in) {
         return Builder.class.cast(super.from(in)).virtualIPs(in.virtualIps);
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
