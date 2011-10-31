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
 * @author Vijay Kiran
 */
public class SSHKeyPair implements Comparable<SSHKeyPair> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String fingerprint;
      private String name;
      private String privateKey;

      public Builder fingerprint(String fingerprint) {
         this.fingerprint = fingerprint;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder privateKey(String privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      public SSHKeyPair build() {
         return new SSHKeyPair(fingerprint, name, privateKey);
      }
   }

   // for deserialization
   SSHKeyPair() {

   }

   private String fingerprint;
   private String name;
   @SerializedName("privatekey")
   private String privateKey;

   public SSHKeyPair(String fingerprint, String name, String privateKey) {
      this.fingerprint = fingerprint;
      this.name = name;
      this.privateKey = privateKey;
   }

   public String getFingerprint() {
      return fingerprint;
   }

   public String getName() {
      return name;
   }

   public String getPrivateKey() {
      return privateKey;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fingerprint == null) ? 0 : fingerprint.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());

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
      SSHKeyPair other = (SSHKeyPair) obj;
      if (fingerprint == null) {
         if (other.fingerprint != null)
            return false;
      } else if (!fingerprint.equals(other.fingerprint))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (privateKey == null) {
         if (other.privateKey != null)
            return false;
      } else if (!privateKey.equals(other.privateKey))
         return false;

      return true;
   }

   @Override
   public String toString() {
      return "[fingerprint=" + fingerprint + ", name=" + name + "]";
   }

   @Override
   public int compareTo(SSHKeyPair arg0) {
      return fingerprint.compareTo(arg0.getFingerprint());
   }

}
