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
package org.jclouds.vcloud.domain.network.firewall;

/**
 * The Protocols element specifies the protocols to which firewall rules apply.
 * 
 * @since vcloud api 0.9 emulated for 0.8
 * 
 * 
 */
public class FirewallProtocols {
   private final boolean tcp;
   private final boolean udp;

   public FirewallProtocols(boolean tcp, boolean udp) {
      this.tcp = tcp;
      this.udp = udp;
   }

   /**
    * @return true if the firewall rules apply to the TCP protocol
    */
   public boolean isTcp() {
      return tcp;
   }

   /**
    * @return true if the firewall rules apply to the UDP protocol
    */
   public boolean isUdp() {
      return udp;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (tcp ? 1231 : 1237);
      result = prime * result + (udp ? 1231 : 1237);
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
      FirewallProtocols other = (FirewallProtocols) obj;
      if (tcp != other.tcp)
         return false;
      if (udp != other.udp)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Protocols [tcp=" + tcp + ", udp=" + udp + "]";
   }

}