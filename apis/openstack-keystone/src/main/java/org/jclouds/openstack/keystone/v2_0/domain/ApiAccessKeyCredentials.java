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
package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Api AccessKey Credentials
 *
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/POST_authenticate_v2.0_tokens_Service_API_Client_Operations.html#d662e583"
/>
 * @author Adrian Cole
 */
@CredentialType(CredentialTypes.API_ACCESS_KEY_CREDENTIALS)
public class ApiAccessKeyCredentials {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiAccessKeyCredentials(this);
   }

   public static ApiAccessKeyCredentials createWithAccessKeyAndSecretKey(String accessKey, String secretKey) {
      return new ApiAccessKeyCredentials(accessKey, secretKey);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String accessKey;
      protected String secretKey;

      /**
       * @see ApiAccessKeyCredentials#getAccessKey()
       */
      public T accessKey(String accessKey) {
         this.accessKey = accessKey;
         return self();
      }

      /**
       * @see ApiAccessKeyCredentials#getSecretKey()
       */
      public T secretKey(String secretKey) {
         this.secretKey = secretKey;
         return self();
      }

      public ApiAccessKeyCredentials build() {
         return new ApiAccessKeyCredentials(accessKey, secretKey);
      }

      public T fromApiAccessKeyCredentials(ApiAccessKeyCredentials in) {
         return this
               .accessKey(in.getAccessKey())
               .secretKey(in.getSecretKey());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String accessKey;
   private final String secretKey;

   @ConstructorProperties({
         "accessKey", "secretKey"
   })
   protected ApiAccessKeyCredentials(String accessKey, String secretKey) {
      this.accessKey = checkNotNull(accessKey, "accessKey");
      this.secretKey = checkNotNull(secretKey, "secretKey");
   }

   /**
    * @return the accessKey
    */
   public String getAccessKey() {
      return this.accessKey;
   }

   /**
    * @return the secretKey
    */
   public String getSecretKey() {
      return this.secretKey;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accessKey, secretKey);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ApiAccessKeyCredentials that = ApiAccessKeyCredentials.class.cast(obj);
      return Objects.equal(this.accessKey, that.accessKey)
            && Objects.equal(this.secretKey, that.secretKey);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("accessKey", accessKey).add("secretKey", secretKey);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
