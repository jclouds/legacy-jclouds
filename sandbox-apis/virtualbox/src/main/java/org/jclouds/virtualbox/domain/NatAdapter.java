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

import org.virtualbox_4_1.NATProtocol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a NAT network adapter in VirtualBox.
 *
 * redirectRules are the redirect rules that are applied to the network adapter.
 */
public class NatAdapter {

   private final Set<RedirectRule> redirectRules = new HashSet<RedirectRule>();

   public NatAdapter(Set<RedirectRule> redirectRules) {
      this.redirectRules.addAll(redirectRules);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private Set<RedirectRule> redirectRules = new HashSet<RedirectRule>();
      private long adapterSlot;

      public Builder tcpRedirectRule(String host, int hostPort, String guest, int guestPort) {
         redirectRules.add(new RedirectRule(NATProtocol.TCP, host, hostPort, guest, guestPort));
         return this;
      }

      public Builder udpRedirectRule(String host, int hostPort, String guest, int guestPort) {
         redirectRules.add(new RedirectRule(NATProtocol.UDP, host, hostPort, guest, guestPort));
         return this;
      }

      public NatAdapter build() {
         return new NatAdapter(redirectRules);
      }

   }

   public Set<RedirectRule> getRedirectRules() {
      return Collections.unmodifiableSet(redirectRules);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NatAdapter that = (NatAdapter) o;

      if (redirectRules != null ? !redirectRules.equals(that.redirectRules) : that.redirectRules != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return redirectRules != null ? redirectRules.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "NatAdapter{" +
              "redirectRules=" + redirectRules +
              '}';
   }
}
