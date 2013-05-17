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
import com.google.common.collect.ImmutableMap;

/**
 * Create a Domain or Subdomain.
 * 
 * @author Everett Toews
 */
public class CreateDomain {
   private final String name;
   private final String emailAddress;
   private final Optional<Integer> ttl;
   private final Optional<String> comment;
   // subdomains is an ImmutableMap for serialization
   private final ImmutableMap<String, Iterable<CreateSubdomain>> subdomains;
   // recordList is an ImmutableMap for serialization
   private final ImmutableMap<String, Iterable<Record>> recordsList;

   private CreateDomain(String name, String email, Optional<Integer> ttl, Optional<String> comment,
         ImmutableMap<String, Iterable<CreateSubdomain>> subdomains,
         ImmutableMap<String, Iterable<Record>> recordsList) {
      this.name = checkNotNull(name, "name required");
      this.emailAddress = checkNotNull(email, "email required");
      this.ttl = ttl;
      this.comment = comment;
      this.subdomains = subdomains != null ? subdomains : ImmutableMap.<String, Iterable<CreateSubdomain>> of();
      this.recordsList = recordsList != null ? recordsList : ImmutableMap.<String, Iterable<Record>> of();
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

   /**
    * @see Builder#subdomains(Iterable)
    */
   public Iterable<CreateSubdomain> getSubdomains() {
      return subdomains.get("domains");
   }

   /**
    * @see Builder#records(Iterable)
    */
   public Iterable<Record> getRecords() {
      return recordsList.get("records");
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
      CreateDomain that = CreateDomain.class.cast(obj);

      return Objects.equal(this.name, that.name);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("email", emailAddress)
            .add("ttl", ttl.orNull()).add("comment", comment.orNull()).add("subdomains", subdomains)
            .add("records", recordsList);
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
      private ImmutableMap<String, Iterable<CreateSubdomain>> subdomains;
      private ImmutableMap<String, Iterable<Record>> records;

      /**
       * The name for the domain or subdomain. Must be a fully qualified domain name (FQDN) that doesn't end in a '.'.
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
       * The duration in seconds that the record may be cached by clients. If specified, must be greater than 300. The
       * default value, if not specified, is 3600.
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

      /**
       * Create Subdomains of this Domain.
       */
      public Builder subdomains(Iterable<CreateSubdomain> subdomains) {
         if (subdomains != null) {
            this.subdomains = ImmutableMap.of("domains", subdomains);
         }

         return this;
      }

      /**
       * Create Records for this Domain.
       * </p>
       * See <a href="http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/supported_record_types.html">
       * Supported Record Types</a>
       */
      public Builder records(Iterable<Record> records) {
         if (records != null) {
            this.records = ImmutableMap.of("records", records);
         }

         return this;
      }

      public CreateDomain build() {
         return new CreateDomain(name, emailAddress, ttl, comment, subdomains, records);
      }

      public Builder from(CreateDomain in) {
         return this.name(in.getName()).email(in.getEmail()).ttl(in.getTTL().orNull())
               .comment(in.getComment().orNull()).subdomains(in.getSubdomains()).records(in.getRecords());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
