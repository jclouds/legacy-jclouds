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

import java.util.Date;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public class ResourceRecordMetadata {

   private final String zoneId;
   private final String guid;
   private final String zoneName;
   private final Date created;
   private final Date modified;
   private final ResourceRecord record;

   private ResourceRecordMetadata(String zoneId, String guid, String zoneName, Date created, Date modified,
         ResourceRecord record) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.guid = checkNotNull(guid, "guid");
      this.zoneName = checkNotNull(zoneName, "zoneName of %s/%s", zoneId, guid);
      this.created = checkNotNull(created, "created of %s/%s", zoneId, guid);
      this.modified = checkNotNull(modified, "modified of %s/%s", zoneId, guid);
      this.record = checkNotNull(record, "record of %s/%s", zoneId, guid);
   }

   public String getZoneId() {
      return zoneId;
   }

   public String getGuid() {
      return guid;
   }

   public String getZoneName() {
      return zoneName;
   }

   public Date getCreated() {
      return created;
   }

   public Date getModified() {
      return modified;
   }

   /**
    * the record in the zone file.
    */
   public ResourceRecord getRecord() {
      return record;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneId, guid);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ResourceRecordMetadata that = ResourceRecordMetadata.class.cast(obj);
      return equal(this.zoneId, that.zoneId) && equal(this.guid, that.guid);
   }

   @Override
   public String toString() {
      return toStringHelper("").omitNullValues().add("zoneId", zoneId).add("guid", guid).add("zoneName", zoneName)
            .add("created", created).add("modified", modified).add("record", record).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String zoneId;
      private String guid;
      private String zoneName;
      private Date created;
      private Date modified;
      private ResourceRecord record;

      /**
       * @see ResourceRecordMetadata#getZoneName()
       */
      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

      /**
       * @see ResourceRecordMetadata#getGuid()
       */
      public Builder guid(String guid) {
         this.guid = guid;
         return this;
      }

      /**
       * @see ResourceRecordMetadata#getZoneId()
       */
      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @see ResourceRecordMetadata#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      /**
       * @see ResourceRecordMetadata#getModified()
       */
      public Builder modified(Date modified) {
         this.modified = modified;
         return this;
      }

      /**
       * @see ResourceRecordMetadata#getRecord()
       */
      public Builder record(ResourceRecord record) {
         this.record = record;
         return this;
      }

      /**
       * @see ResourceRecordMetadata#getRecord()
       */
      public Builder record(ResourceRecord.Builder record) {
         this.record = record.build();
         return this;
      }

      public ResourceRecordMetadata build() {
         return new ResourceRecordMetadata(zoneId, guid, zoneName, created, modified, record);
      }

      public Builder from(ResourceRecordMetadata in) {
         return this.zoneName(in.zoneName).guid(in.guid).zoneId(in.zoneId).created(in.created).modified(in.modified)
               .record(in.record);
      }
   }
}
