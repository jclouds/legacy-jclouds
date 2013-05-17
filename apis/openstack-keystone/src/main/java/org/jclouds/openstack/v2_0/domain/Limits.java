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
import java.util.Map;

import javax.inject.Named;

import com.google.common.base.Objects;

/**
 * @author Everett Toews
 */
public final class Limits {

   @Named("rate")
   private final Iterable<RateLimit> rateLimits;
   @Named("absolute")
   private final Map<String, Integer> absoluteLimits;

   @ConstructorProperties({ "rate", "absolute" })
   private Limits(Iterable<RateLimit> rateLimits, Map<String, Integer> absoluteLimits) {
      this.rateLimits = checkNotNull(rateLimits, "rateLimits");
      this.absoluteLimits = checkNotNull(absoluteLimits, "absoluteLimits");
   }

   public Iterable<RateLimit> getRateLimits() {
      return rateLimits;
   }

   public Map<String, Integer> getAbsoluteLimits() {
      return absoluteLimits;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(rateLimits, absoluteLimits);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Limits that = Limits.class.cast(obj);
      return equal(this.rateLimits, that.rateLimits) && equal(this.absoluteLimits, that.absoluteLimits);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("rateLimits", rateLimits).add("absoluteLimits", absoluteLimits).toString();
   }
}
