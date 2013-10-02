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
package org.jclouds.sts.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public final class User {
   public static User fromIdAndArn(String id, String arn) {
      return new User(id, arn);
   }

   private final String id;
   private final String arn;

   private User(String id, String arn) {
      this.id = checkNotNull(id, "id");
      this.arn = checkNotNull(arn, "arn for %s", id);
   }

   /**
    * The id of the federated user or assumed role. ex.
    * {@code ARO123EXAMPLE123:Bob}
    */
   public String getId() {
      return id;
   }

   /**
    * The arn of the federated user or assumed role.
    * 
    * ex. {@code arn:aws:sts::123456789012:federated-user/Bob} or
    * {@code arn:aws:sts::123456789012:assumed-role/demo/Bob}
    */
   public String getArn() {
      return arn;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, arn);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      User other = (User) obj;
      return Objects.equal(this.id, other.id) && Objects.equal(this.arn, other.arn);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("arn", arn).toString();
   }
}
