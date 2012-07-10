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
package org.jclouds.joyent.cloudapi.v6_5.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.joyent.cloudapi.v6_5.domain.Key;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class KeyAndPrivateKey {

   public static KeyAndPrivateKey fromKeyAndPrivateKey(Key key, String privateKey) {
      return new KeyAndPrivateKey(key, privateKey);
   }

   protected final Key key;
   protected final String privateKey;

   protected KeyAndPrivateKey(Key key, String privateKey) {
      this.key = checkNotNull(key, "key");
      this.privateKey = checkNotNull(privateKey, "privateKey");
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(key, privateKey);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      KeyAndPrivateKey other = (KeyAndPrivateKey) obj;
      return Objects.equal(key, other.key) && Objects.equal(privateKey, other.privateKey);
   }

   public Key getKey() {
      return key;
   }

   public String getPrivateKey() {
      return privateKey;
   }

   @Override
   public String toString() {
      return "[key=" + key + ", privateKey=" + privateKey + "]";
   }

}
