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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
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
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      FirewallService that = FirewallService.class.cast(o);
      return equal(this.enabled, that.enabled) && equal(this.firewallRules, that.firewallRules);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enabled, firewallRules);
   }

   @Override
   public String toString() {
      ToStringHelper helper = Objects.toStringHelper("").omitNullValues().add("enabled", enabled);
      if (firewallRules.size() > 0)
         helper.add("firewallRules", firewallRules);
      return helper.toString();
   }
}
