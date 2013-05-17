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
package org.jclouds.domain.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ComparisonChain.start;
import static com.google.common.collect.Ordering.natural;

import java.net.URI;
import java.util.Map;

import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Maps;

/**
 * Idpayload of the object
 * 
 * @author Adrian Cole
 */
public abstract class ResourceMetadataImpl<T extends Enum<T>> implements ResourceMetadata<T> {

   @Nullable
   private final String providerId;
   @Nullable
   private final String name;
   @Nullable
   private final Location location;
   @Nullable
   private final URI uri;
   private final Map<String, String> userMetadata = Maps.newLinkedHashMap();

   public ResourceMetadataImpl(@Nullable String providerId, @Nullable String name, @Nullable Location location,
         @Nullable URI uri, Map<String, String> userMetadata) {
      this.providerId = providerId;
      this.name = name;
      this.location = location;
      this.uri = uri;
      this.userMetadata.putAll(checkNotNull(userMetadata, "userMetadata"));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(ResourceMetadata<T> that) {
      return start()
            .compare(this.getName(), that.getName(), natural().nullsLast())
            .result();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getProviderId() {
      return providerId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Location getLocation() {
      return location;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getUri() {
      return uri;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, String> getUserMetadata() {
      return userMetadata;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceMetadataImpl<?> that = ResourceMetadataImpl.class.cast(o);
      return equal(this.getType(), that.getType()) && equal(this.providerId, that.providerId)
               && equal(this.name, that.name) && equal(this.location, that.location) && equal(this.uri, that.uri);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getType(), providerId, name, location, uri);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("type", getType()).add("providerId", providerId)
               .add("name", name).add("location", location).add("uri", uri).add("userMetadata", userMetadata);
   }

}
