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

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecompute.domain.Resource.nullCollectionOnNullOrEmpty;

/**
 * A service account for which access tokens are to be made available to the instance through metadata queries.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances"/>
 */
public class InstanceServiceAccount {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromInstanceServiceAccount(this);
   }

   public static class Builder {

      private String email;
      private ImmutableSet.Builder<String> scopes = ImmutableSet.builder();

      /**
       * @see InstanceServiceAccount#getEmail()
       */
      public Builder email(String email) {
         this.email = checkNotNull(email);
         return this;
      }

      /**
       * @see InstanceServiceAccount#getScopes()
       */
      public Builder addScopes(String scopes) {
         this.scopes.add(scopes);
         return this;
      }

      /**
       * @see InstanceServiceAccount#getScopes()
       */
      public Builder scopes(Set<String> scopes) {
         this.scopes.addAll(scopes);
         return this;
      }

      public InstanceServiceAccount build() {
         return new InstanceServiceAccount(email, scopes.build());
      }

      public Builder fromInstanceServiceAccount(InstanceServiceAccount in) {
         return this.email(in.getEmail()).scopes(in.getScopes());
      }
   }

   private final String email;
   private final Set<String> scopes;

   @ConstructorProperties({
           "email", "scopes"
   })
   public InstanceServiceAccount(String email, Set<String> scopes) {
      this.email = email;
      this.scopes = nullCollectionOnNullOrEmpty(scopes);
   }

   /**
    * @return email address of the service account.
    */
   public String getEmail() {
      return email;
   }

   /**
    * @return the list of scopes to be made available for this service account.
    */
   public Set<String> getScopes() {
      return scopes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(email, scopes);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      InstanceServiceAccount that = InstanceServiceAccount.class.cast(obj);
      return equal(this.email, that.email)
              && equal(this.scopes, that.scopes);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("email", email).add("scopes", scopes);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
