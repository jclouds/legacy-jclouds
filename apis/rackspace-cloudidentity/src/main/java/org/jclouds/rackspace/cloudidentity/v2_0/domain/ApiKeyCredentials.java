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
package org.jclouds.rackspace.cloudidentity.v2_0.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import com.google.common.base.Objects;

/**
 * ApiKey Credentials
 * 
 * @see <a href="http://docs.rackspace.com/servers/api/v2/cs-devguide/content/curl_auth.html">docs</a>

 * @author Adrian Cole
 */
@CredentialType(CloudIdentityCredentialTypes.API_KEY_CREDENTIALS)
public class ApiKeyCredentials {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromApiKeyCredentials(this);
   }

   public static ApiKeyCredentials createWithUsernameAndApiKey(String username, String apiKey) {
      return builder().apiKey(apiKey).username(username).build();
   }

   public static class Builder {
      protected String username;
      protected String apiKey;

      /**
       * @see ApiKeyCredentials#getUsername()
       */
      protected Builder apiKey(String apiKey) {
         this.apiKey = apiKey;
         return this;
      }

      /**
       * @see ApiKeyCredentials#getApiKey()
       */
      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public ApiKeyCredentials build() {
         return new ApiKeyCredentials(username, apiKey);
      }

      public Builder fromApiKeyCredentials(ApiKeyCredentials from) {
         return username(from.getUsername()).apiKey(from.getApiKey());
      }
   }

   protected final String username;
   protected final String apiKey;

   protected ApiKeyCredentials(String username, String apiKey) {
      this.username = checkNotNull(username, "username");
      this.apiKey = checkNotNull(apiKey, "apiKey");
   }

   /**
    * @return the username
    */
   public String getUsername() {
      return username;
   }

   /**
    * @return the apiKey
    */
   public String getApiKey() {
      return apiKey;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ApiKeyCredentials) {
         final ApiKeyCredentials other = ApiKeyCredentials.class.cast(object);
         return equal(username, other.username) && equal(apiKey, other.apiKey);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(username, apiKey);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("username", username).add("apiKey", apiKey).toString();
   }

}
