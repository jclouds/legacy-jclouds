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
package org.jclouds.domain;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class Credentials {
   public static class Builder<T extends Credentials> {
      protected String identity;
      protected String credential;

      public Builder<T> identity(String identity) {
         this.identity = identity;
         return this;
      }

      public Builder<T> credential(String credential) {
         this.credential = credential;
         return this;
      }

      @SuppressWarnings("unchecked")
      public T build() {
         return (T) new Credentials(identity, credential);
      }
   }

   public final String identity;
   public final String credential;

   public Credentials(String identity, String credential) {
      this.identity = identity;
      this.credential = credential;
   }

   public Builder<? extends Credentials> toBuilder() {
      return new Builder<Credentials>().identity(identity).credential(credential);
   }

   @Override
   public String toString() {
      return "[identity=" + identity + ", credential=" + credential + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(identity, credential);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!(obj instanceof Credentials))
         return false;
      Credentials other = (Credentials) obj;
      if (!Objects.equal(identity, other.identity))
         return false;
      if (!Objects.equal(credential, other.credential))
         return false;
      return true;
   }
}
