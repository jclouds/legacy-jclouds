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
public class ResourceRecord {

   private final String dName;
   private final int type;
   private final int ttl;
   private final List<String> infoValues;

   private ResourceRecord(String dName, int type, int ttl, List<String> infoValues) {
      this.dName = checkNotNull(dName, "dName");
      checkArgument(type >= 0, "type of %s must be >= 0", dName);
      this.type = type;
      checkArgument(ttl >= 0, "ttl of %s must be >= 0", dName);
      this.ttl = ttl;
      this.infoValues = checkNotNull(infoValues, "infoValues of %s", dName);
   }

   /**
    * the {@code dName} of the record.
    */
   public String getName() {
      return dName;
   }

   /**
    * the type value. ex {@code 1} for type {@code A}
    */
   public int getType() {
      return type;
   }

   public int getTTL() {
      return ttl;
   }

   /**
    * {@link #getType() type}-specific binary values.
    */
   public List<String> getRData() {
      return infoValues;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(dName, type, ttl, infoValues);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ResourceRecord that = ResourceRecord.class.cast(obj);
      return equal(this.dName, that.dName) && equal(this.type, that.type) && equal(this.ttl, that.ttl)
            && equal(this.infoValues, that.infoValues);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("dName", dName).add("type", type).add("ttl", ttl)
            .add("infoValues", infoValues).toString();
   }

   public static Builder rrBuilder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return rrBuilder().from(this);
   }

   public static final class Builder {
      private String dName;
      private int type = -1;
      private int ttl = -1;
      private ImmutableList.Builder<String> infoValues = ImmutableList.<String> builder();

      /**
       * @see ResourceRecord#getName()
       */
      public Builder name(String dName) {
         this.dName = dName;
         return this;
      }

      /**
       * @see ResourceRecord#getType()
       */
      public Builder type(int type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceRecord#getTTL()
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * adds to current values
       * 
       * @see ResourceRecord#getRData()
       */
      public Builder infoValue(Object infoValue) {
         this.infoValues.add(infoValue.toString());
         return this;
      }

      /**
       * replaces current values
       * 
       * @see ResourceRecord#getRData()
       */
      public Builder rdata(Object infoValue) {
         this.infoValues = ImmutableList.<String> builder().add(infoValue.toString());
         return this;
      }

      /**
       * replaces current values
       * 
       * @see ResourceRecord#getRData()
       */
      public Builder rdata(Iterable<?> infoValues) {
         this.infoValues = ImmutableList.<String> builder().addAll(transform(infoValues, toStringFunction()));
         return this;
      }

      public ResourceRecord build() {
         return new ResourceRecord(dName, type, ttl, infoValues.build());
      }

      public Builder from(ResourceRecord in) {
         return name(in.getName()).type(in.getType()).ttl(in.getTTL()).rdata(in.getRData());
      }
   }
}
