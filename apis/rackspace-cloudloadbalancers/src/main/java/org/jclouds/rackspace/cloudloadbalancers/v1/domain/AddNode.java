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

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode;

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
public class AddNode extends BaseNode<AddNode> {

   // for serialization only
   AddNode() {
   }

   public AddNode(String address, int port, Condition condition, Type type, Integer weight) {
      super(address, port, condition, type, weight);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("address", address).add("port", port).add("condition", condition).add("type", type).add("weight", weight);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(address, port);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      AddNode that = AddNode.class.cast(obj);
      return Objects.equal(this.address, that.address)
            && Objects.equal(this.port, that.port);
   }

   public static class Builder extends BaseNode.Builder<AddNode> {
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder address(String address) {
         return Builder.class.cast(super.address(address));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder condition(Condition condition) {
         return Builder.class.cast(super.condition(condition));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(Type type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder port(int port) {
         return Builder.class.cast(super.port(port));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder weight(Integer weight) {
         return Builder.class.cast(super.weight(weight));
      }

      @Override
      public AddNode build() {
         return new AddNode(address, port, condition, type, weight);
      }
      
      @Override
      public Builder from(AddNode in) {
         return Builder.class.cast(super.from(in));
      }
   }

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
}
