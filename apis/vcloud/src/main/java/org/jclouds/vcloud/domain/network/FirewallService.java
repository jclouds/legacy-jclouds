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
package org.jclouds.vcloud.domain.network;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The FirewallService element defines the firewall service capabilities of a network.
 */
public class FirewallService {
   private final boolean enabled;

   List<FirewallRule> firewallRules = Lists.newArrayList();

   public FirewallService(boolean enabled, Iterable<FirewallRule> firewallRules) {
      this.enabled = enabled;
      Iterables.addAll(this.firewallRules, checkNotNull(firewallRules, "firewallRules"));
   }

   /**
    * @return Firewall rules for the network
    * 
    * @since vcloud api 0.8
    */
   public List<FirewallRule> getFirewallRules() {
      return firewallRules;
   }

   /**
    * @return true if the service is enabled
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public boolean isEnabled() {
      return enabled;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (enabled ? 1231 : 1237);
      result = prime * result + ((firewallRules == null) ? 0 : firewallRules.hashCode());
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
      FirewallService other = (FirewallService) obj;
      if (enabled != other.enabled)
         return false;
      if (firewallRules == null) {
         if (other.firewallRules != null)
            return false;
      } else if (!firewallRules.equals(other.firewallRules))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[enabled=" + enabled + ", firewallRules=" + firewallRules + "]";
   }

}