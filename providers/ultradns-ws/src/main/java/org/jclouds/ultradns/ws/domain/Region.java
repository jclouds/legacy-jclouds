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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

/**
 * A region is a set of territory names.
 * 
 * @author Adrian Cole
 */
public class Region extends ForwardingSet<String> {

   private final String name;
   private final Set<String> territoryNames;

   private Region(String name, Set<String> territoryNames) {
      this.name = checkNotNull(name, "name");
      this.territoryNames = checkNotNull(territoryNames, "territoryNames of %s", name);
   }

   public String getName() {
      return name;
   }

   public Set<String> getTerritoryNames() {
      return territoryNames;
   }

   @Override
   protected Set<String> delegate() {
      return territoryNames;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, territoryNames);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Region that = Region.class.cast(obj);
      return equal(this.name, that.name) && equal(this.territoryNames, that.territoryNames);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("name", name).add("territoryNames", territoryNames).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String name;
      private ImmutableSet.Builder<String> territoryNames = ImmutableSet.<String> builder();

      /**
       * @see Region#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * adds to current territoryNames
       * 
       * @see Region#getTerritoryNames()
       */
      public Builder addTerritoryName(String territoryName) {
         this.territoryNames.add(territoryName);
         return this;
      }

      /**
       * replaces current territoryNames
       * 
       * @see Region#getTerritoryNames()
       */
      public Builder territoryNames(Iterable<String> territoryNames) {
         this.territoryNames = ImmutableSet.<String> builder().addAll(territoryNames);
         return this;
      }

      public Region build() {
         return new Region(name, territoryNames.build());
      }

      public Builder from(Region in) {
         return name(in.getName()).territoryNames(in.getTerritoryNames());
      }
   }
}
