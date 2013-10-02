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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
public class CreateSubdomain {
   private final String name;
   private final String emailAddress;
   private final Optional<Integer> ttl;
   private final Optional<String> comment;

   private CreateSubdomain(String name, String email, Optional<Integer> ttl, Optional<String> comment) {
      this.name = checkNotNull(name, "name required");
      this.emailAddress = checkNotNull(email, "email required");
      this.ttl = ttl;
      this.comment = comment;
   }

   /**
    * @see Builder#name(String)
    */
   public String getName() {
      return name;
   }

   /**
    * @see Builder#email(String)
    */
   public String getEmail() {
      return emailAddress;
   }

   /**
    * @see Builder#ttl(Integer)
    */
   public Optional<Integer> getTTL() {
      return ttl;
   }

   /**
    * @see Builder#comment(String)
    */
   public Optional<String> getComment() {
      return comment;
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      CreateSubdomain that = CreateSubdomain.class.cast(obj);

      return Objects.equal(this.name, that.name);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("email", emailAddress)
            .add("ttl", ttl.orNull()).add("comment", comment.orNull());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder {
      private String name;
      private String emailAddress;
      private Optional<Integer> ttl = Optional.absent();
      private Optional<String> comment = Optional.absent();

      /**
       * The name for the subdomain. Must be a fully qualified domain name (FQDN) that doesn't end in a '.'.
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * Email address to use for contacting the domain administrator. Used as the email-addr (rname) in the SOA record.
       */
      public Builder email(String email) {
         this.emailAddress = email;
         return this;
      }

      /**
       * The duration in seconds that the record may be cached. If specified, must be greater than 300. The default
       * value, if not specified, is 3600.
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

      public CreateSubdomain build() {
         return new CreateSubdomain(name, emailAddress, ttl, comment);
      }

      public Builder from(CreateSubdomain in) {
         return this.name(in.getName()).email(in.getEmail()).ttl(in.getTTL().orNull())
               .comment(in.getComment().orNull());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
