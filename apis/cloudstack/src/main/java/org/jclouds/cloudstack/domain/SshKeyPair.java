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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class SshKeyPair
 *
 * @author Vijay Kiran
 */
public class SshKeyPair {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSshKeyPair(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String fingerprint;
      protected String name;
      protected String privateKey;

      /**
       * @see SshKeyPair#getFingerprint()
       */
      public T fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return self();
      }

      /**
       * @see SshKeyPair#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see SshKeyPair#getPrivateKey()
       */
      public T privateKey(String privateKey) {
         this.privateKey = privateKey;
         return self();
      }

      public SshKeyPair build() {
         return new SshKeyPair(fingerprint, name, privateKey);
      }

      public T fromSshKeyPair(SshKeyPair in) {
         return this
               .fingerprint(in.getFingerprint())
               .name(in.getName())
               .privateKey(in.getPrivateKey());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String fingerprint;
   private final String name;
   private final String privateKey;

   @ConstructorProperties({
         "fingerprint", "name", "privatekey"
   })
   protected SshKeyPair(@Nullable String fingerprint, String name, @Nullable String privateKey) {
      this.fingerprint = fingerprint;
      this.name = checkNotNull(name, "name");
      this.privateKey = privateKey;
   }

   @Nullable
   public String getFingerprint() {
      return this.fingerprint;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(fingerprint, name, privateKey);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SshKeyPair that = SshKeyPair.class.cast(obj);
      return Objects.equal(this.fingerprint, that.fingerprint)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.privateKey, that.privateKey);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("fingerprint", fingerprint).add("name", name).add("privateKey", privateKey);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
