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
import org.jclouds.domain.Credentials;

import java.security.PrivateKey;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Special kind credentials for oauth authentication that includes {@link java.security.PrivateKey} to sign
 * requests.
 */
public class OAuthCredentials extends Credentials {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromOauthCredentials(this);
   }

   public static class Builder extends Credentials.Builder<OAuthCredentials> {

      protected PrivateKey privateKey;

      /**
       * @see OAuthCredentials#privateKey
       */
      public Builder privateKey(PrivateKey privateKey) {
         this.privateKey = checkNotNull(privateKey);
         return this;
      }

      /**
       * @see Credentials#identity
       */
      public Builder identity(String identity) {
         this.identity = checkNotNull(identity);
         return this;
      }

      /**
       * @see Credentials#credential
       */
      public Builder credential(String credential) {
         this.credential = credential;
         return this;
      }

      public OAuthCredentials build() {
         return new OAuthCredentials(checkNotNull(identity), credential, privateKey);
      }

      public Builder fromOauthCredentials(OAuthCredentials credentials) {
         return new Builder().privateKey(credentials.privateKey).identity(credentials.identity)
                 .credential(credentials.credential);
      }
   }

   /**
    * The private key associated with Credentials#identity.
    * Used to sign token requests.
    */
   public final PrivateKey privateKey;

   public OAuthCredentials(String identity, String credential, PrivateKey privateKey) {
      super(identity, credential);
      this.privateKey = privateKey;
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
      OAuthCredentials other = (OAuthCredentials) obj;
      return equal(this.identity, other.identity) && equal(this.credential,
              other.credential) && equal(this.privateKey,
              other.privateKey);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(identity, credential, privateKey);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("identity", identity)
              .add("credential", credential != null ? credential.hashCode() : null).add("privateKey",
                      privateKey.hashCode());
   }
}
