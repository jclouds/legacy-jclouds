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

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Objects.ToStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * The claimset for the token.
 *
 * @author David Alves
 * @see <a
 *      href="https://developers.google.com/accounts/docs/OAuth2ServiceAccount"
 *      >doc</a>
 */
public class ClaimSet extends ForwardingMap<String, String> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromClaimSet(this);
   }

   public static class Builder {

      private Set<String> requiredClaims;
      private ImmutableMap.Builder<String, String> claims = new ImmutableMap.Builder<String, String>();
      private long emissionTime;
      private long expirationTime;

      public Builder() {
         this(ImmutableSet.<String>of());
      }

      /**
       * Constructor that allows to predefine a mandatory set of claims as a comma-separated string, e.g, "iss,iat".
       */
      public Builder(String commaSeparatedRequiredClaims) {
         this(ImmutableSet.copyOf(Splitter.on(",").split(checkNotNull(commaSeparatedRequiredClaims))));
      }

      /**
       * Constructor that allows to predefine a mandatory set of claims as a set of strings.
       */
      public Builder(Set<String> requiredClaims) {
         this.requiredClaims = ImmutableSet.copyOf(checkNotNull(requiredClaims));
      }

      /**
       * Adds a Claim, i.e. key/value pair, e.g., "scope":"all_permissions".
       */
      public Builder addClaim(String name, String value) {
         claims.put(checkNotNull(name), checkNotNull(value, "value of %s", name));
         return this;
      }

      /**
       * @see ClaimSet#getEmissionTime()
       */
      public Builder emissionTime(long emmissionTime) {
         this.emissionTime = emmissionTime;
         return this;
      }

      /**
       * @see ClaimSet#getExpirationTime()
       */
      public Builder expirationTime(long expirationTime) {
         this.expirationTime = expirationTime;
         return this;
      }

      /**
       * Adds a map containing multiple claims
       */
      public Builder addAllClaims(Map<String, String> claims) {
         this.claims.putAll(checkNotNull(claims));
         return this;
      }

      public ClaimSet build() {
         Map<String, String> claimsMap = claims.build();
         checkState(Sets.intersection(claimsMap.keySet(), requiredClaims).size() == requiredClaims.size(),
                 "not all required claims were present");
         if (expirationTime == 0) {
            expirationTime = emissionTime + 3600;
         }
         return new ClaimSet(claimsMap, emissionTime, expirationTime);
      }

      public Builder fromClaimSet(ClaimSet claimSet) {
         return new Builder().addAllClaims(claimSet.claims).expirationTime(expirationTime).emissionTime(emissionTime);
      }
   }

   private final Map<String, String> claims;
   private final long emissionTime;
   private final long expirationTime;

   private ClaimSet(Map<String, String> claims, long emissionTime, long expirationTime) {
      this.claims = claims;
      this.emissionTime = emissionTime;
      this.expirationTime = expirationTime;
   }

   /**
    * The emission time, in seconds since the epoch.
    */
   public long getEmissionTime() {
      return emissionTime;
   }

   /**
    * The expiration time, in seconds since the emission time.
    */
   public long getExpirationTime() {
      return expirationTime;
   }

   /**
    * @returns the claims.
    */
   @Override
   protected Map<String, String> delegate() {
      return claims;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(claims);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ClaimSet other = (ClaimSet) obj;
      return equal(claims, other.claims);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("claims", claims)
              .add("emissionTime", emissionTime).add("expirationTIme", expirationTime);
   }
}
