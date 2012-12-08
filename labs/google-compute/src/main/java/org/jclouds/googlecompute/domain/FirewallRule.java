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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecompute.domain.Resource.nullCollectionOnNullOrEmpty;

/**
 * A Firewall rule. Rule specifies a protocol and port-range tuple that describes a
 * permitted connection.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/firewalls"/>
 */
public class FirewallRule {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromFirewallRule(this);
   }

   public static class Builder {

      private String IPProtocol;
      private ImmutableSet.Builder<String> ports = ImmutableSet.builder();

      /**
       * @see FirewallRule#getIPProtocol()
       */
      public Builder IPProtocol(String IPProtocol) {
         this.IPProtocol = IPProtocol;
         return this;
      }

      /**
       * @see FirewallRule#getPorts()
       */
      public Builder addPort(String port) {
         this.ports.add(checkNotNull(port));
         return this;
      }

      /**
       * @see FirewallRule#getPorts()
       */
      public Builder ports(Set<String> ports) {
         this.ports.addAll(checkNotNull(ports));
         return this;
      }

      public FirewallRule build() {
         return new FirewallRule(IPProtocol, ports.build());
      }

      public Builder fromFirewallRule(FirewallRule firewallRule) {
         return new Builder().IPProtocol(firewallRule.getIPProtocol()).ports(firewallRule.getPorts());
      }
   }


   private final String IPProtocol;
   private final Set<String> ports;

   @ConstructorProperties({
           "IPProtocol", "ports"
   })
   private FirewallRule(String IPProtocol, Set<String> ports) {
      this.IPProtocol = checkNotNull(IPProtocol);
      this.ports = nullCollectionOnNullOrEmpty(ports);
   }

   /**
    * @return Required; this is the IP protocol that is allowed for this rule. This can either be a well known
    *         protocol string (tcp, udp or icmp) or the IP protocol number.
    */
   public String getIPProtocol() {
      return IPProtocol;
   }

   /**
    * @return An optional list of ports which are allowed. It is an error to specify this for any protocol that isn't
    *         UDP or TCP. Each entry must be either an integer or a range. If not specified,
    *         connections through any port are allowed.
    *         Example inputs include: ["22"], ["80,"443"], and ["12345-12349"].
    */
   @Nullable
   public Set<String> getPorts() {
      return ports;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(IPProtocol, ports);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      FirewallRule that = FirewallRule.class.cast(obj);
      return equal(this.IPProtocol, that.IPProtocol)
              && equal(this.ports, that.ports);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("IPProtocol", IPProtocol).add("ports", ports);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }


}
