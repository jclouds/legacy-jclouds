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
package org.jclouds.openstack.nova.v1_1.compute.domain;

import com.google.common.base.Objects;

/**
 * @author Adam Lowe
 */
public class RegionAndName {
   protected final String region;
   protected final String name;

   public RegionAndName(String region, String name) {
      this.region = region;
      this.name = name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(region, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RegionAndName other = (RegionAndName) obj;
      return Objects.equal(region, other.region) && Objects.equal(name, other.name);
   }

   public String getRegion() {
      return region;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return "[region=" + region + ", name=" + name + "]";
   }

}
