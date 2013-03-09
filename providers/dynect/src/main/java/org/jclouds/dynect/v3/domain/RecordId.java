/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
public class RecordId {

   private final long id;
   private final String zone;
   private final String fqdn;
   private final String type;

   @ConstructorProperties({ "zone", "fqdn", "record_type", "record_id" })
   RecordId(String zone, String fqdn, String type, long id) {
      this.id = checkNotNull(id, "id");
      this.fqdn = checkNotNull(fqdn, "fqdn of %s", id);
      this.zone = checkNotNull(zone, "zone of %s", id);
      this.type = checkNotNull(type, "type of %s", id);
   }

   /**
    * Name of the zone
    */
   public String getZone() {
      return zone;
   }

   /**
    * Fully qualified domain name of a node in the zone
    */
   public String getFQDN() {
      return fqdn;
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
      return Objects.hashCode(zone, fqdn, type, id);
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
      return equal(this.zone, that.zone) && equal(this.fqdn, that.fqdn) && equal(this.type, that.type)
            && equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper(this).add("zone", zone).add("fqdn", fqdn).add("type", type).add("id", id);
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
       * @see RecordId#getZone()
       */
      public B zone(String zone) {
         this.zone = zone;
         return self();
      }

      /**
       * @see RecordId#getFQDN()
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
         return zone(in.zone).fqdn(in.fqdn).type(in.type).id(in.id);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      protected ConcreteBuilder self() {
         return this;
      }
   }
}