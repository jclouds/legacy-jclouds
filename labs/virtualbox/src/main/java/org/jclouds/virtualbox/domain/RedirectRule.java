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
package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.virtualbox_4_2.NATProtocol;

import com.google.common.base.Objects;

/**
 * @author Mattias Holmqvist
 */
public class RedirectRule {

   private final NATProtocol protocol;
   private final String host;
   private final int hostPort;
   private final String guest;
   private final int guestPort;
   
   /**
    * @param protocol
    * @param host incoming address
    * @param hostPort
    * @param guest guest address or empty string for all addresses
    * @param guestPort
    * @return
    */
   public RedirectRule(NATProtocol protocol, String host, int hostPort, String guest, int guestPort) {
      checkNotNull(protocol);
      checkNotNull(host);
      checkNotNull(guest);
      this.protocol = protocol;
      this.host = host;
      this.hostPort = hostPort;
      this.guest = guest;
      this.guestPort = guestPort;
   }

   public NATProtocol getProtocol() {
      return protocol;
   }

   public String getHost() {
      return host;
   }

   public int getHostPort() {
      return hostPort;
   }

   public String getGuest() {
      return guest;
   }

   public int getGuestPort() {
      return guestPort;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof RedirectRule) {
         RedirectRule other = (RedirectRule) o;
         return Objects.equal(protocol, other.protocol) &&
                 Objects.equal(host, other.host) &&
                 Objects.equal(hostPort, other.hostPort) &&
                 Objects.equal(guest, other.guest) &&
                 Objects.equal(guestPort, other.guestPort);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(protocol, host, hostPort, guest, guestPort);
   }

   @Override
   public String toString() {
      return "RedirectRule{" +
              "protocol=" + protocol +
              ", host='" + host + '\'' +
              ", hostPort=" + hostPort +
              ", guest='" + guest + '\'' +
              ", guestPort=" + guestPort +
              '}';
   }
}
