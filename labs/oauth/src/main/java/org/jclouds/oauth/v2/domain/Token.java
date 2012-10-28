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

import java.beans.ConstructorProperties;

/**
 * The oauth token, obtained upon a sucessful token request and ready to embed in requests.
 *
 * @author David Alves
 */
public class Token {

   private final String accessToken;
   private final String tokenType;
   private final long expiresIn;

   @ConstructorProperties({"access_token", "token_type", "expires_in"})
   public Token(String accessToken, String tokenType, long expiresIn) {
      this.accessToken = accessToken;
      this.tokenType = tokenType;
      this.expiresIn = expiresIn;
   }

   public String getAccessToken() {
      return accessToken;
   }

   public String getTokenType() {
      return tokenType;
   }

   public long getExpiresIn() {
      return expiresIn;
   }

   @Override
   public String toString() {
      return "Token{" +
              "accessToken='" + accessToken + '\'' +
              ", tokenType='" + tokenType + '\'' +
              ", expiresIn=" + expiresIn +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Token token = (Token) o;

      if (expiresIn != token.expiresIn) return false;
      if (!accessToken.equals(token.accessToken)) return false;
      if (!tokenType.equals(token.tokenType)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = accessToken.hashCode();
      result = 31 * result + tokenType.hashCode();
      result = 31 * result + (int) (expiresIn ^ (expiresIn >>> 32));
      return result;
   }
}
