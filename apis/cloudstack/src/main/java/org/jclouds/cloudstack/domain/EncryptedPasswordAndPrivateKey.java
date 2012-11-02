/*
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
package org.jclouds.cloudstack.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class EncryptedPasswordAndPrivateKey
 *
 * @author Andrei Savu
 */
public class EncryptedPasswordAndPrivateKey {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEncryptedPasswordAndPrivateKey(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String encryptedPassword;
      protected String privateKey;

      /**
       * @see EncryptedPasswordAndPrivateKey#getEncryptedPassword()
       */
      public T encryptedPassword(String encryptedPassword) {
         this.encryptedPassword = encryptedPassword;
         return self();
      }

      /**
       * @see EncryptedPasswordAndPrivateKey#getPrivateKey()
       */
      public T privateKey(String privateKey) {
         this.privateKey = privateKey;
         return self();
      }

      public EncryptedPasswordAndPrivateKey build() {
         return new EncryptedPasswordAndPrivateKey(encryptedPassword, privateKey);
      }

      public T fromEncryptedPasswordAndPrivateKey(EncryptedPasswordAndPrivateKey in) {
         return this
               .encryptedPassword(in.getEncryptedPassword())
               .privateKey(in.getPrivateKey());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String encryptedPassword;
   private final String privateKey;

   @ConstructorProperties({
         "encryptedPassword", "privateKey"
   })
   protected EncryptedPasswordAndPrivateKey(@Nullable String encryptedPassword, @Nullable String privateKey) {
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

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("encryptedPassword", encryptedPassword).add("privateKey", privateKey);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
