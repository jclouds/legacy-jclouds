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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class CreateRecord<D extends Map<String, Object>> {

   private final String fqdn;
   private final String type;
   private final int ttl;
   private final D rdata;

   private CreateRecord(String fqdn, String type, int ttl, D rdata) {
      this.fqdn = checkNotNull(fqdn, "fqdn");
      this.type = checkNotNull(type, "type of %s", fqdn);
      checkArgument(ttl >= 0, "ttl of %s must be unsigned", fqdn);
      this.ttl = ttl;
      this.rdata = checkNotNull(rdata, "rdata of %s", fqdn);
   }

   /**
    * @see RecordId#getFQDN()
    */
   public String getFQDN() {
      return fqdn;
   }

   /**
    * @see RecordId#getType()
    */
   public String getType() {
      return type;
   }

   /**
    * zero for zone default
    * 
    * @see Record#getTTL()
    */
   public int getTTL() {
      return ttl;
   }

   /**
    * @see Record#getRData()
    */
   public D getRData() {
      return rdata;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || !obj.getClass().equals(CreateRecord.class))
         return false;
      CreateRecord<?> that = CreateRecord.class.cast(obj);
      return equal(this.fqdn, that.fqdn) && equal(this.type, that.type) && equal(this.ttl, that.ttl)
            && equal(this.rdata, that.rdata);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("fqdn", fqdn).add("type", type).add("ttl", ttl).add("rdata", rdata).toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(fqdn, type, ttl, rdata);
   }

   public static <D extends Map<String, Object>> Builder<D> builder() {
      return new Builder<D>();
   }

   public Builder<D> toBuilder() {
      return new Builder<D>().from(this);
   }

   public static class Builder<D extends Map<String, Object>> {
      protected String fqdn;
      protected String type;
      // default of zone is implied when ttl is set to zero
      protected int ttl = 0;
      protected D rdata;

      /**
       * @see CreateRecord#getFQDN()
       */
      public Builder<D> fqdn(String fqdn) {
         this.fqdn = fqdn;
         return this;
      }

      /**
       * @see CreateRecord#getType()
       */
      public Builder<D> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see CreateRecord#getTTL()
       */
      public Builder<D> ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * @see CreateRecord#getRData()
       */
      public Builder<D> rdata(D rdata) {
         this.rdata = rdata;
         return this;
      }

      public CreateRecord<D> build() {
         return new CreateRecord<D>(fqdn, type, ttl, rdata);
      }

      public <Y extends D> Builder<D> from(CreateRecord<Y> in) {
         return fqdn(in.fqdn).type(in.type).ttl(in.ttl).rdata(in.rdata);
      }

      public <Y extends D> Builder<D> from(Record<Y> in) {
         return fqdn(in.getFQDN()).type(in.getType()).ttl(in.getTTL()).rdata(in.getRData());
      }
   }
}
