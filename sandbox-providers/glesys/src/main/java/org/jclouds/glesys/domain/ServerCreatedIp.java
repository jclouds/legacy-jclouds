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

package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Represents detailed information about an available ip address of a new server.
 *
 * @author Adam Lowe
 * @see ServerCreated
 */
public class ServerCreatedIp {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String ip;
      protected int version;
      protected double cost;

      public Builder version(int version) {
         this.version = version;
         return this;
      }

      public Builder ip(String ip) {
         this.ip = ip;
         return this;
      }

      public Builder cost(double cost) {
         this.cost = cost;
         return this;
      }

      public ServerCreatedIp build() {
         return new ServerCreatedIp(ip, version, cost);
      }

      public Builder fromIpCreated(ServerCreatedIp from) {
         return ip(from.getIp()).version(from.getVersion()).cost(from.getCost());
      }
   }

   protected final String ip;
   protected final int version;
   protected final double cost;

   public ServerCreatedIp(String ip, int version, double cost) {
      this.ip = ip;
      this.version = version;
      this.cost = cost;
   }

   /**
    * @return the IP version, ex. 4
    */
   public int getVersion() {
      return version;
   }

   /**
    * @return the ip address of the new server
    */
   public String getIp() {
      return ip;
   }

   /**
    * @return the cost of the ip address allocated to the new server
    */
   public double getCost() {
      return cost;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerCreatedIp) {
         final ServerCreatedIp other = (ServerCreatedIp) object;
         return Objects.equal(ip, other.ip)
               && Objects.equal(version, other.version)
               && Objects.equal(cost, other.cost);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ip, version, cost);
   }

   @Override
   public String toString() {
      return String.format("[ip=%s, version=%d, cost=%f]",
            ip, version, cost);
   }
}
