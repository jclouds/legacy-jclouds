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

package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * Api AccessKey Credentials
 * 
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/POST_authenticate_v2.0_tokens_Service_API_Client_Operations.html#d662e583"
 *      />
 * @author Adrian Cole
 */
public class ApiAccessKeyCredentials {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSecretKeyCredentials(this);
   }

   public static ApiAccessKeyCredentials createWithAccessKeyAndSecretKey(String accessKey, String secretKey) {
      return builder().secretKey(secretKey).accessKey(accessKey).build();
   }

   public static class Builder {
      protected String accessKey;
      protected String secretKey;

      /**
       * @see ApiAccessKeyCredentials#getAccessKey()
       */
      protected Builder secretKey(String secretKey) {
         this.secretKey = secretKey;
         return this;
      }

      /**
       * @see ApiAccessKeyCredentials#getSecretKey()
       */
      public Builder accessKey(String accessKey) {
         this.accessKey = accessKey;
         return this;
      }

      public ApiAccessKeyCredentials build() {
         return new ApiAccessKeyCredentials(accessKey, secretKey);
      }

      public Builder fromSecretKeyCredentials(ApiAccessKeyCredentials from) {
         return accessKey(from.getAccessKey()).secretKey(from.getSecretKey());
      }
   }

   protected final String accessKey;
   protected final String secretKey;

   protected ApiAccessKeyCredentials(String accessKey, String secretKey) {
      this.accessKey = checkNotNull(accessKey, "accessKey");
      this.secretKey = checkNotNull(secretKey, "secretKey");
   }

   /**
    * @return the accessKey
    */
   public String getAccessKey() {
      return accessKey;
   }

   /**
    * @return the secretKey
    */
   public String getSecretKey() {
      return secretKey;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ApiAccessKeyCredentials) {
         final ApiAccessKeyCredentials other = ApiAccessKeyCredentials.class.cast(object);
         return equal(accessKey, other.accessKey) && equal(secretKey, other.secretKey);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accessKey, secretKey);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("accessKey", accessKey).add("secretKey", secretKey).toString();
   }

}
