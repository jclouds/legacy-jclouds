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
package org.jclouds.rackspace.clouddns.v1.domain;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * Update a Domain or Subdomain.
 * 
 * @author Everett Toews
 */
public class UpdateDomain {
   private final Optional<String> emailAddress;
   private final Optional<Integer> ttl;
   private final Optional<String> comment;

   private UpdateDomain(Optional<String> email, Optional<Integer> ttl, Optional<String> comment) {
      this.emailAddress = email;
      this.ttl = ttl;
      this.comment = comment;
   }

   public Optional<String> getEmail() {
      return emailAddress;
   }

   public Optional<Integer> getTTL() {
      return ttl;
   }

   public Optional<String> getComment() {
      return comment;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(emailAddress, ttl, comment);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      UpdateDomain that = UpdateDomain.class.cast(obj);

      return Objects.equal(this.emailAddress, that.emailAddress) && Objects.equal(this.ttl, that.ttl)
            && Objects.equal(this.comment, that.comment);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("email", emailAddress.orNull()).add("ttl", ttl.orNull())
            .add("comment", comment.orNull());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder {
      private Optional<String> emailAddress = Optional.absent();
      private Optional<Integer> ttl = Optional.absent();
      private Optional<String> comment = Optional.absent();

      /**
       * Email address to use for contacting the domain administrator.
       */
      public Builder email(String email) {
         this.emailAddress = Optional.fromNullable(email);
         return this;
      }

      /**
       * If specified, must be greater than 300.
       */
      public Builder ttl(Integer ttl) {
         this.ttl = Optional.fromNullable(ttl);
         return this;
      }

      /**
       * If included, its length must be less than or equal to 160 characters.
       */
      public Builder comment(String comment) {
         this.comment = Optional.fromNullable(comment);
         return this;
      }

      public UpdateDomain build() {
         return new UpdateDomain(emailAddress, ttl, comment);
      }

      public Builder from(UpdateDomain in) {
         return this.email(in.getEmail().orNull()).ttl(in.getTTL().orNull()).comment(in.getComment().orNull());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
