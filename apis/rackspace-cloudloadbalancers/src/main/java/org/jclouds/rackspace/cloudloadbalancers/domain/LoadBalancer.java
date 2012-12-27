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

import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rackspace.cloudloadbalancers.domain.internal.BaseLoadBalancer;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class LoadBalancer extends BaseLoadBalancer<Node, LoadBalancer> {

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

   public static class Builder extends BaseLoadBalancer.Builder<Node, LoadBalancer> {
      private String region;
      private int id = -1;
      private Status status;
      private Set<VirtualIP> virtualIPs = ImmutableSet.<VirtualIP> of();
      private String sessionPersistenceType;
      private String clusterName;
      private Date created;
      private Date updated;
      private boolean connectionLoggingEnabled;
      private int nodeCount = 0;

      public Builder region(String region) {
         this.region = region;
         return this;
      }

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder algorithm(Algorithm algorithm) {
         algorithm(algorithm.name());
         return this;
      }

      public Builder virtualIPs(Iterable<VirtualIP> virtualIPs) {
         this.virtualIPs = ImmutableSet.<VirtualIP> copyOf(checkNotNull(virtualIPs, "virtualIPs"));
         return this;
      }

      public Builder sessionPersistenceType(String sessionPersistenceType) {
         this.sessionPersistenceType = sessionPersistenceType;
         return this;
      }

      public Builder clusterName(String clusterName) {
         this.clusterName = clusterName;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      public Builder connectionLoggingEnabled(boolean connectionLoggingEnabled) {
         this.connectionLoggingEnabled = connectionLoggingEnabled;
         return this;
      }

      /**
       * @see LoadBalancer#getNodeCount()
       */
      public Builder nodeCount(int nodeCount) {
         this.nodeCount = nodeCount;
         return this;
      }

      public LoadBalancer build() {
         return new LoadBalancer(region, id, name, protocol, port, algorithm, status, virtualIPs, nodes,
                  sessionPersistenceType, clusterName, created, updated, connectionLoggingEnabled, nodeCount);
      }

      @Override
      public Builder nodes(Iterable<Node> nodes) {
         this.nodes = ImmutableSet.<Node> copyOf(checkNotNull(nodes, "nodes"));
         return this;
      }

      @Override
      public Builder node(Node nodes) {
         this.nodes.add(checkNotNull(nodes, "nodes"));
         return this;
      }

      @Override
      public Builder algorithm(String algorithm) {
         return Builder.class.cast(super.algorithm(algorithm));
      }

      @Override
      public Builder from(LoadBalancer in) {
         return Builder.class.cast(super.from(in)).id(in.getId()).status(in.getStatus()).virtualIPs(in.getVirtualIPs())
                  .clusterName(in.getClusterName()).created(in.getCreated()).updated(in.getUpdated())
                  .connectionLoggingEnabled(in.isConnectionLoggingEnabled()).nodeCount(in.getNodeCount());
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

   /**
    * All load balancers also have a status attribute to signify the current configuration status of
    * the device. This status is immutable by the caller and is updated automatically based on state
    * changes within the service. When a load balancer is first created, it will be placed into a
    * BUILD status while the configuration is being generated and applied based on the request. Once
    * the configuration is applied and finalized, it will be in an ACTIVE status. In the event of a
    * configuration change or update, the status of the load balancer will change to PENDING_UPDATE
    * to signify configuration changes are in progress but have not yet been finalized. Load
    * balancers in a SUSPENDED status are configured to reject traffic and will not forward requests
    * to back-end nodes.
    */
   public static enum Status {
      /**
       * Load balancer is being provisioned for the first time and configuration is being applied to
       * bring the service online. The service will not yet be ready to serve incoming requests.
       */
      BUILD,
      /**
       * Load balancer is configured properly and ready to serve traffic to incoming requests via
       * the configured virtual IPs.
       */
      ACTIVE,
      /**
       * Load balancer is online but configuration changes are being applied to update the service
       * based on a previous request.
       */
      PENDING_UPDATE,
      /**
       * Load balancer has been taken offline and disabled; contact Support.
       */
      SUSPENDED,
      /**
       * Load balancer is online but configuration changes are being applied to begin deletion of
       * the service based on a previous request.
       */
      PENDING_DELETE,
      /**
       * Load balancers in DELETED status can be displayed for at least 90 days after deletion.
       */
      DELETED,
      /**
       * The system encountered an error when attempting to configure the load balancer; contact
       * Support.
       */
      ERROR, UNRECOGNIZED;

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   /**
    * All load balancers utilize an algorithm that defines how traffic should be directed between
    * back-end nodes. The default algorithm for newly created load balancers is RANDOM, which can be
    * overridden at creation time or changed after the load balancer has been initially provisioned.
    * The algorithm name is to be constant within a major revision of the load balancing API, though
    * new algorithms may be created with a unique algorithm name within a given major revision of
    * the service API.
    */
   public static enum Algorithm {
      /**
       * The node with the lowest number of connections will receive requests.
       */
      LEAST_CONNECTIONS,
      /**
       * Back-end servers are selected at random.
       */
      RANDOM,
      /**
       * Connections are routed to each of the back-end servers in turn.
       */
      ROUND_ROBIN,
      /**
       * Each request will be assigned to a node based on the number of concurrent connections to
       * the node and its weight.
       */
      WEIGHTED_LEAST_CONNECTIONS,
      /**
       * A round robin algorithm, but with different proportions of traffic being directed to the
       * back-end nodes. Weights must be defined as part of the load balancer's node configuration.
       */
      WEIGHTED_ROUND_ROBIN, UNRECOGNIZED;

      public static Algorithm fromValue(String algorithm) {
         try {
            return valueOf(checkNotNull(algorithm, "algorithm"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Algorithm[] WEIGHTED_ALGORITHMS = { Algorithm.WEIGHTED_LEAST_CONNECTIONS,
            Algorithm.WEIGHTED_ROUND_ROBIN };

   private final String region;
   private final int id;
   private final Status status;
   private final Algorithm algorithm;
   private final Set<VirtualIP> virtualIPs;
   private final String sessionPersistenceType;
   private final String clusterName;
   private final Date created;
   private final Date updated;
   private final boolean connectionLoggingEnabled;
   private int nodeCount = 0;

   public LoadBalancer(String region, int id, String name, String protocol, Integer port, @Nullable String algorithm,
            Status status, Iterable<VirtualIP> virtualIPs, Iterable<Node> nodes, String sessionPersistenceType,
            String clusterName, Date created, Date updated, boolean connectionLoggingEnabled, Integer nodeCount) {
      super(name, protocol, port, algorithm, nodes);
      this.region = checkNotNull(region, "region");
      checkArgument(id != -1, "id must be specified");
      this.id = id;
      this.status = checkNotNull(status, "status");
      this.algorithm = algorithm != null ? Algorithm.fromValue(algorithm) : null;
      this.virtualIPs = ImmutableSet.copyOf(checkNotNull(virtualIPs, "virtualIPs"));
      this.sessionPersistenceType = sessionPersistenceType;
      this.clusterName = clusterName;
      this.created = checkNotNull(created, "created");
      this.updated = checkNotNull(updated, "updated");
      this.connectionLoggingEnabled = connectionLoggingEnabled;
      this.nodeCount = nodeCount;
   }

   public String getRegion() {
      return region;
   }

   public int getId() {
      return id;
   }

   public Status getStatus() {
      return status;
   }

   /**
    * 
    * @return algorithm, which may be null if the load balancer is deleted
    */
   @Nullable
   public Algorithm getTypedAlgorithm() {
      return algorithm;
   }

   public Set<VirtualIP> getVirtualIPs() {
      return virtualIPs;
   }

   public String getClusterName() {
      return clusterName;
   }

   public String getSessionPersistenceType() {
      return sessionPersistenceType;
   }

   public Date getCreated() {
      return created;
   }

   public Date getUpdated() {
      return updated;
   }

   public boolean isConnectionLoggingEnabled() {
      return connectionLoggingEnabled;
   }

   /**
    * Broken out as a separate field because when LoadBalancers are returned from 
    * {@link LoadBalancerApi#list()}, no Nodes are returned (so you can't rely on getNodes().size())
    * but a nodeCount is returned. When {@link LoadBalancerApi#get(int)} is called, nodes are
    * returned by no nodeCount is returned.
    *  
    * @return The number of Nodes in this LoadBalancer 
    */
   public int getNodeCount() {
      return nodes.size() > 0 ? nodes.size() : nodeCount;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("region", region).add("name", name).add("protocol", protocol).add("port", port)
            .add("algorithm", algorithm).add("status", status).add("virtualIPs", virtualIPs).add("nodeCount", getNodeCount())
            .add("nodes", nodes).add("sessionPersistenceType", sessionPersistenceType).add("created", created)
            .add("updated", updated).add("clusterName", clusterName).add("connectionLoggingEnabled", connectionLoggingEnabled);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, region);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      LoadBalancer that = LoadBalancer.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.region, that.region);
   }
}
