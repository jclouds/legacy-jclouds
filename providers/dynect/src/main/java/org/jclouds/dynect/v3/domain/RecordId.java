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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Adrian Cole
 */
public class RecordId extends Node {

   private final long id;
   private final String type;

   @ConstructorProperties({"zone", "fqdn", "record_type", "record_id" })
   RecordId(String zone, String fqdn, String type, long id) {
      super(zone, fqdn);
      this.id = checkNotNull(id, "id");
      this.type = checkNotNull(type, "type of %s", id);
   }

   /**
    * The RRType of the record
    */
   public String getType() {
      return type;
   }

   /**
    * The record id
    */
   public long getId() {
      return id;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getZone(), getFQDN(), type, id);
   }

   /**
    * permits equals comparisons with subtypes
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || !(obj instanceof RecordId))
         return false;
      RecordId that = RecordId.class.cast(obj);
      return super.equals(obj) && equal(this.type, that.type) && equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper(this).add("zone", getZone()).add("fqdn", getFQDN()).add("type", type).add("id", id);
   }

   public static Builder<?> recordIdBuilder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().from(this);
   }

   public abstract static class Builder<B extends Builder<B>>  {
      protected abstract B self();

      protected String zone;
      protected String fqdn;
      protected String type;
      protected long id;

      /**
       * @see Node#getZone()
       */
      public B zone(String zone) {
         this.zone = zone;
         return self();
      }

      /**
       * @see Node#getFQDN()
       */
      public B fqdn(String fqdn) {
         this.fqdn = fqdn;
         return self();
      }

      /**
       * @see RecordId#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see RecordId#getId()
       */
      public B id(long id) {
         this.id = id;
         return self();
      }

      public RecordId build() {
         return new RecordId(zone, fqdn, type, id);
      }

      public B from(RecordId in) {
         return zone(in.getZone()).fqdn(in.getFQDN()).type(in.type).id(in.id);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
