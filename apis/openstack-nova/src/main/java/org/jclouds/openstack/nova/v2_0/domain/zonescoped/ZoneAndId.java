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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author Adam Lowe
 */
public class ZoneAndId {
   public static ZoneAndId fromSlashEncoded(String id) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(id, "id"));
      checkArgument(Iterables.size(parts) == 2, "id must be in format zoneId/id");
      return new ZoneAndId(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static ZoneAndId fromZoneAndId(String zoneId, String id) {
      return new ZoneAndId(zoneId, id);
   }

   private static String slashEncodeZoneAndId(String zoneId, String id) {
      return checkNotNull(zoneId, "zoneId") + "/" + checkNotNull(id, "id");
   }

   public String slashEncode() {
      return slashEncodeZoneAndId(zoneId, id);
   }

   protected final String zoneId;
   protected final String id;

   protected ZoneAndId(String zoneId, String id) {
      this.zoneId = checkNotNull(zoneId, "zoneId");
      this.id = checkNotNull(id, "id");
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneId, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ZoneAndId other = (ZoneAndId) obj;
      return Objects.equal(zoneId, other.zoneId) && Objects.equal(id, other.id);
   }

   public String getZone() {
      return zoneId;
   }

   public String getId() {
      return id;
   }

   @Override
   public String toString() {
      return "[zoneId=" + zoneId + ", id=" + id + "]";
   }

}
