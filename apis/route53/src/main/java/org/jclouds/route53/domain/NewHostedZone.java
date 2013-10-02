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
public final class NewHostedZone {

   private final HostedZoneAndNameServers zone;
   private final Change change;

   private NewHostedZone(HostedZoneAndNameServers zone, Change change) {
      this.zone = checkNotNull(zone, "zone");
      this.change = checkNotNull(change, "change of %s", zone);
   }

   /**
    * @see HostedZoneAndNameServers#getZone()
    */
   public HostedZone getZone() {
      return zone.getZone();
   }

   /**
    * @see HostedZoneAndNameServers#getNameServers()
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
      NewHostedZone that = NewHostedZone.class.cast(obj);
      return equal(this.zone, that.zone);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("zone", zone.getZone()).add("nameServers", zone.getNameServers())
            .add("change", change).toString();
   }

   public static NewHostedZone create(HostedZoneAndNameServers zone, Change change) {
      return new NewHostedZone(zone, change);
   }
}
