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

import java.util.Map;
import java.util.Set;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class LocationImpl implements Location {

   private final LocationScope scope;
   private final String id;
   private final String description;
   private final Location parent;
   private final Set<String> iso3166Codes;
   private final Map<String, Object> metadata;

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      LocationImpl that = LocationImpl.class.cast(o);
      return equal(this.scope, that.scope) && equal(this.id, that.id) && equal(this.parent, that.parent);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(scope, id, parent);
   }

   @Override
   public String toString() {
      ToStringHelper helper = Objects.toStringHelper("").omitNullValues().add("scope", scope).add("id", id)
            .add("description", description);
      if (parent != null)
         helper.add("parent", parent.getId());
      if (iso3166Codes.size() > 0)
         helper.add("iso3166Codes", iso3166Codes);
      if (metadata.size() > 0)
         helper.add("metadata", metadata);
      return helper.toString();
   }

   public LocationImpl(LocationScope scope, String id, String description, @Nullable Location parent,
            Iterable<String> iso3166Codes, Map<String, Object> metadata) {
      this.scope = checkNotNull(scope, "scope");
      this.id = checkNotNull(id, "id");
      this.description = checkNotNull(description, "description");
      this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
      this.iso3166Codes = ImmutableSet.copyOf(checkNotNull(iso3166Codes, "iso3166Codes"));
      this.parent = parent;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LocationScope getScope() {
      return scope;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Location getParent() {
      return parent;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return iso3166Codes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, Object> getMetadata() {
      return metadata;
   }

}
