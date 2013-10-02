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
package org.jclouds.sts.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.domain.SessionCredentials;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public final class UserAndSessionCredentials {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private User user;
      private SessionCredentials credentials;
      private int packedPolicySize;

      /**
       * @see UserAndSessionCredentials#getUser()
       */
      public Builder user(User user) {
         this.user = user;
         return this;
      }

      /**
       * @see UserAndSessionCredentials#getCredentials()
       */
      public Builder credentials(SessionCredentials credentials) {
         this.credentials = credentials;
         return this;
      }

      /**
       * @see UserAndSessionCredentials#getPackedPolicySize()
       */
      public Builder packedPolicySize(int packedPolicySize) {
         this.packedPolicySize = packedPolicySize;
         return this;
      }

      public UserAndSessionCredentials build() {
         return new UserAndSessionCredentials(user, credentials, packedPolicySize);
      }

      public Builder from(UserAndSessionCredentials in) {
         return this.user(in.user).credentials(in.credentials).packedPolicySize(in.packedPolicySize);
      }
   }

   private final User user;
   private final SessionCredentials credentials;
   private final int packedPolicySize;

   private UserAndSessionCredentials(User user, SessionCredentials credentials, int packedPolicySize) {
      this.user = checkNotNull(user, "user");
      this.credentials = checkNotNull(credentials, "credentials for %s", user);
      this.packedPolicySize = checkNotNull(packedPolicySize, "packedPolicySize for %s", user);
   }

   /**
    * user correlating to {@link UserAndSessionCredentials#getCredentials()}
    */
   public User getUser() {
      return user;
   }

   /**
    * The temporary security credentials, which includes an Access Key ID, a
    * Secret Access Key, and a security token.
    */
   public SessionCredentials getCredentials() {
      return credentials;
   }

   /**
    * A percentage value that indicates the size of the policy in packed form.
    */
   public int getPackedPolicySize() {
      return packedPolicySize;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(user, credentials, packedPolicySize);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      UserAndSessionCredentials other = (UserAndSessionCredentials) obj;
      return Objects.equal(this.user, other.user) && Objects.equal(this.credentials, other.credentials)
            && Objects.equal(this.packedPolicySize, other.packedPolicySize);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("user", user).add("credentials", credentials)
            .add("packedPolicySize", packedPolicySize).toString();
   }
}
