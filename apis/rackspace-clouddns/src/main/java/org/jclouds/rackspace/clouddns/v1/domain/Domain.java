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

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * A domain is an entity/container of all DNS-related information containing one or more records. Within Rackspace DNS,
 * the account which creates the domain is the domain owner.
 * 
 * @author Everett Toews
 */
public class Domain {
   private final int id;
   private final String name;
   private final String email;
   private final Optional<String> comment;
   private final Date created;
   private final Date updated;
   private final int accountId;
   private final int ttl;
   private final Set<String> nameservers;
   private final Set<Subdomain> subdomains;
   private final Set<RecordDetail> records;

   @ConstructorProperties({ "id", "name", "emailAddress", "comment", "created", "updated", "accountId", "ttl",
         "nameservers", "subdomains", "recordsList" })
   protected Domain(int id, String name, String email, @Nullable String comment, Date created, Date updated,
         int accountId, int ttl, @Nullable Set<String> nameservers, @Nullable Set<Subdomain> nameToSubdomain,
         @Nullable Set<RecordDetail> records) {
      this.id = id;
      this.name = name;
      this.email = email;
      this.comment = Optional.fromNullable(comment);
      this.created = created;
      this.updated = updated;
      this.accountId = accountId;
      this.ttl = ttl;
      this.nameservers = nameservers != null ? nameservers : ImmutableSet.<String> of();
      this.subdomains = nameToSubdomain != null ? nameToSubdomain : ImmutableSet.<Subdomain> of();
      this.records = records != null ? records : ImmutableSet.<RecordDetail> of();
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getEmail() {
      return email;
   }

   public Optional<String> getComment() {
      return comment;
   }

   public Date getCreated() {
      return created;
   }

   public Date getUpdated() {
      return updated;
   }

   public int getAccountId() {
      return accountId;
   }

   public int getTTL() {
      return ttl;
   }

   public Set<String> getNameservers() {
      return nameservers;
   }

   public Set<Subdomain> getSubdomains() {
      return subdomains;
   }

   public Set<RecordDetail> getRecords() {
      return records;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Domain that = Domain.class.cast(obj);

      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name).add("email", email)
            .add("comment", comment.orNull()).add("created", created).add("updated", updated)
            .add("accountId", accountId).add("ttl", ttl).add("nameservers", nameservers)
            .add("subdomains", subdomains).add("records", records);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected Domain from(Domain in) {
      return new Domain(in.getId(), in.getName(), in.getEmail(), in.getComment().orNull(), in.getCreated(),
            in.getUpdated(), in.getAccountId(), in.getTTL(), in.getNameservers(), in.getSubdomains(), in.getRecords());
   }

   public enum Format {
      BIND_9,

      UNRECOGNIZED;

      public static Format fromValue(String format) {
         try {
            return valueOf(checkNotNull(format, "format").toUpperCase());
         }
         catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
}
