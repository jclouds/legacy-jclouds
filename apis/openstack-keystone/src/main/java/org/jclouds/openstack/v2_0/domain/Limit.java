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
package org.jclouds.openstack.v2_0.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
public final class Limit {

   private final String verb;
   private final String unit;
   private final int value;
   private final Optional<Integer> remaining;
   @Named("next-available")
   private final Optional<Date> nextAvailable;

   @ConstructorProperties({ "verb", "unit", "value", "remaining", "next-available" })
   private Limit(String verb, String unit, int value, @Nullable Integer remaining, @Nullable Date nextAvailable) {
      this.verb = checkNotNull(verb, "verb");
      this.unit = checkNotNull(unit, "unit", unit);
      this.value = value;
      this.remaining = Optional.fromNullable(remaining);
      this.nextAvailable = Optional.fromNullable(nextAvailable);
   }

   public String getVerb() {
      return verb;
   }

   public String getUnit() {
      return unit;
   }

   public int getValue() {
      return value;
   }

   public Optional<Integer> getRemaining() {
      return remaining;
   }

   public Optional<Date> getNextAvailable() {
      return nextAvailable;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(verb, unit);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Limit that = Limit.class.cast(obj);
      return equal(this.verb, that.verb) && equal(this.unit, that.unit);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("verb", verb).add("unit", unit).add("value", value)
            .add("remaining", remaining.orNull()).add("nextAvailable", nextAvailable.orNull()).toString();
   }
}
