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

import java.util.List;
import java.util.Map;

import org.jclouds.dynect.v3.domain.RecordSet.Value;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

/**
 * A set of records which shared the same name, type, and ttl
 * 
 * @author Adrian Cole
 */
public class RecordSet extends ForwardingList<Value> {

   private final String type;
   private final int ttl;
   private transient final List<Value> values;

   private RecordSet(String type, int ttl, List<Value> values) {
      this.type = checkNotNull(type, "type");
      this.ttl = ttl;
      checkArgument(ttl >= 0, "ttl must be >=0");
      this.values = checkNotNull(values, "values");
   }

   /**
    * @see Record#getType()
    */
   public String getType() {
      return type;
   }

   /**
    * @see Record#getTTL()
    */
   public int getTTL() {
      return ttl;
   }

   public static class Value {
      private final Optional<String> label;
      private final Optional<Integer> weight;
      private final Map<String, Object> rdata;

      private Value(Optional<String> label, Optional<Integer> weight, Map<String, Object> rdata) {
         this.label = checkNotNull(label, "label");
         this.weight = checkNotNull(weight, "weight");
         this.rdata = checkNotNull(rdata, "rdata");
      }

      /**
       * The label of the value.
       */
      public Optional<String> getLabel() {
         return label;
      }

      /**
       * The relative weight of the value.
       */
      public Optional<Integer> getWeight() {
         return weight;
      }

      /**
       * @see Record#getRData()
       */
      public Map<String, Object> getRData() {
         return rdata;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(label, weight, rdata);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         Value that = Value.class.cast(obj);
         return equal(this.label, that.label) && equal(this.weight, that.weight) && equal(this.rdata, that.rdata);
      }

      @Override
      public String toString() {
         return toStringHelper("").omitNullValues().add("label", label.orNull()).add("weight", weight.orNull())
               .add("rdata", rdata).toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return new Builder().from(this);
      }

      public final static class Builder {
         private Optional<String> label = Optional.absent();
         private Optional<Integer> weight = Optional.absent();
         private Map<String, Object> rdata;

         /**
          * @see Value#getLabel()
          */
         public Builder label(String label) {
            this.label = Optional.fromNullable(label);
            return this;
         }

         /**
          * @see Value#getWeight()
          */
         public Builder weight(Integer weight) {
            this.weight = Optional.fromNullable(weight);
            return this;
         }

         /**
          * @see Record#getRData()
          */
         public Builder rdata(Map<String, Object> rdata) {
            this.rdata = rdata;
            return this;
         }

         public Value build() {
            return new Value(label, weight, rdata);
         }

         public Builder from(Value in) {
            return label(in.label.orNull()).weight(in.weight.orNull()).rdata(in.rdata);
         }
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type, ttl, values);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      RecordSet that = RecordSet.class.cast(obj);
      return equal(this.type, that.type) && equal(this.ttl, that.ttl) && equal(this.values, that.values);
   }

   @Override
   public String toString() {
      return toStringHelper("").omitNullValues().add("type", type).add("ttl", ttl).add("values", values).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }

   public final static class Builder {
      private String type;
      private int ttl = -1;
      private ImmutableList.Builder<Value> values = ImmutableList.builder();

      /**
       * @see RecordSet#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see RecordSet#getTTL()
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * @see RecordSet#iterator()
       */
      public Builder add(Value value) {
         this.values.add(value);
         return this;
      }

      /**
       * replaces current values
       * 
       * @see RecordSet#iterator()
       */
      public Builder values(Iterable<Value> values) {
         this.values = ImmutableList.<Value> builder().addAll(values);
         return this;
      }

      /**
       * @see RecordSet#iterator()
       */
      public Builder addAll(Iterable<Value> values) {
         this.values.addAll(values);
         return this;
      }

      public RecordSet build() {
         return new RecordSet(type, ttl, values.build());
      }

      public Builder from(RecordSet in) {
         return type(in.type).ttl(in.ttl).values(in.values);
      }
   }

   @Override
   protected List<Value> delegate() {
      return values;
   }
}
