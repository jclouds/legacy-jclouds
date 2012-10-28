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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A complete token request.
 *
 * @author David Alves
 */
public class TokenRequest {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromTokenRequest(this);
   }

   public static class Builder {
      private Header header;
      private ClaimSet claimSet;

      /**
       * @see TokenRequest#getClaimSet()
       */
      public Builder header(Header header) {
         this.header = header;
         return this;
      }

      /**
       * @see TokenRequest#getHeader()
       */
      public Builder claimSet(ClaimSet claimSet) {
         this.claimSet = claimSet;
         return this;
      }

      public TokenRequest build() {
         return new TokenRequest(header, claimSet);
      }

      public Builder fromTokenRequest(TokenRequest tokeRequest) {
         return new Builder().header(tokeRequest.header).claimSet(tokeRequest.claimSet);
      }
   }

   private final Header header;
   private final ClaimSet claimSet;

   public TokenRequest(Header header, ClaimSet claimSet) {
      this.header = checkNotNull(header);
      this.claimSet = checkNotNull(claimSet);
   }

   /**
    * The header of this token request.
    *
    * @see Header
    */
   public Header getHeader() {
      return header;
   }

   /**
    * The claim set of this token request.
    *
    * @see ClaimSet
    */
   public ClaimSet getClaimSet() {
      return claimSet;
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
      TokenRequest other = (TokenRequest) obj;
      return equal(this.header, other.header) && equal(this.claimSet,
              other.claimSet);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(header, claimSet);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("header", header)
              .add("claimSet", claimSet);
   }


}
