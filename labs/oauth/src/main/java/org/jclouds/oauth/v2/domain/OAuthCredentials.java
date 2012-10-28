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

import org.jclouds.domain.Credentials;

import java.security.PrivateKey;

/**
 * Special kind credentials for oauth authentication.
 * <p/>
 * OAuth authentication requires a {@link java.security.PrivateKey} to sign requests.
 */
public class OAuthCredentials extends Credentials {

   public final PrivateKey privateKey;

   public OAuthCredentials(String identity, String credential, PrivateKey privateKey) {
      super(identity, credential);
      this.privateKey = privateKey;
   }

   public static class Builder<T extends OAuthCredentials> extends Credentials.Builder<T> {

      protected PrivateKey privateKey;

      public Builder<T> privateKey(PrivateKey privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      public Builder<T> identity(String identity) {
         this.identity = identity;
         return this;
      }

      public Builder<T> credential(String credential) {
         this.credential = credential;
         return this;
      }

      @SuppressWarnings("unchecked")
      public T build() {
         return (T) new OAuthCredentials(identity, credential, privateKey);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      OAuthCredentials that = (OAuthCredentials) o;

      if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
      return result;
   }
}
