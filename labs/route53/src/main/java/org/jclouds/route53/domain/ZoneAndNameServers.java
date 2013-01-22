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
package org.jclouds.route53.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public final class ZoneAndNameServers {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private Zone zone;
      private ImmutableSet.Builder<String> nameServers = ImmutableSet.<String> builder();

      /**
       * @see ZoneAndNameServers#getZone()
       */
      public Builder zone(Zone zone) {
         this.zone = zone;
         return this;
      }

      /**
       * @see ZoneAndNameServers#getNameServers()
       */
      public Builder nameServers(Iterable<String> nameServers) {
         this.nameServers = ImmutableSet.<String> builder().addAll(checkNotNull(nameServers, "nameServers"));
         return this;
      }

      /**
       * @see ZoneAndNameServers#getNameServers()
       */
      public Builder addNameServer(String nameServer) {
         this.nameServers.add(checkNotNull(nameServer, "nameServer"));
         return this;
      }

      public ZoneAndNameServers build() {
         return new ZoneAndNameServers(zone, nameServers.build());
      }

      public Builder from(ZoneAndNameServers in) {
         return this.zone(in.zone).nameServers(in.nameServers);
      }
   }

   private final Zone zone;
   private final ImmutableSet<String> nameServers;

   private ZoneAndNameServers(Zone zone, ImmutableSet<String> nameServers) {
      this.zone = checkNotNull(zone, "zone");
      this.nameServers = checkNotNull(nameServers, "nameServers for %s", zone);
   }

   /**
    * the hosted zone
    */
   public Zone getZone() {
      return zone;
   }

   /**
    * the authoritative name servers for the hosted zone
    */
   public Set<String> getNameServers() {
      return nameServers;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zone, nameServers);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ZoneAndNameServers that = (ZoneAndNameServers) obj;
      return Objects.equal(this.zone, that.zone) && Objects.equal(this.nameServers, that.nameServers);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("zone", zone).add("nameServers", nameServers).toString();
   }
}
