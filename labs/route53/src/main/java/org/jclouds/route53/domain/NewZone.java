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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public final class NewZone {

   private final ZoneAndNameServers zone;
   private final Change change;

   private NewZone(ZoneAndNameServers zone, Change change) {
      this.zone = checkNotNull(zone, "zone");
      this.change = checkNotNull(change, "change of %s", zone);
   }

   /**
    * @see ZoneAndNameServers#getZone()
    */
   public Zone getZone() {
      return zone.getZone();
   }

   /**
    * @see ZoneAndNameServers#getNameServers()
    */
   public ImmutableList<String> getNameServers() {
      return zone.getNameServers();
   }

   /**
    * the zone creation event
    */
   public Change getChange() {
      return change;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zone);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      NewZone that = NewZone.class.cast(obj);
      return equal(this.zone, that.zone);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("zone", zone.getZone()).add("nameServers", zone.getNameServers())
            .add("change", change).toString();
   }

   public static NewZone create(ZoneAndNameServers zone, Change change) {
      return new NewZone(zone, change);
   }
}
