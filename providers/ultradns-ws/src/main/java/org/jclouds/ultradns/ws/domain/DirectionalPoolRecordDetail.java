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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
public class DirectionalPoolRecordDetail {

   private final String zoneName;
   private final String name;
   private final String id;
   private final Optional<IdAndName> group;
   private final Optional<IdAndName> geolocationGroup;
   private final Optional<IdAndName> sourceIpGroup;
   private final DirectionalPoolRecord record;

   private DirectionalPoolRecordDetail(String zoneName, String name, String id,
         Optional<IdAndName> group, Optional<IdAndName> geolocationGroup,
         Optional<IdAndName> sourceIpGroup, DirectionalPoolRecord record) {
      this.zoneName = checkNotNull(zoneName, "zoneName");
      this.name = checkNotNull(name, "name");
      this.id = checkNotNull(id, "id");
      this.group = checkNotNull(group, "group of %s/%s/%s", zoneName, name, id);
      this.geolocationGroup = checkNotNull(geolocationGroup, "geolocationGroup of %s/%s/%s", zoneName, name, id);
      this.sourceIpGroup = checkNotNull(sourceIpGroup, "sourceIpGroup of %s/%s/%s", zoneName, name, id);
      this.record = checkNotNull(record, "record of %s/%s/%s", zoneName, name, id);
   }

   public String getZoneName() {
      return zoneName;
   }

   public String getName() {
      return name;
   }

   public String getId() {
      return id;
   }

   /**
    * group containing all regions that you have not specifically configured in {@link #getGeolocationGroup()}
    */
   public Optional<IdAndName> getGroup() {
      return group;
   }

   /**
    * group containing territories.
    */
   public Optional<IdAndName> getGeolocationGroup() {
      return geolocationGroup;
   }

   /**
    * group containing IPV4 or IPV6 ranges. 
    */
   public Optional<IdAndName> getSourceIpGroup() {
      return sourceIpGroup;
   }

   public DirectionalPoolRecord getRecord() {
      return record;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneName, name, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DirectionalPoolRecordDetail that = DirectionalPoolRecordDetail.class.cast(obj);
      return equal(this.zoneName, that.zoneName) && equal(this.name, that.name) && equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("zoneName", zoneName).add("name", name).add("id", id)
            .add("group", group.orNull()).add("geolocationGroup", geolocationGroup.orNull())
            .add("sourceIpGroup", sourceIpGroup.orNull()).add("record", record).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String zoneName;
      private String name;
      private String id;
      private Optional<IdAndName> group = Optional.absent();
      private Optional<IdAndName> geolocationGroup = Optional.absent();
      private Optional<IdAndName> sourceIpGroup = Optional.absent();
      private DirectionalPoolRecord record;

      /**
       * @see DirectionalPoolRecordDetail#getZoneName()
       */
      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

      /**
       * @see DirectionalPoolRecordDetail#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see DirectionalPoolRecordDetail#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see DirectionalPoolRecordDetail#getGroup()
       */
      public Builder group(IdAndName group) {
         this.group = Optional.fromNullable(group);
         return this;
      }

      /**
       * @see DirectionalPoolRecordDetail#getGeolocationGroup()
       */
      public Builder geolocationGroup(IdAndName geolocationGroup) {
         this.geolocationGroup = Optional.fromNullable(geolocationGroup);
         return this;
      }

      /**
       * @see DirectionalPoolRecordDetail#getSourceIpGroup()
       */
      public Builder sourceIpGroup(IdAndName sourceIpGroup) {
         this.sourceIpGroup = Optional.fromNullable(sourceIpGroup);
         return this;
      }

      /**
       * @see DirectionalPoolRecordDetail#getRecord()
       */
      public Builder record(DirectionalPoolRecord record) {
         this.record = record;
         return this;
      }

      /**
       * @see DirectionalPoolRecordDetail#getRecord()
       */
      public Builder record(DirectionalPoolRecord.Builder record) {
         this.record = record.build();
         return this;
      }

      public DirectionalPoolRecordDetail build() {
         return new DirectionalPoolRecordDetail(zoneName, name, id, group, geolocationGroup, sourceIpGroup, record);
      }

      public Builder from(DirectionalPoolRecordDetail in) {
         return this.zoneName(in.zoneName).name(in.name).id(in.id).group(in.group.orNull())
               .geolocationGroup(in.geolocationGroup.orNull()).sourceIpGroup(in.sourceIpGroup.orNull())
               .record(in.record);
      }
   }
}
