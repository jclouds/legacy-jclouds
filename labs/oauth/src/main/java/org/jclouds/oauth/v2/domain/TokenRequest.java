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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The complete token request.
 *
 * @author David Alves
 */
public class TokenRequest {

   private final Header header;
   private final ClaimSet claimSet;

   public TokenRequest(Header header, ClaimSet claimSet) {
      this.header = header;
      this.claimSet = claimSet;
   }

   public Header getHeader() {
      return header;
   }

   public ClaimSet getClaimSet() {
      return claimSet;
   }

   @Override
   public String toString() {
      return "TokenRequest{" +
              "header=" + header +
              ", claimSet=" + claimSet +
              '}';
   }

   public static class Builder {
      private Header header;
      private ClaimSet claimSet;

      public Builder header(Header header) {
         this.header = header;
         return this;
      }

      public Builder claimSet(ClaimSet claimSet) {
         this.claimSet = claimSet;
         return this;
      }

      public TokenRequest build() {
         return new TokenRequest(checkNotNull(header), checkNotNull(claimSet));
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TokenRequest that = (TokenRequest) o;

      if (!claimSet.equals(that.claimSet)) return false;
      if (!header.equals(that.header)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = header.hashCode();
      result = 31 * result + claimSet.hashCode();
      return result;
   }
}
