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

package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Representation of the API keypair response
 *
 * @author Andrei Savu
 */
public class ApiKeyPair implements Comparable<ApiKeyPair> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String apiKey;
      private String secretKey;

      public Builder apiKey(String apiKey) {
         this.apiKey = apiKey;
         return this;
      }

      public Builder secretKey(String secretKey) {
         this.secretKey = secretKey;
         return this;
      }

      public ApiKeyPair build() {
         return new ApiKeyPair(apiKey, secretKey);
      }
   }

   // for deserialization
   ApiKeyPair() {
   }

   @SerializedName("apikey")
   private String apiKey;
   @SerializedName("secretkey")
   private String secretKey;

   public ApiKeyPair(String apiKey, String secretKey) {
      this.apiKey = apiKey;
      this.secretKey = secretKey;
   }

   public String getSecretKey() {
      return secretKey;
   }

   public String getApiKey() {
      return apiKey;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ApiKeyPair that = (ApiKeyPair) o;

      if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null)
         return false;
      if (secretKey != null ? !secretKey.equals(that.secretKey) : that.secretKey != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = apiKey != null ? apiKey.hashCode() : 0;
      result = 31 * result + (secretKey != null ? secretKey.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "ApiKeyPair{" +
         "apiKey='" + apiKey + '\'' +
         ", secretKey='" + secretKey + '\'' +
         '}';
   }

   @Override
   public int compareTo(ApiKeyPair arg0) {
      return apiKey.compareTo(arg0.getApiKey());
   }

}
