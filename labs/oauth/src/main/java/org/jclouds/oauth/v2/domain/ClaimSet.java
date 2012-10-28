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
package org.jclouds.oauth.v2.domain;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * The claimset for the token.
 *
 * @author David Alves
 */
public class ClaimSet {

   private final Map<String, String> claims;
   private final long emissionTime;
   private final long expirationTime;

   private ClaimSet(Map<String, String> claims, long emissionTime, long expirationTime) {
      this.claims = claims;
      this.emissionTime = emissionTime;
      this.expirationTime = expirationTime;
   }

   public Map<String, String> getClaims() {
      return claims;
   }

   public long getEmissionTime() {
      return emissionTime;
   }

   public long getExpirationTime() {
      return expirationTime;
   }

   public static class Builder {

      private Set<String> requiredClaims;
      private Map<String, String> claims = Maps.newLinkedHashMap();
      private long emissionTime;
      private long expirationTime;

      public Builder() {
         this(ImmutableSet.<String>of());
      }

      public Builder(String commaSeparatedRequiredClaims) {
         this(ImmutableSet.copyOf(Splitter.on(",").split(commaSeparatedRequiredClaims)));
      }

      public Builder(Set<String> requiredClaims) {
         this.requiredClaims = requiredClaims;
      }

      public Builder addClaim(String name, String value) {
         claims.put(checkNotNull(name), checkNotNull(value));
         return this;
      }

      public Builder emissionTime(long emmissionTime) {
         this.emissionTime = emmissionTime;
         return this;
      }

      public Builder expirationTime(long expirationTime) {
         this.expirationTime = expirationTime;
         return this;
      }

      public Builder addAllClaims(Map<String, String> claims) {
         this.claims.putAll(claims);
         return this;
      }

      public ClaimSet build() {
         checkState(Sets.intersection(claims.keySet(), requiredClaims).size() == requiredClaims.size(),
                 "not all required claims were present");
         if (emissionTime == 0) {
            emissionTime = System.currentTimeMillis() / 1000;
         }
         if (expirationTime == 0) {
            expirationTime = emissionTime + 3600;
         }
         return new ClaimSet(ImmutableMap.copyOf(claims), emissionTime, expirationTime);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ClaimSet claimSet = (ClaimSet) o;

      if (emissionTime != claimSet.emissionTime) return false;
      if (expirationTime != claimSet.expirationTime) return false;
      if (!claims.equals(claimSet.claims)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = claims.hashCode();
      result = 31 * result + (int) (emissionTime ^ (emissionTime >>> 32));
      result = 31 * result + (int) (expirationTime ^ (expirationTime >>> 32));
      return result;
   }
}
