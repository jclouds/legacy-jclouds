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
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * The FirewallRule element defines a single firewall rule.
 * 
 * @author Adrian Cole
 * @since vcloud api 0.8
 */
public class FirewallRule {

   private final boolean enabled;
   @Nullable
   private final String description;
   @Nullable
   private final FirewallPolicy policy;
   @Nullable
   private final FirewallProtocols protocols;
   private final int port;
   private final String destinationIp;

   public FirewallRule(boolean enabled, @Nullable String description, @Nullable FirewallPolicy policy,
            @Nullable FirewallProtocols protocols, int port, String destinationIp) {
      this.enabled = enabled;
      this.description = description;
      this.policy = policy;
      this.protocols = protocols;
      this.port = port;
      this.destinationIp = checkNotNull(destinationIp, "destinationIp");
   }

   /**
    * @return true if the rule is enabled
    */
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * @return description of the rule
    */
   @Nullable
   public String getDescription() {
      return description;
   }

   /**
    * @return specifies how packets are handled by the firewall
    */
   @Nullable
   public FirewallPolicy getPolicy() {
      return policy;
   }

   /**
    * @return specifies the protocols to which this firewall rule applies
    */
   @Nullable
   public FirewallProtocols getProtocols() {
      return protocols;
   }

   /**
    * @return specifies the network port to which this firewall rule applies. A value of ‐1 matches
    *         any port.
    */
   public int getPort() {
      return port;
   }

   /**
    * @return specifies the destination IP address, inside the firewall, to which this firewall rule
    *         applies
    */
   public String getDestinationIp() {
      return destinationIp;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      FirewallRule that = FirewallRule.class.cast(o);
      return equal(this.enabled, that.enabled) && equal(this.description, that.description)
            && equal(this.policy, that.policy) && equal(this.protocols, that.protocols) && equal(this.port, that.port)
            && equal(this.destinationIp, that.destinationIp);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enabled, description, policy, protocols, port, destinationIp);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("enabled", enabled).add("description", description)
            .add("policy", policy).add("protocols", protocols).add("port", port).add("destinationIp", destinationIp)
            .toString();
   }

}
