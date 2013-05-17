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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
public class Record {
   private final String name;
   private final String type;
   private final Optional<Integer> ttl;
   private final String data;
   private final Integer priority;
   private final String comment;

   private Record(@Nullable String name, @Nullable String type, Optional<Integer> ttl, @Nullable String data,
         @Nullable Integer priority, @Nullable String comment) {
      this.name = name;
      this.type = type;
      this.ttl = ttl;
      this.data = data;
      this.priority = priority;
      this.comment = comment;
   }

   /**
    * @see Record.Builder#name(String)
    */
   public String getName() {
      return name;
   }

   /**
    * @see Record.Builder#type(String)
    */
   public String getType() {
      return type;
   }

   /**
    * @see Record.Builder#ttl(Integer)
    */
   public Optional<Integer> getTTL() {
      return ttl;
   }

   /**
    * @see Record.Builder#data(String)
    */
   public String getData() {
      return data;
   }

   /**
    * @see Record.Builder#priority(Integer)
    */
   @Nullable
   public Integer getPriority() {
      return priority;
   }

   /**
    * @see Record.Builder#comment(String)
    */
   @Nullable
   public String getComment() {
      return comment;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, type, ttl, data, priority, comment);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Record that = Record.class.cast(obj);

      return equal(this.name, that.name) && equal(this.type, that.type) && equal(this.ttl, that.ttl)
            && equal(this.data, that.data) && equal(this.priority, that.priority) && equal(this.comment, that.comment);
   }

   protected ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("name", name).add("type", type).add("ttl", ttl)
            .add("data", data).add("priority", priority).add("comment", comment);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public final static class Builder {
      private String name;
      private String type;
      private Optional<Integer> ttl = Optional.absent();
      private String data;
      private Integer priority;
      private String comment;

      /**
       * The name for the domain or subdomain. Must be a fully qualified domain name (FQDN) that doesn't end in a '.'.
       * </p>
       * Users can add one or more wildcard records to any domain or sub-domain on their account. For information on the
       * intent and use of wildcard records, see the DNS literature including RFC 1034, section 4.3.3, and RFC 4595.
       * </p>
       * Wildcards are supported for A, AAAA, CNAME, MX, SRV and TXT record types.
       * </p>
       * A valid wildcard DNS record is specified by using an asterisk ("*") as the leftmost part of a record name, for
       * example *.example.com. An asterisk in any other part of a record name is invalid. Only the asterisk ("*") is
       * accepted as a wildcard character.
       * </p>
       * For SRV records, this specifies the entire service name, which is made up of the service, protocol, and domain
       * name to which the record belongs. The service and protocol fields of the service name can be modified but not
       * the domain name field.
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * The record type to add.
       * </p>
       * See <a href="http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/supported_record_types.html">
       * Supported Record Types</a>
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * The duration in seconds that the record may be cached by clients. If specified, must be greater than 300. The
       * default value, if not specified, is 3600.
       */
      public Builder ttl(int ttl) {
         this.ttl = Optional.fromNullable(ttl);
         return this;
      }

      /**
       * @see Builder#ttl(int)
       */
      public Builder ttl(Optional<Integer> ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * The data field for PTR, A, and AAAA records must be a valid IPv4 or IPv6 IP address.
       */
      public Builder data(String data) {
         this.data = data;
         return this;
      }

      /**
       * Required for MX and SRV records, but forbidden for other record types. If specified, must be an integer from 0
       * to 65535.
       */
      public Builder priority(Integer priority) {
         this.priority = priority;
         return this;
      }

      /**
       * If included, its length must be less than or equal to 160 characters.
       */
      public Builder comment(String comment) {
         this.comment = comment;
         return this;
      }

      public Record build() {
         return new Record(name, type, ttl, data, priority, comment);
      }

      public Builder from(Record in) {
         return name(in.getName()).type(in.getType()).ttl(in.getTTL()).data(in.getData()).priority(in.getPriority())
               .comment(in.getComment());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }
}
