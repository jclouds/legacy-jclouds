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
package org.jclouds.trmk.vcloud_0_8.domain;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

/**
 * an SSH keypair
 * 
 * @author Adrian Cole
 */
public class KeyPair {
   private final URI id;
   private final String name;
   private final boolean isDefault;
   @Nullable
   private final String privateKey;
   private final String fingerPrint;

   public KeyPair(URI id, String name, boolean isDefault, @Nullable String privateKey, String fingerPrint) {
      this.id = id;
      this.name = name;
      this.isDefault = isDefault;
      this.privateKey = privateKey;
      this.fingerPrint = fingerPrint;
   }

   public URI getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public boolean isDefault() {
      return isDefault;
   }

   @Nullable
   public String getPrivateKey() {
      return privateKey;
   }

   public String getFingerPrint() {
      return fingerPrint;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fingerPrint == null) ? 0 : fingerPrint.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + (isDefault ? 1231 : 1237);
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
      KeyPair other = (KeyPair) obj;
      if (fingerPrint == null) {
         if (other.fingerPrint != null)
            return false;
      } else if (!fingerPrint.equals(other.fingerPrint))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (isDefault != other.isDefault)
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
      return "Key [fingerPrint=" + fingerPrint + ", id=" + id + ", isDefault=" + isDefault + ", name=" + name
            + ", privateKey=" + (privateKey != null) + "]";
   }

}
