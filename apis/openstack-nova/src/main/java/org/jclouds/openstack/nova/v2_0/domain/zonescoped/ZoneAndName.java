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
package org.jclouds.openstack.nova.v2_0.domain.zonescoped;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Iterables;

/**
 * Helpful when looking for resources by zone and name
 * 
 * @author Adrian Cole
 */
public class ZoneAndName {
   
   public final static Function<ZoneAndName, String> NAME_FUNCTION = new Function<ZoneAndName, String>(){

      @Override
      public String apply(ZoneAndName input) {
         return input.getName();
      }
      
   };
   
   public final static Function<ZoneAndName, String> ZONE_FUNCTION = new Function<ZoneAndName, String>(){

      @Override
      public String apply(ZoneAndName input) {
         return input.getZone();
      }
      
   };
   
   public static ZoneAndName fromSlashEncoded(String name) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(name, "name"));
      checkArgument(Iterables.size(parts) == 2, "name must be in format zoneId/name");
      return new ZoneAndName(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static ZoneAndName fromZoneAndName(String zoneId, String name) {
      return new ZoneAndName(zoneId, name);
   }

   private static String slashEncodeZoneAndName(String zoneId, String name) {
      return checkNotNull(zoneId, "zoneId") + "/" + checkNotNull(name, "name");
   }

   public String slashEncode() {
      return slashEncodeZoneAndName(zoneId, name);
   }

   protected final String zoneId;
   protected final String name;

   protected ZoneAndName(String zoneId, String name) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.name = checkNotNull(name, "name");
   }

   public String getZone() {
      return zoneId;
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ZoneAndName that = ZoneAndName.class.cast(o);
      return equal(this.zoneId, that.zoneId) && equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneId, name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("zoneId", zoneId).add("name", name);
   }
}
