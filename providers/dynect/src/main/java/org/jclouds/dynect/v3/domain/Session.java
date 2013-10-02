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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public final class Session {

   public static Session forTokenAndVersion(String token, String version) {
      return new Session(token, version);
   }
   
   private final String token;
   private final String version;

   @ConstructorProperties({"token", "version"})
   private Session(String token, String version) {
      this.token = checkNotNull(token, "token");
      this.version = checkNotNull(version, "version for %s", token);
   }

   /**
    * The authentication token
    */
   public String getToken() {
      return token;
   }

   /**
    * The current version of the server
    */
   public String getVersion() {
      return version;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(token, version);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Session other = Session.class.cast(obj);
      return Objects.equal(this.token, other.token) && Objects.equal(this.version, other.version);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("token", token).add("version", version).toString();
   }
}
