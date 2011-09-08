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
package org.jclouds.softlayer.domain;

/**
 * 
 * @author Adrian Cole
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Location"
 *      />
 */
public class Location implements Comparable<Location> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id = -1;
      private String name;
      private String longName;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder longName(String longName) {
         this.longName = longName;
         return this;
      }

      public Location build() {
         return new Location(id, name, longName);
      }

      public static Builder fromLocation(Location in) {
         return Location.builder().id(in.getId()).name(in.getName()).longName(in.getLongName());
      }
   }

   private long id = -1;
   private String name;
   private String longName;

   // for deserializer
   Location() {

   }

   public Location(long id, String name, String longName) {
      this.id = id;
      this.name = name;
      this.longName = longName;
   }

   @Override
   public int compareTo(Location arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public long getId() {
      return id;
   }

   /**
    * @return A short location description.
    */
   public String getName() {
      return name;
   }

   /**
    * @return A longer location description.
    */
   public String getLongName() {
      return longName;
   }

   public Builder toBuilder() {
      return Builder.fromLocation(this);
   }
}
