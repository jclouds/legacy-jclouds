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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;

public class Node {

   public static Node create(String zone, String fqdn) {
      return new Node(zone, fqdn);
   }

   private final String zone;
   private final String fqdn;

   @ConstructorProperties({ "zone", "fqdn" })
   protected Node(String zone, String fqdn) {
      this.fqdn = checkNotNull(fqdn, "fqdn");
      this.zone = checkNotNull(zone, "zone for %s", fqdn);
   }

   /**
    * Name of the zone
    */
   public String getZone() {
      return zone;
   }

   /**
    * Fully qualified domain name of a node in the zone
    */
   public String getFQDN() {
      return fqdn;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zone, fqdn);
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
      return Objects.equal(this.zone, that.zone) && Objects.equal(this.fqdn, that.fqdn);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("zone", zone).add("fqdn", fqdn).toString();
   }
}
