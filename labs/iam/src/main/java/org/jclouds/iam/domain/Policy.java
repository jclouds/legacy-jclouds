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
package org.jclouds.iam.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * @see <a href="http://docs.aws.amazon.com/IAM/latest/APIReference/API_GetUserPolicy.html" />
 * 
 * @author Adrian Cole
 */
public final class Policy {

   private final String owner;
   private final String name;
   private final String document;

   private Policy(String owner, String name, String document) {
      this.name = checkNotNull(name, "name");
      this.owner = checkNotNull(owner, "owner for %s", owner);
      this.document = checkNotNull(document, "document for %s", owner);
   }

   /**
    * The group, user, or role the policy is associated with.
    */
   public String getOwner() {
      return owner;
   }

   /**
    * friendly name ex. {@code Developers}
    */
   public String getName() {
      return name;
   }

   /**
    * The policy document.
    */
   public String getDocument() {
      return document;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(owner, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Policy that = Policy.class.cast(obj);
      return equal(this.owner, that.owner) && equal(this.name, that.name);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("owner", owner).add("name", name).add("document", document).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static class Builder {
      private String owner;
      private String name;
      private String document;

      /**
       * @see Policy#getOwner()
       */
      public Builder owner(String owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @see Policy#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Policy#getDocument()
       */
      public Builder document(String document) {
         this.document = document;
         return this;
      }

      public Policy build() {
         return new Policy(owner, name, document);
      }

      public Builder from(Policy in) {
         return owner(in.owner).name(in.name).document(in.document);
      }
   }
}
