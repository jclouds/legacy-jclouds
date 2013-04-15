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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
public class DirectionalRecordDetail {

   private final String zoneName;
   private final String name;
   private final String id;
   private final Optional<IdAndName> group;
   private final Optional<IdAndName> geolocationGroup;
   private final Optional<IdAndName> sourceIpGroup;
   private final DirectionalRecord record;

   private DirectionalRecordDetail(String zoneName, String name, String id,
         Optional<IdAndName> group, Optional<IdAndName> geolocationGroup,
         Optional<IdAndName> sourceIpGroup, DirectionalRecord record) {
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

   public DirectionalRecord getRecord() {
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
      DirectionalRecordDetail that = DirectionalRecordDetail.class.cast(obj);
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

   public final static class Builder {
      private String zoneName;
      private String name;
      private String id;
      private Optional<IdAndName> group = Optional.absent();
      private Optional<IdAndName> geolocationGroup = Optional.absent();
      private Optional<IdAndName> sourceIpGroup = Optional.absent();
      private DirectionalRecord record;

      /**
       * @see DirectionalRecordDetail#getZoneName()
       */
      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

      /**
       * @see DirectionalRecordDetail#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see DirectionalRecordDetail#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see DirectionalRecordDetail#getGroup()
       */
      public Builder group(IdAndName group) {
         this.group = Optional.fromNullable(group);
         return this;
      }

      /**
       * @see DirectionalRecordDetail#getGeolocationGroup()
       */
      public Builder geolocationGroup(IdAndName geolocationGroup) {
         this.geolocationGroup = Optional.fromNullable(geolocationGroup);
         return this;
      }

      /**
       * @see DirectionalRecordDetail#getSourceIpGroup()
       */
      public Builder sourceIpGroup(IdAndName sourceIpGroup) {
         this.sourceIpGroup = Optional.fromNullable(sourceIpGroup);
         return this;
      }

      /**
       * @see DirectionalRecordDetail#getRecord()
       */
      public Builder record(DirectionalRecord record) {
         this.record = record;
         return this;
      }

      /**
       * @see DirectionalRecordDetail#getRecord()
       */
      public Builder record(DirectionalRecord.Builder record) {
         this.record = record.build();
         return this;
      }

      public DirectionalRecordDetail build() {
         return new DirectionalRecordDetail(zoneName, name, id, group, geolocationGroup, sourceIpGroup, record);
      }

      public Builder from(DirectionalRecordDetail in) {
         return this.zoneName(in.zoneName).name(in.name).id(in.id).group(in.group.orNull())
               .geolocationGroup(in.geolocationGroup.orNull()).sourceIpGroup(in.sourceIpGroup.orNull())
               .record(in.record);
      }
   }
}
