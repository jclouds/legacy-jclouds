/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.jclouds.domain.Credentials;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * AWS credentials for API authentication.
 * 
 * @see <a href=
 *      "http://docs.aws.amazon.com/STS/latest/APIReference/API_Credentials.html"
 *      />
 * 
 * @author Adrian Cole
 */
public final class SessionCredentials extends Credentials {

   private final String sessionToken;
   private final Optional<Date> expiration;

   private SessionCredentials(String accessKeyId, String secretAccessKey, String sessionToken, Optional<Date> expiration) {
      super(checkNotNull(accessKeyId, "accessKeyId"), checkNotNull(secretAccessKey, "secretAccessKey for %s",
            accessKeyId));
      this.sessionToken = checkNotNull(sessionToken, "sessionToken for %s", accessKeyId);
      this.expiration = checkNotNull(expiration, "expiration for %s", accessKeyId);
   }

   /**
    * AccessKeyId ID that identifies the temporary credentials.
    */
   public String getAccessKeyId() {
      return identity;
   }

   /**
    * The Secret Access Key to sign requests.
    */
   public String getSecretAccessKey() {
      return credential;
   }

   /**
    * The security token that users must pass to the service API to use the
    * temporary credentials.
    */
   public String getSessionToken() {
      return sessionToken;
   }

   /**
    * The date on which these credentials expire.
    */
   public Optional<Date> getExpiration() {
      return expiration;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(identity, credential, sessionToken, expiration);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SessionCredentials other = (SessionCredentials) obj;
      return Objects.equal(this.identity, other.identity) && Objects.equal(this.credential, other.credential)
            && Objects.equal(this.sessionToken, other.sessionToken) && Objects.equal(this.expiration, other.expiration);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("accessKeyId", identity)
            .add("sessionToken", sessionToken).add("expiration", expiration.orNull()).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder extends Credentials.Builder<SessionCredentials> {
      private String accessKeyId;
      private String secretAccessKey;
      private String sessionToken;
      private Optional<Date> expiration = Optional.absent();

      @Override
      public Builder identity(String identity) {
         return accessKeyId(identity);
      }

      @Override
      public Builder credential(String credential) {
         return secretAccessKey(credential);
      }

      /**
       * @see SessionCredentials#getAccessKeyId()
       */
      public Builder accessKeyId(String accessKeyId) {
         this.accessKeyId = accessKeyId;
         return this;
      }

      /**
       * @see SessionCredentials#getSecretAccessKey()
       */
      public Builder secretAccessKey(String secretAccessKey) {
         this.secretAccessKey = secretAccessKey;
         return this;
      }

      /**
       * @see SessionCredentials#getSessionToken()
       */
      public Builder sessionToken(String sessionToken) {
         this.sessionToken = sessionToken;
         return this;
      }

      /**
       * @see SessionCredentials#getExpiration()
       */
      public Builder expiration(Date expiration) {
         this.expiration = Optional.fromNullable(expiration);
         return this;
      }

      public SessionCredentials build() {
         return new SessionCredentials(accessKeyId, secretAccessKey, sessionToken, expiration);
      }

      public Builder from(SessionCredentials in) {
         return this.accessKeyId(in.identity).secretAccessKey(in.credential).sessionToken(in.sessionToken)
               .expiration(in.expiration.orNull());
      }
   }
}
