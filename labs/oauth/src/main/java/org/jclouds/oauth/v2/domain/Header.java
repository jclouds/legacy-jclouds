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
 * The header for the OAuth token, contains the signer algorithm's name and the type of the token
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/accounts/docs/OAuth2ServiceAccount">doc</a>
 */
public class Header {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromHeader(this);
   }

   public static class Builder {

      private String signerAlgorithm;
      private String type;

      /**
       * @see Header#getSignerAlgorithm()
       */
      public Builder signerAlgorithm(String signerAlgorithm) {
         this.signerAlgorithm = checkNotNull(signerAlgorithm);
         return this;
      }

      /**
       * @see Header#getType()
       */
      public Builder type(String type) {
         this.type = checkNotNull(type);
         return this;
      }

      public Header build() {
         return new Header(signerAlgorithm, type);
      }

      public Builder fromHeader(Header header) {
         return new Builder().signerAlgorithm(header.signerAlgorithm).type(header.type);
      }
   }

   private final String signerAlgorithm;
   private final String type;

   protected Header(String signerAlgorithm, String type) {
      this.signerAlgorithm = checkNotNull(signerAlgorithm);
      this.type = checkNotNull(type);
   }

   /**
    * The name of the algorithm used to compute the signature, e.g., "RS256"
    */
   public String getSignerAlgorithm() {
      return signerAlgorithm;
   }

   /**
    * The type of the token, e.g., "JWT"
    */
   public String getType() {
      return type;
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
      Header other = (Header) obj;
      return equal(this.signerAlgorithm, other.signerAlgorithm) && equal(this.type,
              other.type);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(signerAlgorithm, type);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("signerAlgorithm", signerAlgorithm)
              .add("type", type);
   }
}
