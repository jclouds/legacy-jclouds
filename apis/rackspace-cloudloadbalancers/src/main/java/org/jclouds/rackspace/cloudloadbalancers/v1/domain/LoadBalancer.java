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

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
public class LoadBalancer extends BaseLoadBalancer<Node, LoadBalancer> {

   private final String region;
   private final int id;
   private final Status status;
   private final Set<VirtualIPWithId> virtualIPs;
   private final String clusterName;
   private final Date created;
   private final Date updated;
   private final boolean contentCaching;
   private final int nodeCount;
   private final SSLTermination sslTermination;
   private final SourceAddresses sourceAddresses;
   private final Set<AccessRuleWithId> accessRules;
   private final Metadata metadata;
   private final URI uri;

   public LoadBalancer(String region, int id, String name, String protocol, @Nullable Integer port, Set<Node> nodes,
         @Nullable Integer timeout, @Nullable Boolean halfClosed, @Nullable Algorithm algorithm, Status status,
         Set<VirtualIPWithId> virtualIPs, @Nullable Map<String, SessionPersistence> sessionPersistenceType,
         String clusterName, Date created, Date updated, @Nullable Map<String, Boolean> connectionLogging,
         @Nullable ConnectionThrottle connectionThrottle, boolean contentCaching, int nodeCount,
         @Nullable HealthMonitor healthMonitor, @Nullable SSLTermination sslTermination,
         SourceAddresses sourceAddresses, Set<AccessRuleWithId> accessRules, Metadata metadata, URI uri) {
      super(name, protocol, port, nodes, algorithm, timeout, halfClosed, sessionPersistenceType, connectionLogging,
            connectionThrottle, healthMonitor);
      this.region = checkNotNull(region, "region");
      checkArgument(id != -1, "id must be specified");
      this.id = id;
      this.status = checkNotNull(status, "status");
      this.virtualIPs = ImmutableSet.copyOf(checkNotNull(virtualIPs, "virtualIPs"));
      this.clusterName = clusterName;
      this.created = checkNotNull(created, "created");
      this.updated = checkNotNull(updated, "updated");
      this.contentCaching = contentCaching;
      this.nodeCount = nodeCount;
      this.sslTermination = sslTermination;
      this.sourceAddresses = sourceAddresses;
      this.accessRules = accessRules == null ? ImmutableSet.<AccessRuleWithId> of() : ImmutableSet.copyOf(accessRules);
      this.metadata = metadata == null ? new Metadata() : metadata;
      this.uri = uri;
   }

   public String getRegion() {
      return region;
   }

   public int getId() {
      return id;
   }

   /**
    * @see Status
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @see VirtualIP
    */
   public Set<VirtualIPWithId> getVirtualIPs() {
      return virtualIPs;
   }

   /**
    * Name of the cluster.
    */
   public String getClusterName() {
      return clusterName;
   }

   /**
    * When the load balancer was created.
    */
   public Date getCreated() {
      return created;
   }

   /**
    * When the load balancer was updated.
    */
   public Date getUpdated() {
      return updated;
   }

   /**
    * View the current content caching configuration.
    */
   public boolean isContentCaching() {
      return contentCaching;
   }

   /**
    * Broken out as a separate field because when LoadBalancers are returned from 
    * {@link LoadBalancerApi#list()}, no Nodes are returned (so you can't rely on getNodes().size())
    * but a nodeCount is returned. When {@link LoadBalancerApi#get(int)} is called, nodes are
    * returned but no nodeCount is returned.
    *  
    * @return The number of Nodes in this LoadBalancer 
    */
   public int getNodeCount() {
      return nodes.size() > 0 ? nodes.size() : nodeCount;
   }

   /**
    * @see SSLTermination
    */
   @Nullable
   public SSLTermination getSSLTermination() {
      return sslTermination;
   }

   /**
    * @see SourceAddresses
    */
   public SourceAddresses getSourceAddresses() {
      return sourceAddresses;
   }

   /**
    * @see AccessRule
    */
   public Set<AccessRuleWithId> getAccessRules() {
      return accessRules;
   }

   /**
    * @see Metadata
    */
   public Metadata getMetadata() {
      return metadata;
   }
   
