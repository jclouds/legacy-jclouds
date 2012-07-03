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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class KeyPair
*/
public class KeyPair {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromKeyPair(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String publicKey;
      protected String privateKey;
      protected String userId;
      protected String name;
      protected String fingerprint;
   
      /** 
       * @see KeyPair#getPublicKey()
       */
      public T publicKey(String publicKey) {
         this.publicKey = publicKey;
         return self();
      }

      /** 
       * @see KeyPair#getPrivateKey()
       */
      public T privateKey(String privateKey) {
         this.privateKey = privateKey;
         return self();
      }

      /** 
       * @see KeyPair#getUserId()
       */
      public T userId(String userId) {
         this.userId = userId;
         return self();
      }

      /** 
       * @see KeyPair#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see KeyPair#getFingerprint()
       */
      public T fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return self();
      }

      public KeyPair build() {
         return new KeyPair(publicKey, privateKey, userId, name, fingerprint);
      }
      
      public T fromKeyPair(KeyPair in) {
         return this
                  .publicKey(in.getPublicKey())
                  .privateKey(in.getPrivateKey())
                  .userId(in.getUserId())
                  .name(in.getName())
                  .fingerprint(in.getFingerprint());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("public_key")
   private final String publicKey;
   @Named("private_key")
   private final String privateKey;
   @Named("user_id")
   private final String userId;
   private final String name;
   private final String fingerprint;

   @ConstructorProperties({
      "public_key", "private_key", "user_id", "name", "fingerprint"
   })
   protected KeyPair(@Nullable String publicKey, @Nullable String privateKey, @Nullable String userId, String name, @Nullable String fingerprint) {
      this.publicKey = publicKey;
      this.privateKey = privateKey;
      this.userId = userId;
      this.name = checkNotNull(name, "name");
      this.fingerprint = fingerprint;
   }

   @Nullable
   public String getPublicKey() {
      return this.publicKey;
   }

   @Nullable
   public String getPrivateKey() {
      return this.privateKey;
   }

   @Nullable
   public String getUserId() {
      return this.userId;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getFingerprint() {
      return this.fingerprint;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(publicKey, privateKey, userId, name, fingerprint);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      KeyPair that = KeyPair.class.cast(obj);
      return Objects.equal(this.publicKey, that.publicKey)
               && Objects.equal(this.privateKey, that.privateKey)
               && Objects.equal(this.userId, that.userId)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.fingerprint, that.fingerprint);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("publicKey", publicKey).add("privateKey", privateKey).add("userId", userId).add("name", name).add("fingerprint", fingerprint);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
