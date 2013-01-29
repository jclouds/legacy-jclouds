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
package org.jclouds.route53.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public final class ResourceRecordSet extends ForwardingList<String> {

   private final String name;
   private final Type type;
   private final Optional<Integer> ttl;
   private final ImmutableList<String> items;

   private ResourceRecordSet(String name, Type type, Optional<Integer> ttl, ImmutableList<String> items) {
      this.name = checkNotNull(name, "name");
      this.type = checkNotNull(type, "type of %s", name);
      this.ttl = checkNotNull(ttl, "ttl for %s", name);
      this.items = checkNotNull(items, "items for %s", name);
   }

   /**
    * The name of the domain.
    */
   public String getName() {
      return name;
   }

   /**
    * The resource record set type.
    */
   public Type getType() {
      return type;
   }

   /**
    * Present in all resource record sets except aliases. The resource record
    * cache time to live (TTL), in seconds.
    */
   public Optional<Integer> getTTL() {
      return ttl;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, type, ttl, items);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ResourceRecordSet other = ResourceRecordSet.class.cast(obj);
      return equal(this.name, other.name) && equal(this.type, other.type) && equal(this.ttl, other.ttl)
            && equal(this.items, other.items);
   }

   @Override
   public String toString() {
      return toStringHelper("").omitNullValues().add("name", name).add("type", type).add("ttl", ttl.orNull())
            .add("resourceRecords", items).toString();
   }

   @Override
   protected List<String> delegate() {
      return items;
   }

   public enum Type {
      A, AAAA, CNAME, MX, NS, PTR, SOA, SPF, SRV, TXT, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String name;
      private Type type;
      private Optional<Integer> ttl = Optional.absent();
      private ImmutableList.Builder<String> items = ImmutableList.<String> builder();

      /**
       * @see ResourceRecordSet#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see ResourceRecordSet#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceRecordSet#getTTL()
       */
      public Builder ttl(Integer ttl) {
         this.ttl = Optional.fromNullable(ttl);
         return this;
      }

      public Builder add(String item) {
         this.items.add(item);
         return this;
      }

      public Builder addAll(Iterable<String> items) {
         this.items.addAll(items);
         return this;
      }

      public ResourceRecordSet build() {
         return new ResourceRecordSet(name, type, ttl, items.build());
      }

      public Builder from(ResourceRecordSet in) {
         return this.name(in.name).type(in.type).ttl(in.ttl.orNull()).addAll(in.items);
      }
   }
}
