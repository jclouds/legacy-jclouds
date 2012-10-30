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
import org.jclouds.vcloud.domain.network.nat.NatPolicy;
import org.jclouds.vcloud.domain.network.nat.NatRule;
import org.jclouds.vcloud.domain.network.nat.NatType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The NatService element defines the network address translation capabilities of a network.
 */
public class NatService {
   private final boolean enabled;
   @Nullable
   private final NatType type;
   @Nullable
   private final NatPolicy policy;
   private final List<NatRule> natRules = Lists.newArrayList();

   public NatService(boolean enabled, @Nullable NatType type, @Nullable NatPolicy policy,
            Iterable<NatRule> natRules) {
      this.enabled = enabled;
      this.type = type;
      this.policy = policy;
      Iterables.addAll(this.natRules, checkNotNull(natRules, "natRules"));
   }

   /**
    * @return Nat rules for the network
    * 
    * @since vcloud api 0.8
    */
   public List<NatRule> getNatRules() {
      return natRules;
   }

   /**
    * @return true if the service is enabled
    * 
    * @since vcloud api 0.9
    */
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * @return specifies how Network Address Translation is implemented by the NAT service
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public NatType getType() {
      return type;
   }

   /**
    * @return specifies how packets are handled by the NAT service.
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public NatPolicy getPolicy() {
      return policy;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NatService that = NatService.class.cast(o);
      return equal(this.enabled, that.enabled) && equal(this.type, that.type)
            && equal(this.policy, that.policy) && equal(this.natRules, that.natRules);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enabled, type, policy, natRules);
   }

   @Override
   public String toString() {
      ToStringHelper helper = Objects.toStringHelper("").omitNullValues().add("enabled", enabled)
            .add("type", type).add("policy", policy);
      if (natRules.size() >0)
         helper.add("natRules", natRules);
      return helper.toString();
   }
}
