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
package org.jclouds.ec2.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * 
 * @author Adrian Cole
 */
public class RegionAndName {

   protected final String region;
   protected final String name;

   public String slashEncode() {
      return new StringBuilder(region).append('/').append(name).toString();
   }

   public RegionAndName(String region, String name) {
      this.region = checkNotNull(region, "region");
      this.name = checkNotNull(name, "name");
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
      if (!(obj instanceof RegionAndName))
         return false;
      RegionAndName other = RegionAndName.class.cast(obj);
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
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("region", region).add("name", name);
   }

   private static enum RegionFunction implements Function<RegionAndName, String> {
      INSTANCE;
      @Override
      public String apply(RegionAndName input) {
         return input.getRegion();
      }

      @Override
      public String toString() {
         return "getRegion()";
      }
   };

   public static Function<RegionAndName, String> regionFunction() {
      return RegionFunction.INSTANCE;
   }

   private static enum NameFunction implements Function<RegionAndName, String> {
      INSTANCE;
      @Override
      public String apply(RegionAndName input) {
         return input.getName();
      }

      @Override
      public String toString() {
         return "getName()";
      }
   };

   public static Function<RegionAndName, String> nameFunction() {
      return NameFunction.INSTANCE;
   }
   
}
