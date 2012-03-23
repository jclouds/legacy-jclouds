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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.toStringHelper;

import org.jclouds.javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class KeyPair implements Comparable<KeyPair> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromKeyPair(this);
   }

   public static class Builder {

      private String publicKey;
      private String privateKey;
      private String userId;
      private String name;
      private String fingerprint;

      public Builder publicKey(String publicKey) {
         this.publicKey = publicKey;
         return this;
      }

      public Builder privateKey(String privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      public Builder userId(String userId) {
         this.userId = userId;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      public KeyPair build() {
         return new KeyPair(publicKey, privateKey, userId, name, fingerprint);
      }

      public Builder fromKeyPair(KeyPair in) {
         return publicKey(in.getPublicKey()).privateKey(in.getPrivateKey()).userId(in.getUserId()).name(in.getName())
               .fingerprint(in.getFingerprint());
      }

   }

   @SerializedName("public_key")
   String publicKey;
   @SerializedName("private_key")
   String privateKey;
   @SerializedName("user_id")
   String userId;
   String name;
   String fingerprint;

   protected KeyPair(String publicKey, String privateKey, @Nullable String userId, String name, String fingerprint) {
      this.publicKey = publicKey;
      this.privateKey = privateKey;
      this.userId = userId;
      this.name = name;
      this.fingerprint = fingerprint;
   }

   public String getPublicKey() {
      return this.publicKey;
   }

   public String getPrivateKey() {
      return this.privateKey;
   }

   public String getUserId() {
      return this.privateKey;
   }

   public String getName() {
      return this.name;
   }

   public String getFingerprint() {
      return this.fingerprint;
   }

   @Override
   public int compareTo(KeyPair o) {
      return this.fingerprint.compareTo(o.getFingerprint());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
      result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((fingerprint == null) ? 0 : fingerprint.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      KeyPair other = (KeyPair) obj;
      if (publicKey == null) {
         if (other.publicKey != null)
            return false;
      } else if (!publicKey.equals(other.publicKey))
         return false;
      if (privateKey == null) {
         if (other.privateKey != null)
            return false;
      } else if (!privateKey.equals(other.privateKey))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (fingerprint == null) {
         if (other.fingerprint != null)
            return false;
      } else if (!fingerprint.equals(other.fingerprint))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return toStringHelper("").add("userId", userId).add("name", name).add("fingerprint", fingerprint).toString();
   }
}