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

import com.google.common.base.Objects;

/**
 * @author Andrei Savu
 */
public class EncryptedPasswordAndPrivateKey {

   private final String encryptedPassword;
   private final String privateKey;

   public EncryptedPasswordAndPrivateKey(String encryptedPassword, String privateKey) {
      this.encryptedPassword = encryptedPassword;
      this.privateKey = privateKey;
   }

   /**
    * @return the encrypted password String representation
    */
   public String getEncryptedPassword() {
      return encryptedPassword;
   }

   /**
    * @return get the string representation of the private key
    */
   public String getPrivateKey() {
      return privateKey;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      EncryptedPasswordAndPrivateKey that = (EncryptedPasswordAndPrivateKey) o;

      if (!Objects.equal(encryptedPassword, that.encryptedPassword)) return false;
      if (!Objects.equal(privateKey, that.privateKey)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(encryptedPassword, privateKey);
   }

   @Override
   public String toString() {
      return "EncryptedPasswordAndPrivateKey{" +
         "encryptedPassword='" + encryptedPassword + '\'' +
         ", privateKey='" + privateKey + '\'' +
         '}';
   }
}
