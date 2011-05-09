/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.cloudloadbalancers.domain.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
 * @see <a href=
 *      "http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s02.html" />
 */
public class BaseNode<T extends BaseNode<T>> implements Comparable<BaseNode<T>> {

   public static <T extends BaseNode<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @SuppressWarnings("unchecked")
   public Builder<T> toBuilder() {
      return new Builder<T>().from((T) this);
   }

   public static class Builder<T extends BaseNode<T>> {
      protected String address;
      protected int port = -1;
      protected Condition condition = Condition.ENABLED;
      protected Integer weight;

      public Builder<T> address(String address) {
         this.address = address;
         return this;
      }

      public Builder<T> port(int port) {
         this.port = port;
         return this;
      }

      public Builder<T> condition(Condition condition) {
         this.condition = condition;
         return this;
      }

      public Builder<T> weight(Integer weight) {
         this.weight = weight;
         return this;
      }

      public BaseNode<T> build() {
         return new BaseNode<T>(address, port, condition, weight);
      }

      public Builder<T> from(T in) {
         return address(in.getAddress()).port(in.getPort()).condition(in.getCondition()).weight(in.getWeight());
      }
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
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   protected String address;
   protected int port;
   protected Condition condition;
   protected Integer weight;

   // for serialization only
   protected BaseNode() {

   }

   public BaseNode(String address, int port, Condition condition, Integer weight) {
      this.address = checkNotNull(address, "address");
      checkArgument(port != -1, "port must be specified");
      this.port = port;
      this.condition = checkNotNull(condition, "condition");
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((condition == null) ? 0 : condition.hashCode());
      result = prime * result + port;
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
      BaseNode<?> other = (BaseNode<?>) obj;
      if (address == null) {
         if (other.address != null)
            return false;
      } else if (!address.equals(other.address))
         return false;
      if (condition == null) {
         if (other.condition != null)
            return false;
      } else if (!condition.equals(other.condition))
         return false;
      if (port != other.port)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[address=%s, condition=%s, port=%s, weight=%s]", address, condition, port, weight);
   }

}
