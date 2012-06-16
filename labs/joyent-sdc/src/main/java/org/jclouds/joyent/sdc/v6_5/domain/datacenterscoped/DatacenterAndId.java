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
package org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class DatacenterAndId {
   public static DatacenterAndId fromSlashEncoded(String id) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(id, "id"));
      checkArgument(Iterables.size(parts) == 2, "id must be in format datacenterId/id");
      return new DatacenterAndId(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static DatacenterAndId fromDatacenterAndId(String datacenterId, String id) {
      return new DatacenterAndId(datacenterId, id);
   }

   private static String slashEncodeDatacenterAndId(String datacenterId, String id) {
      return checkNotNull(datacenterId, "datacenterId") + "/" + checkNotNull(id, "id");
   }

   public String slashEncode() {
      return slashEncodeDatacenterAndId(datacenterId, id);
   }

   protected final String datacenterId;
   protected final String id;

   protected DatacenterAndId(String datacenterId, String id) {
      this.datacenterId = checkNotNull(datacenterId, "datacenterId");
      this.id = checkNotNull(id, "id");
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(datacenterId, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DatacenterAndId other = (DatacenterAndId) obj;
      return Objects.equal(datacenterId, other.datacenterId) && Objects.equal(id, other.id);
   }

   public String getDatacenter() {
      return datacenterId;
   }

   public String getId() {
      return id;
   }

   @Override
   public String toString() {
      return "[datacenterId=" + datacenterId + ", id=" + id + "]";
   }

}
