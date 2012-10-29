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

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

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
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      FirewallProtocols that = FirewallProtocols.class.cast(o);
      return equal(this.tcp, that.tcp) && equal(this.udp, that.udp);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tcp, udp);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("tcp", tcp).add("udp", udp).toString();
   }

}
