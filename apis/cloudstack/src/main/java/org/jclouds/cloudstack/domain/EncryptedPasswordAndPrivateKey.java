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
package org.jclouds.cloudstack.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 *
 * @author Andrei Savu
 */
public final class EncryptedPasswordAndPrivateKey {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromEncryptedPasswordAndPrivateKey(this);
   }

   public static final class Builder {

      protected String encryptedPassword;
      protected String privateKey;

      /**
       * @see EncryptedPasswordAndPrivateKey#getEncryptedPassword()
       */
      public Builder encryptedPassword(String encryptedPassword) {
         this.encryptedPassword = encryptedPassword;
         return this;
      }

      /**
       * @see EncryptedPasswordAndPrivateKey#getPrivateKey()
       */
      public Builder privateKey(String privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      public EncryptedPasswordAndPrivateKey build() {
         return new EncryptedPasswordAndPrivateKey(encryptedPassword, privateKey);
      }

      public Builder fromEncryptedPasswordAndPrivateKey(EncryptedPasswordAndPrivateKey in) {
         return encryptedPassword(in.getEncryptedPassword())
               .privateKey(in.getPrivateKey());
      }
   }

   private final String encryptedPassword;
   private final String privateKey;

   @ConstructorProperties({
         "encryptedPassword", "privateKey"
   })
   public EncryptedPasswordAndPrivateKey(@Nullable String encryptedPassword, @Nullable String privateKey) {
      this.encryptedPassword = encryptedPassword;
      this.privateKey = privateKey;
   }

   /**
    * @return the encrypted password String representation
    */
   @Nullable
   public String getEncryptedPassword() {
      return this.encryptedPassword;
   }

   /**
    * @return get the string representation of the private key
    */
   @Nullable
   public String getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(encryptedPassword, privateKey);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      EncryptedPasswordAndPrivateKey that = EncryptedPasswordAndPrivateKey.class.cast(obj);
      return Objects.equal(this.encryptedPassword, that.encryptedPassword)
            && Objects.equal(this.privateKey, that.privateKey);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("encryptedPassword", encryptedPassword).add("privateKey", privateKey).toString();
   }

}
