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
 * @author Andrei Savu
 */
public class EncryptedPassword implements Comparable<EncryptedPassword> {

   @SerializedName("encryptedpassword")
   private String encryptedPassword;

   public EncryptedPassword(String encryptedPassword) {
      this.encryptedPassword = encryptedPassword;
   }

   EncryptedPassword() { /* for serializer */ }

   /**
    * @return the string representation of the encrypted password
    */
   public String getEncryptedPassword() {
      return encryptedPassword;
   }

   @Override
   public int hashCode() {
      return encryptedPassword.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      EncryptedPassword that = (EncryptedPassword) o;

      if (encryptedPassword != null ? !encryptedPassword.equals(that.encryptedPassword) : that.encryptedPassword != null)
         return false;

      return true;
   }

   @Override
   public String toString() {
      return "EncryptedPassword{" +
         "encryptedPassword='" + encryptedPassword + '\'' +
         '}';
   }

   @Override
   public int compareTo(EncryptedPassword arg0) {
      return encryptedPassword.compareTo(arg0.getEncryptedPassword());
   }
}
