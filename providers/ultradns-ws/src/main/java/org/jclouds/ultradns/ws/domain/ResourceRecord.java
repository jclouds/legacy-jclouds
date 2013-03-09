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

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import org.jclouds.ultradns.ws.ResourceTypeToValue;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.UnsignedInteger;

/**
 * @author Adrian Cole
 */
public class ResourceRecord {

   private final String dName;
   private final UnsignedInteger type;
   private final UnsignedInteger ttl;
   private final List<String> infoValues;

   private ResourceRecord(String dName, UnsignedInteger type, UnsignedInteger ttl, List<String> infoValues) {
      this.dName = checkNotNull(dName, "dName");
      this.type = checkNotNull(type, "type of %s", dName);
      this.ttl = checkNotNull(ttl, "ttl of %s", dName);
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
   public UnsignedInteger getType() {
      return type;
   }

   public UnsignedInteger getTTL() {
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
      return toStringHelper("").omitNullValues().add("dName", dName).add("type", type).add("ttl", ttl)
            .add("infoValues", infoValues).toString();
   }

   public static Builder rrBuilder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return rrBuilder().from(this);
   }

   public final static class Builder {
      private String dName;
      private UnsignedInteger type;
      private UnsignedInteger ttl;
      private ImmutableList.Builder<String> infoValues = ImmutableList.<String> builder();

      /**
       * @see ResourceRecord#getName()
       */
      public Builder name(String dName) {
         this.dName = dName;
         return this;
      }

      /**
       * use this for common type values available in
       * {@link ResourceTypeToValue}, such as {@code MX} or {@code PTR}
       * 
       * @see ResourceRecord#getType()
       * @throws IllegalArgumentException
       *            if the type value is not present in
       *            {@link ResourceTypeToValue},
       */
      public Builder type(String type) throws IllegalArgumentException {
         this.type = ResourceTypeToValue.lookup(type);
         return this;
      }

      /**
       * @see ResourceRecord#getType()
       */
      public Builder type(UnsignedInteger type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceRecord#getType()
       */
      public Builder type(int type) {
         return type(UnsignedInteger.fromIntBits(type));
      }

      /**
       * @see ResourceRecord#getTTL()
       */
      public Builder ttl(UnsignedInteger ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * @see ResourceRecord#getTTL()
       */
      public Builder ttl(int ttl) {
         return ttl(UnsignedInteger.fromIntBits(ttl));
      }

      /**
       * @see ResourceRecord#getRData()
       */
      public Builder rdata(Object infoValue) {
         this.infoValues.add(infoValue.toString());
         return this;
      }

      /**
       * @see ResourceRecord#getRData()
       */
      public Builder rdata(Iterable<?> infoValues) {
         this.infoValues.addAll(transform(infoValues, toStringFunction()));
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
