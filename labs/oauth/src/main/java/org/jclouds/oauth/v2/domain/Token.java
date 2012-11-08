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

import java.beans.ConstructorProperties;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The oauth token, obtained upon a successful token request and ready to embed in requests.
 *
 * @author David Alves
 */
public class Token {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromToken(this);
   }

   public static class Builder {

      private String accessToken;
      private String tokenType;
      private long expiresIn;

      /**
       * @see Token#getAccessToken()
       */
      public Builder accessToken(String accessToken) {
         this.accessToken = checkNotNull(accessToken);
         return this;
      }

      /**
       * @see Token#getTokenType()
       */
      public Builder tokenType(String tokenType) {
         this.tokenType = checkNotNull(tokenType);
         return this;
      }

      /**
       * @see Token#getExpiresIn()
       */
      public Builder expiresIn(long expiresIn) {
         this.expiresIn = expiresIn;
         return this;
      }

      public Token build() {
         return new Token(accessToken, tokenType, expiresIn);
      }

      public Builder fromToken(Token token) {
         return new Builder().accessToken(token.accessToken).tokenType(token.tokenType).expiresIn(token.expiresIn);
      }
   }

   private final String accessToken;
   private final String tokenType;
   private final long expiresIn;

   @ConstructorProperties({"access_token", "token_type", "expires_in"})
   protected Token(String accessToken, String tokenType, long expiresIn) {
      this.accessToken = accessToken;
      this.tokenType = tokenType;
      this.expiresIn = expiresIn;
   }

   /**
    * The access token obtained from the OAuth server.
    */
   public String getAccessToken() {
      return accessToken;
   }

   /**
    * The type of the token, e.g., "Bearer"
    */
   public String getTokenType() {
      return tokenType;
   }

   /**
    * In how many seconds this token expires.
    */
   public long getExpiresIn() {
      return expiresIn;
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
      Token other = (Token) obj;
      return equal(this.accessToken, other.accessToken) && equal(this.tokenType,
              other.tokenType) && equal(this.expiresIn,
              other.expiresIn);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(accessToken, tokenType, expiresIn);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("accessToken", accessToken)
              .add("tokenType", tokenType).add("expiresIn", expiresIn);
   }

}
