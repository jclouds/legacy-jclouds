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
import com.google.common.base.Objects.ToStringHelper;

/**
 * Representation of the API keypair response
 *
 * @author Andrei Savu
 */
public class ApiKeyPair {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiKeyPair(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String apiKey;
      protected String secretKey;

      /**
       * @see ApiKeyPair#getApiKey()
       */
      public T apiKey(String apiKey) {
         this.apiKey = apiKey;
         return self();
      }

      /**
       * @see ApiKeyPair#getSecretKey()
       */
      public T secretKey(String secretKey) {
         this.secretKey = secretKey;
         return self();
      }

      public ApiKeyPair build() {
         return new ApiKeyPair(apiKey, secretKey);
      }

      public T fromApiKeyPair(ApiKeyPair in) {
         return this
               .apiKey(in.getApiKey())
               .secretKey(in.getSecretKey());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String apiKey;
   private final String secretKey;

   @ConstructorProperties({
         "apikey", "secretkey"
   })
   protected ApiKeyPair(@Nullable String apiKey, @Nullable String secretKey) {
      this.apiKey = apiKey;
      this.secretKey = secretKey;
   }

   @Nullable
   public String getApiKey() {
      return this.apiKey;
   }

   @Nullable
   public String getSecretKey() {
      return this.secretKey;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(apiKey, secretKey);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ApiKeyPair that = ApiKeyPair.class.cast(obj);
      return Objects.equal(this.apiKey, that.apiKey)
            && Objects.equal(this.secretKey, that.secretKey);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("apiKey", apiKey).add("secretKey", secretKey);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
