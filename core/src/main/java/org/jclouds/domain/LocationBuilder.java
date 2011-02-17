/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.domain.internal.LocationImpl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class LocationBuilder {
   protected LocationScope scope;
   protected String id;
   protected String description;
   protected Location parent;
   protected Set<String> iso3166Codes = ImmutableSet.of();
   protected Map<String, Object> metadata = ImmutableMap.of();

   public LocationBuilder scope(LocationScope scope) {
      this.scope = scope;
      return this;
   }

   public LocationBuilder id(String id) {
      this.id = id;
      return this;
   }

   public LocationBuilder description(String description) {
      this.description = description;
      return this;
   }

   public LocationBuilder parent(Location parent) {
      this.parent = parent;
      return this;
   }

   public LocationBuilder iso3166Codes(Iterable<String> iso3166Codes) {
      this.iso3166Codes = ImmutableSet.copyOf(checkNotNull(iso3166Codes, "iso3166Codes"));
      return this;
   }

   public LocationBuilder metadata(Map<String, Object> metadata) {
      this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
      return this;
   }

   public Location build() {
      return new LocationImpl(scope, id, description, parent, iso3166Codes, metadata);
   }
}