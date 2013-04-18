/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

public class Node {

   public static Node create(String fqdn, String zone) {
      return new Node(fqdn, zone);
   }

   private final String fqdn;
   private final String zone;

   @ConstructorProperties({ "fqdn", "zone" })
   protected Node(String fqdn, String zone) {
      this.fqdn = checkNotNull(fqdn, "fqdn");
      this.zone = checkNotNull(zone, "zone for %s", fqdn);
   }

   /**
    * Fully qualified domain name of a node in the zone
    */
   public String getFQDN() {
      return fqdn;
   }

   /**
    * Name of the zone
    */
   public String getZone() {
      return zone;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(fqdn, zone);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Node that = Node.class.cast(obj);
      return Objects.equal(this.fqdn, that.fqdn) && Objects.equal(this.zone, that.zone);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("fqdn", fqdn).add("zone", zone).toString();
   }
}
