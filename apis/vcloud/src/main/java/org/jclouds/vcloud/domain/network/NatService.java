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
import org.jclouds.vcloud.domain.network.nat.NatPolicy;
import org.jclouds.vcloud.domain.network.nat.NatRule;
import org.jclouds.vcloud.domain.network.nat.NatType;

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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (enabled ? 1231 : 1237);
      result = prime * result + ((natRules == null) ? 0 : natRules.hashCode());
      result = prime * result + ((policy == null) ? 0 : policy.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      NatService other = (NatService) obj;
      if (enabled != other.enabled)
         return false;
      if (natRules == null) {
         if (other.natRules != null)
            return false;
      } else if (!natRules.equals(other.natRules))
         return false;
      if (policy == null) {
         if (other.policy != null)
            return false;
      } else if (!policy.equals(other.policy))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[enabled=" + enabled + ", natRules=" + natRules + ", policy=" + policy + ", type=" + type + "]";
   }

}