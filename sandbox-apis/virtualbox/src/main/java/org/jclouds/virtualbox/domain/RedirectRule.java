/*
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

import org.virtualbox_4_1.NATProtocol;

/**
 * @author Mattias Holmqvist
 */
public class RedirectRule {

   private final NATProtocol protocol;
   private final String host;
   private final int hostPort;
   private final String guest;
   private final int guestPort;

   public RedirectRule(NATProtocol protocol, String host, int hostPort, String guest, int guestPort) {
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
}