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
package org.jclouds.fujitsu.fgcp.domain;

import com.google.common.base.Objects;

/**
 * Describes the target server of a load balancer.
 * 
 * @author Dies Koper
 */
public class Target {
   private String serverId;

   private String serverName;

   private String ipAddress;

   private String port1;

   private String port2;

   private String status;

   private String now;

   private String peak;

   /**
    * @return the serverId
    */
   public String getServerId() {
      return serverId;
   }

   /**
    * @return the serverName
    */
   public String getServerName() {
      return serverName;
   }

   /**
    * @return the ipAddress
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * @return the port1
    */
   public String getPort1() {
      return port1;
   }

   /**
    * @return the port2
    */
   public String getPort2() {
      return port2;
   }

   /**
    * @return the status
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return the now
    */
   public String getNow() {
      return now;
   }

   /**
    * @return the peak
    */
   public String getPeak() {
      return peak;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(serverId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Target that = Target.class.cast(obj);
      return Objects.equal(this.serverId, that.serverId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("serverId", serverId).add("serverName", serverName)
            .add("ipAddress", ipAddress).add("port1", port1)
            .add("port2", port2).add("status", status).add("now", now)
            .add("peak", peak).toString();
   }
}