   public URI getUri() {
      return uri;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("region", region).add("status", status)
            .add("name", name).add("protocol", protocol).add("port", port).add("nodeCount", getNodeCount())
            .add("nodes", nodes).add("timeout", timeout).add("algorithm", algorithm).add("halfClosed", halfClosed)
            .add("clusterName", clusterName).add("created", created).add("updated", updated)
            .add("contentCaching", contentCaching).add("sessionPersistenceType", getSessionPersistenceType())
            .add("sslTermination", sslTermination).add("connectionLogging", isConnectionLogging())
            .add("connectionThrottle", connectionThrottle).add("healthMonitor", healthMonitor)
            .add("accessRules", accessRules).add("metadata", getMetadata()).add("uri", uri).add("sourceAddresses", sourceAddresses)
            .add("virtualIPs", virtualIPs);
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
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;

      LoadBalancer that = LoadBalancer.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.region, that.region);
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
         }
         catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static class Builder extends BaseLoadBalancer.Builder<Node, LoadBalancer> {
      private String region;
      private int id = -1;
      private Status status;
      private Set<VirtualIPWithId> virtualIPs = ImmutableSet.<VirtualIPWithId> of();
      private String clusterName;
      private Date created;
      private Date updated;
      private boolean contentCaching;
      private int nodeCount = 0;
      private SSLTermination sslTermination;
      private SourceAddresses sourceAddresses;
      private Set<AccessRuleWithId> accessRules;
      private Metadata metadata;
      private URI uri;

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

      public Builder virtualIPs(Iterable<VirtualIPWithId> virtualIPs) {
         this.virtualIPs = ImmutableSet.<VirtualIPWithId> copyOf(checkNotNull(virtualIPs, "virtualIPs"));
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

      public Builder contentCaching(boolean contentCaching) {
         this.contentCaching = contentCaching;
         return this;
      }

      /**
       * @see LoadBalancer#getNodeCount()
       */
      public Builder nodeCount(int nodeCount) {
         this.nodeCount = nodeCount;
         return this;
      }

      public Builder sslTermination(SSLTermination sslTermination) {
         this.sslTermination = checkNotNull(sslTermination, "sslTermination");
         return this;
      }

      public Builder sourceAddresses(SourceAddresses sourceAddresses) {
         this.sourceAddresses = checkNotNull(sourceAddresses, "sourceAddresses");
         return this;
      }

      public Builder accessRules(Iterable<AccessRuleWithId> accessRules) {
         this.accessRules = ImmutableSet.copyOf(checkNotNull(accessRules, "accessRules"));
         return this;
      }

      public Builder metadata(Metadata metadata) {
         this.metadata = checkNotNull(metadata, "metadata");
         return this;
      }
      
      public Builder uri(URI uri) {
         this.uri = uri;
         return this;
      }

      public LoadBalancer build() {
         return new LoadBalancer(region, id, name, protocol, port, nodes, timeout, halfClosed, algorithm, status,
               virtualIPs, sessionPersistence, clusterName, created, updated, connectionLogging, connectionThrottle,
               contentCaching, nodeCount, healthMonitor, sslTermination, sourceAddresses, accessRules, metadata, uri);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder nodes(Iterable<Node> nodes) {
         this.nodes = ImmutableSet.<Node> copyOf(checkNotNull(nodes, "nodes"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder node(Node node) {
         this.nodes.add(checkNotNull(node, "nodes"));
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
      public Builder timeout(@Nullable Integer timeout) {
         return Builder.class.cast(super.timeout(timeout));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder halfClosed(@Nullable Boolean halfClosed) {
         return Builder.class.cast(super.halfClosed(halfClosed));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder sessionPersistenceType(@Nullable SessionPersistence sessionPersistenceType) {
         return Builder.class.cast(super.sessionPersistenceType(sessionPersistenceType));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder connectionLogging(@Nullable Boolean connectionLogging) {
         return Builder.class.cast(super.connectionLogging(connectionLogging));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder connectionThrottle(@Nullable ConnectionThrottle connectionThrottle) {
         return Builder.class.cast(super.connectionThrottle(connectionThrottle));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder healthMonitor(@Nullable HealthMonitor healthMonitor) {
         return Builder.class.cast(super.healthMonitor(healthMonitor));
      }

      @Override
      public Builder from(LoadBalancer in) {
         return Builder.class.cast(super.from(in)).region(in.getRegion()).id(in.getId()).status(in.getStatus())
               .virtualIPs(in.getVirtualIPs()).clusterName(in.getClusterName()).created(in.getCreated())
               .updated(in.getUpdated()).contentCaching(in.isContentCaching()).nodeCount(in.getNodeCount())
               .sslTermination(in.getSSLTermination()).sourceAddresses(in.getSourceAddresses())
               .accessRules(in.getAccessRules()).metadata(in.getMetadata()).uri(in.getUri());
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
