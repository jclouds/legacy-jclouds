/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.compute.domain;

import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.terremark.domain.KeyPair;

/**
 * @author Adrian Cole
 */
public class KeyPairCredentials extends Credentials {

   public static class Builder<T extends KeyPairCredentials> extends Credentials.Builder<T> {
      private String identity;
      private KeyPair keyPair;

      public Builder<T> identity(String identity) {
         this.identity = identity;
         return this;
      }

      public Builder<T> keyPair(KeyPair keyPair) {
         this.keyPair = keyPair;
         return this;
      }

      @SuppressWarnings("unchecked")
      public T build() {
         return (T) new KeyPairCredentials(identity, keyPair);
      }
   }

   public KeyPair getKeyPair() {
      return keyPair;
   }

   private final KeyPair keyPair;

   public KeyPairCredentials(String identity, KeyPair keyPair) {
      super(identity, keyPair.getPrivateKey());
      this.keyPair = keyPair;
   }

   public Builder<? extends KeyPairCredentials> toBuilder() {
      return new Builder<KeyPairCredentials>().identity(identity).keyPair(keyPair);
   }

}