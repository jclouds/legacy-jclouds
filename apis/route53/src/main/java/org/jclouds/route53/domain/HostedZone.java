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

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
public final class HostedZone {

   private final String id;
   private final String name;
   private final String callerReference;
   private final int resourceRecordSetCount;
   private final Optional<String> comment;

   private HostedZone(String id, String name, String callerReference, int resourceRecordSetCount, Optional<String> comment) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.callerReference = checkNotNull(callerReference, "callerReference for %s", name);
      this.resourceRecordSetCount = checkNotNull(resourceRecordSetCount, "resourceRecordSetCount for %s", name);
      this.comment = checkNotNull(comment, "comment for %s", comment);
   }

   /**
    * The ID of the hosted zone.
    */
   public String getId() {
      return id;
   }

   /**
    * The name of the domain.
    */
   public String getName() {
      return name;
   }

   /**
    * A unique string that identifies the request to create the hosted zone.
    */
   public String getCallerReference() {
      return callerReference;
   }

   /**
    * count of record sets
    */
   public int getResourceRecordSetCount() {
      return resourceRecordSetCount;
   }

   public Optional<String> getComment() {
      return comment;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, callerReference);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      HostedZone that = HostedZone.class.cast(obj);
      return equal(this.id, that.id) && equal(this.name, that.name)
            && equal(this.callerReference, that.callerReference);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("id", id).add("name", name)
            .add("callerReference", callerReference).add("resourceRecordSetCount", resourceRecordSetCount)
            .add("comment", comment.orNull()).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String id;
      private String name;
      private String callerReference;
      private int resourceRecordSetCount;
      private Optional<String> comment = Optional.absent();

      /**
       * @see HostedZone#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see HostedZone#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see HostedZone#getCallerReference()
       */
      public Builder callerReference(String callerReference) {
         this.callerReference = callerReference;
         return this;
      }

      /**
       * @see HostedZone#getResourceRecordSetCount()
       */
      public Builder resourceRecordSetCount(int resourceRecordSetCount) {
         this.resourceRecordSetCount = resourceRecordSetCount;
         return this;
      }

      /**
       * @see HostedZone#getComment()
       */
      public Builder comment(String comment) {
         this.comment = Optional.fromNullable(comment);
         return this;
      }

      public HostedZone build() {
         return new HostedZone(id, name, callerReference, resourceRecordSetCount, comment);
      }

      public Builder from(HostedZone in) {
         return this.id(in.id).name(in.name).callerReference(in.callerReference)
               .resourceRecordSetCount(in.resourceRecordSetCount).comment(in.comment.orNull());
      }
   }
}
