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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseLoadBalancer.Algorithm;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The nodes defined by the load balancer are responsible for servicing the requests received
 * through the load balancer's virtual IP. By default, the load balancer employs a basic health
 * check that ensures the node is listening on its defined port. The node is checked at the time of
 * addition and at regular intervals as defined by the load balancer health check configuration. If
 * a back-end node is not listening on its port or does not meet the conditions of the defined
 * active health check for the load balancer, then the load balancer will not forward connections
 * and its status will be listed as OFFLINE. Only nodes that are in an ONLINE status will receive
 * and be able to service traffic from the load balancer.
 * <p/>
 * All nodes have an associated status that indicates whether the node is ONLINE, OFFLINE, or
 * DRAINING. Only nodes that are in ONLINE status will receive and be able to service traffic from
 * the load balancer. The OFFLINE status represents a node that cannot accept or service traffic. A
 * node in DRAINING status represents a node that stops the traffic manager from sending any
 * additional new connections to the node, but honors established sessions. If the traffic manager
 * receives a request and session persistence requires that the node is used, the traffic manager
 * will use it. The status is determined by the passive or active health monitors.
 * <p/>
 * If the WEIGHTED_ROUND_ROBIN load balancer algorithm mode is selected, then the caller should
 * assign the relevant weights to the node as part of the weight attribute of the node element. When
 * the algorithm of the load balancer is changed to WEIGHTED_ROUND_ROBIN and the nodes do not
 * already have an assigned weight, the service will automatically set the weight to "1" for all
 * nodes.
 * 
 * @author Adrian Cole
 */
public class BaseNode<T extends BaseNode<T>> implements Comparable<BaseNode<T>> {

   protected String address;
   protected int port;
   protected Condition condition;
   protected Type type;
   protected Integer weight;

   // for serialization only
   protected BaseNode() {
   }

   public BaseNode(String address, int port, Condition condition, Type type, Integer weight) {
      this.address = checkNotNull(address, "address");
      checkArgument(port != -1, "port must be specified");
      this.port = port;
      this.condition = checkNotNull(condition, "condition");
      this.type = type;
      this.weight = weight;
   }

   public String getAddress() {
      return address;
   }

   public int getPort() {
      return port;
   }

   public Condition getCondition() {
      return condition;
   }

   public Type getType() {
      return type;
   }

   /**
    * the maximum weight of a node is 100.
    */
   public Integer getWeight() {
      return weight;
   }

   @Override
   public int compareTo(BaseNode<T> arg0) {
      return address.compareTo(arg0.address);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("address", address).add("port", port)
            .add("condition", condition).add("type", type).add("weight", weight);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(address, port, condition);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;

      BaseNode<?> that = BaseNode.class.cast(obj);
      return Objects.equal(this.address, that.address) && Objects.equal(this.port, that.port)
            && Objects.equal(this.condition, that.condition);
   }

   /**
    * Virtual IP Conditions
    */
   public static enum Condition {
      /**
       * Node is permitted to accept new connections.
       */
      ENABLED,
      /**
       * Node is not permitted to accept any new connections regardless of session persistence
       * configuration. Existing connections are forcibly terminated.
       */
      DISABLED,
      /**
       * Node is allowed to service existing established connections and connections that are being
       * directed to it as a result of the session persistence configuration.
       */
      DRAINING,

      UNRECOGNIZED;

      public static Condition fromValue(String condition) {
         try {
            return valueOf(checkNotNull(condition, "condition"));
         }
         catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   /**
    * Type of node.
    */
   public static enum Type {
      /**
       * Nodes defined as PRIMARY are in the normal rotation to receive traffic from the load balancer.
       */
      PRIMARY,

      /**
       * Nodes defined as SECONDARY are only in the rotation to receive traffic from the load balancer when all the
       * primary nodes fail. This provides a failover feature that automatically routes traffic to the secondary node
       * in the event that the primary node is disabled or in a failing state. Note that active health monitoring must
       * be enabled on the load balancer to enable the failover feature to the secondary node.
       */
      SECONDARY,

      UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         }
         catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static class Builder<T extends BaseNode<T>> {
      protected String address;
      protected int port = -1;
      protected Condition condition = Condition.ENABLED;
      protected Type type;
      protected Integer weight;

      /**
       * Required. IP address or domain name for the node.
       */
      public Builder<T> address(String address) {
         this.address = address;
         return this;
      }

      /**
       * Required. Port number for the service you are load balancing.
       */
      public Builder<T> port(int port) {
         this.port = port;
         return this;
      }

      /**
       * Required. Condition for the node, which determines its role within the load balancer.
       * 
       * @see Condition
       */
      public Builder<T> condition(Condition condition) {
         this.condition = condition;
         return this;
      }

      /**
       * Type of node to add.
       * 
       * @see Type
       */
      public Builder<T> type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * Weight of node to add. If the {@link Algorithm#WEIGHTED_ROUND_ROBIN} load balancer algorithm mode is 
       * selected, then the user should assign the relevant weight to the node using the weight attribute for 
       * the node. Must be an integer from 1 to 100.
       */
      public Builder<T> weight(Integer weight) {
         this.weight = weight;
         return this;
      }

      public BaseNode<T> build() {
         return new BaseNode<T>(address, port, condition, type, weight);
      }

      public Builder<T> from(T in) {
         return address(in.getAddress()).port(in.getPort()).condition(in.getCondition()).type(in.getType())
               .weight(in.getWeight());
      }
   }

   public static <T extends BaseNode<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @SuppressWarnings("unchecked")
   public Builder<T> toBuilder() {
      return new Builder<T>().from((T) this);
   }
}
