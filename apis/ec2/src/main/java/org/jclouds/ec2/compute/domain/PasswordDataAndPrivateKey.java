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
package org.jclouds.ec2.compute.domain;

import org.jclouds.ec2.domain.PasswordData;

import com.google.common.base.Objects;

/**
 * An encrypted Windows Administrator password, and the private key that can decrypt it.
 * 
 * @author Richard Downer
 */
public class PasswordDataAndPrivateKey {

   private final PasswordData passwordData;
   private final String privateKey;

   public PasswordDataAndPrivateKey(PasswordData passwordData, String privateKey) {
      this.passwordData = passwordData;
      this.privateKey = privateKey;
   }

   public PasswordData getPasswordData() {
      return passwordData;
   }

   public String getPrivateKey() {
      return privateKey;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(passwordData, privateKey);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PasswordDataAndPrivateKey other = PasswordDataAndPrivateKey.class.cast(obj);
      return Objects.equal(this.passwordData, other.passwordData) && Objects.equal(this.privateKey, other.privateKey);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("passwordData", passwordData).toString();
   }
}
