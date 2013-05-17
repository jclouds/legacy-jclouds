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
package org.jclouds.route53.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public class ResourceRecordSetIterable extends IterableWithMarker<ResourceRecordSet> {

   private final Iterable<ResourceRecordSet> items;
   private final Optional<NextRecord> nextRecord;

   private ResourceRecordSetIterable(Iterable<ResourceRecordSet> items, @Nullable NextRecord nextRecord) {
      this.items = checkNotNull(items, "items");
      this.nextRecord = Optional.fromNullable(nextRecord);
   }

   /**
    * present when the list is not truncated
    */
   public Optional<NextRecord> nextRecord() {
      return nextRecord;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Optional<Object> nextMarker() {
      return Optional.class.cast(nextRecord);
   }

   @Override
   public Iterator<ResourceRecordSet> iterator() {
      return items.iterator();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(items, nextRecord);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ResourceRecordSetIterable that = ResourceRecordSetIterable.class.cast(obj);
      return equal(this.items, that.items) && equal(this.nextRecord, that.nextRecord);
   }

   @Override
   public String toString() {
      return toStringHelper("").omitNullValues().add("items", items).add("nextRecord", nextRecord.orNull()).toString();
   }

   /**
    * If the results were truncated, this holds the position of the next item.
    */
   public static class NextRecord {
      public static NextRecord name(String name) {
         return new NextRecord(name, null, null);
      }

      public static NextRecord nameAndType(String name, String type) {
         return new NextRecord(name, type, null);
      }

      public static NextRecord nameTypeAndIdentifier(String name, String type, String identifier) {
         return new NextRecord(name, type, identifier);
      }

      private final String name;
      private final Optional<String> type;
      private final Optional<String> identifier;

      private NextRecord(String name, String type, String identifier) {
         this.name = checkNotNull(name, "name");
         this.type = Optional.fromNullable(type);
         this.identifier = Optional.fromNullable(identifier);
      }

      /**
       * If the results were truncated, the name of the next record in the list.
       */
      public String getName() {
         return name;
      }

      /**
       * the type of the next record in the list.
       */
      public Optional<String> getType() {
         return type;
      }

      /**
       * Weighted and latency resource record sets only.
       */
      public Optional<String> getIdentifier() {
         return identifier;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(name, type, identifier);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         NextRecord that = NextRecord.class.cast(obj);
         return equal(this.name, that.name) && equal(this.type, that.type) && equal(this.identifier, that.identifier);
      }

      @Override
      public String toString() {
         return toStringHelper("").omitNullValues().add("name", name).add("type", type.orNull())
               .add("identifier", identifier.orNull()).toString();
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private ImmutableList.Builder<ResourceRecordSet> items = ImmutableList.<ResourceRecordSet> builder();
      private String nextRecordName;
      private String nextRecordType;
      private String nextRecordIdentifier;

      public Builder add(ResourceRecordSet item) {
         this.items.add(item);
         return this;
      }

      public Builder addAll(Iterable<ResourceRecordSet> items) {
         this.items.addAll(items);
         return this;
      }

      public Builder nextRecordName(String nextRecordName) {
         this.nextRecordName = nextRecordName;
         return this;
      }

      public Builder nextRecordType(String nextRecordType) {
         this.nextRecordType = nextRecordType;
         return this;
      }

      public Builder nextRecordIdentifier(String nextRecordIdentifier) {
         this.nextRecordIdentifier = nextRecordIdentifier;
         return this;
      }

      public ResourceRecordSetIterable build() {
         NextRecord nextRecord = nextRecordName != null ? new NextRecord(nextRecordName, nextRecordType,
               nextRecordIdentifier) : null;
         return new ResourceRecordSetIterable(items.build(), nextRecord);
      }
   }
}
