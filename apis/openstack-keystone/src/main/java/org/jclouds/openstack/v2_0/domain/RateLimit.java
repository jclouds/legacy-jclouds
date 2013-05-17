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

import javax.inject.Named;

import com.google.common.base.Objects;

/**
 * @author Everett Toews
 */
public final class RateLimit {

   private final String uri;
   private final String regex;
   @Named("limit")
   private final Iterable<Limit> limits;

   @ConstructorProperties({ "uri", "regex", "limit" })
   private RateLimit(String uri, String regex, Iterable<Limit> limits) {
      this.uri = checkNotNull(uri, "uri");
      this.regex = checkNotNull(regex, "regex");
      this.limits = checkNotNull(limits, "limit");
   }

   public String getUri() {
      return uri;
   }

   public String getRegex() {
      return regex;
   }

   public Iterable<Limit> getLimits() {
      return limits;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(uri);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      RateLimit that = RateLimit.class.cast(obj);
      return equal(this.uri, that.uri);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("uri", uri).add("regex", regex).add("limits", limits).toString();
   }
}
