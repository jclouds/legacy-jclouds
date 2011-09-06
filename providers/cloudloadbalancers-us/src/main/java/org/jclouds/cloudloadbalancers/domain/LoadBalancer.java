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

import java.util.Date;
import java.util.Set;

import org.jclouds.cloudloadbalancers.domain.internal.BaseLoadBalancer;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01s02.html"
 *      />
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

      public LoadBalancer build() {
         return new LoadBalancer(region, id, name, protocol, port, algorithm, status, virtualIPs, nodes,
                  sessionPersistenceType, clusterName, created, updated, connectionLoggingEnabled);
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
                  .connectionLoggingEnabled(in.isConnectionLoggingEnabled());
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
    * to back-end nodess.
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

   private final String region;
   private final int id;
   private final Status status;
   private final Set<VirtualIP> virtualIPs;
   private final String sessionPersistenceType;
   private final String clusterName;
   private final Date created;
   private final Date updated;
   private final boolean connectionLoggingEnabled;

   public LoadBalancer(String region, int id, String name, String protocol, Integer port, String algorithm, Status status,
            Iterable<VirtualIP> virtualIPs, Iterable<Node> nodes, String sessionPersistenceType, String clusterName,
            Date created, Date updated, boolean connectionLoggingEnabled) {
      super(name, protocol, port, algorithm, nodes);
      this.region = checkNotNull(region, "region");
      checkArgument(id != -1, "id must be specified");
      this.id = id;
      this.status = checkNotNull(status, "status");
      this.virtualIPs = ImmutableSet.copyOf(checkNotNull(virtualIPs, "virtualIPs"));
      this.sessionPersistenceType = sessionPersistenceType;
      this.clusterName = clusterName;
      this.created = checkNotNull(created, "created");
      this.updated = checkNotNull(updated, "updated");
      this.connectionLoggingEnabled = connectionLoggingEnabled;
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

   @Override
   public String toString() {
      return String
               .format(
                        "[region=%s, id=%s, name=%s, protocol=%s, port=%s, algorithm=%s, status=%s, virtualIPs=%s, nodes=%s, sessionPersistenceType=%s, created=%s, updated=%s, clusterName=%s, connectionLoggingEnabled=%s]",
                        region, id, name, protocol, port, algorithm, status, virtualIPs, nodes, sessionPersistenceType,
                        created, updated, clusterName, connectionLoggingEnabled);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + id;
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoadBalancer other = (LoadBalancer) obj;
      if (id != other.id)
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      return true;
   }

}
