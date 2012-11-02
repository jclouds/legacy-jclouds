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
package org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Iterables;

/**
 * Helpful when looking for resources by datacenter and name
 * 
 * @author Adrian Cole
 */
public class DatacenterAndName {
   
   public static final Function<DatacenterAndName, String> NAME_FUNCTION = new Function<DatacenterAndName, String>(){

      @Override
      public String apply(DatacenterAndName input) {
         return input.getName();
      }
      
   };
   
   public static final Function<DatacenterAndName, String> DATACENTER_FUNCTION = new Function<DatacenterAndName, String>(){

      @Override
      public String apply(DatacenterAndName input) {
         return input.getDatacenter();
      }
      
   };
   
   public static DatacenterAndName fromSlashEncoded(String name) {
      Iterable<String> parts = Splitter.on('/').split(checkNotNull(name, "name"));
      checkArgument(Iterables.size(parts) == 2, "name must be in format datacenterId/name");
      return new DatacenterAndName(Iterables.get(parts, 0), Iterables.get(parts, 1));
   }

   public static DatacenterAndName fromDatacenterAndName(String datacenterId, String name) {
      return new DatacenterAndName(datacenterId, name);
   }

   private static String slashEncodeDatacenterAndName(String datacenterId, String name) {
      return checkNotNull(datacenterId, "datacenterId") + "/" + checkNotNull(name, "name");
   }

   public String slashEncode() {
      return slashEncodeDatacenterAndName(datacenterId, name);
   }

   protected final String datacenterId;
   protected final String name;

   protected DatacenterAndName(String datacenterId, String name) {
      this.datacenterId = checkNotNull(datacenterId, "datacenterId");
      this.name = checkNotNull(name, "name");
   }

   public String getDatacenter() {
      return datacenterId;
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
      DatacenterAndName that = DatacenterAndName.class.cast(o);
      return equal(this.datacenterId, that.datacenterId) && equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(datacenterId, name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("datacenterId", datacenterId).add("name", name);
   }
}
