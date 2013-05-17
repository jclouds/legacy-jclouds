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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Everett Toews
 */
public class RecordDetail {
   private final String id;
   private final Date created;
   private final Date updated;
   private final Record record;

   private RecordDetail(String id, Date created, Date updated, Record record) {
      this.id = checkNotNull(id, "id required");
      this.created = checkNotNull(created, "created required");
      this.updated = checkNotNull(updated, "updated required");
      this.record = checkNotNull(record, "record required");
   }

   public String getId() {
      return id;
   }

   /**
    * When this record was created.
    */
   public Date getCreated() {
      return created;
   }

   /**
    * When this record was updated.
    */
   public Date getUpdated() {
      return updated;
   }

   /**
    * The Record.
    */
   public Record getRecord() {
      return record;
   }

   /**
    * @see Record.Builder#name(String)
    */
   public String getName() {
      return record.getName();
   }

   /**
    * @see Record.Builder#type(String)
    */
   public String getType() {
      return record.getType();
   }

   /**
    * @see Record.Builder#ttl(Integer)
    */
   public int getTTL() {
      return record.getTTL().get();
   }

   /**
    * @see Record.Builder#data(String)
    */
   public String getData() {
      return record.getData();
   }

   /**
    * @see Record.Builder#priority(Integer)
    */
   public Integer getPriority() {
      return record.getPriority();
   }

   /**
    * @see Record.Builder#comment(String)
    */
   public String getComment() {
      return record.getComment();
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
      RecordDetail that = RecordDetail.class.cast(obj);

      return equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return toStringHelper(this).omitNullValues().add("id", id).add("created", created).add("updated", updated)
            .add("record", record);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public final static class Builder {
      private String id;
      private Date created;
      private Date updated;
      private Record record;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      public Builder record(Record record) {
         this.record = record;
         return this;
      }

      public Builder record(Record.Builder recordBuilder) {
         this.record = recordBuilder.build();
         return this;
      }

      public RecordDetail build() {
         return new RecordDetail(id, created, updated, record);
      }

      public Builder from(RecordDetail in) {
         return this.id(in.getId()).created(in.getCreated()).updated(in.getUpdated()).record(in.getRecord());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }
}
