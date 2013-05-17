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

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
public class DirectionalPoolRecord {
   private final String type;
   private final int ttl;
   private final boolean noResponseRecord;
   private final List<String> infoValues;

   private DirectionalPoolRecord(String type, int ttl, boolean noResponseRecord, List<String> infoValues) {
      this.type = checkNotNull(type, "type");
      checkArgument(ttl >= 0, "ttl must be >= 0");
      this.ttl = ttl;
      this.noResponseRecord = noResponseRecord;
      this.infoValues = checkNotNull(infoValues, "infoValues");
   }

   /**
    * the type. ex. {@code A}
    */
   public String getType() {
      return type;
   }

   public int getTTL() {
      return ttl;
   }

   /**
    * true if blocks traffic from specified regions by returning No Error, No
    * Response.
    */
   public boolean isNoResponseRecord() {
      return noResponseRecord;
   }

   /**
    * {@link #getType() type}-specific binary values.
    */
   public List<String> getRData() {
      return infoValues;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(noResponseRecord, type, ttl, infoValues);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DirectionalPoolRecord that = DirectionalPoolRecord.class.cast(obj);
      return equal(this.type, that.type) && equal(this.ttl, that.ttl)
            && equal(this.noResponseRecord, that.noResponseRecord) && equal(this.infoValues, that.infoValues);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("type", type).add("ttl", ttl).add("noResponseRecord", noResponseRecord)
            .add("infoValues", infoValues).toString();
   }

   public static Builder drBuilder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return drBuilder().from(this);
   }

   public static final class Builder {
      private String type;
      private int ttl = -1;
      private boolean noResponseRecord;
      private ImmutableList.Builder<String> infoValues = ImmutableList.<String> builder();

      /**
       * @see DirectionalPoolRecord#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see DirectionalPoolRecord#getTTL()
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * @see DirectionalPoolRecord#isNoResponseRecord()
       */
      public Builder noResponseRecord(boolean noResponseRecord) {
         this.noResponseRecord = noResponseRecord;
         return this;
      }

      /**
       * adds to current values
       * 
       * @see DirectionalPoolRecord#getRData()
       */
      public Builder infoValue(Object infoValue) {
         this.infoValues.add(infoValue.toString());
         return this;
      }

      /**
       * replaces current values
       * 
       * @see DirectionalPoolRecord#getRData()
       */
      public Builder rdata(Object infoValue) {
         this.infoValues = ImmutableList.<String> builder().add(infoValue.toString());
         return this;
      }

      /**
       * replaces current values
       * 
       * @see DirectionalPoolRecord#getRData()
       */
      public Builder rdata(Iterable<?> infoValues) {
         this.infoValues = ImmutableList.<String> builder().addAll(transform(infoValues, toStringFunction()));
         return this;
      }

      public DirectionalPoolRecord build() {
         return new DirectionalPoolRecord(type, ttl, noResponseRecord, infoValues.build());
      }

      public Builder from(DirectionalPoolRecord in) {
         return type(in.type).ttl(in.ttl).noResponseRecord(in.noResponseRecord).rdata(in.infoValues);
      }
   }
}
