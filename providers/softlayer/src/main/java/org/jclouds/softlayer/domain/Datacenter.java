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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Adrian Cole
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Location_Datacenter"
 *      />
 */
public class Datacenter implements Comparable<Datacenter> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int id = -1;
      private String name;
      private String longName;
      private Address locationAddress;
      private Set<Region> regions = Sets.newLinkedHashSet();

      public Builder id(int id) {
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

      public Builder locationAddress(Address locationAddress) {
         this.locationAddress = locationAddress;
         return this;
      }

      public Builder region(Region regions) {
         this.regions.add(checkNotNull(regions, "regions"));
         return this;
      }

      public Builder regions(Iterable<Region> regions) {
         this.regions = ImmutableSet.<Region> copyOf(checkNotNull(regions, "regions"));
         return this;
      }

      public Datacenter build() {
         return new Datacenter(id, name, longName, locationAddress, regions);
      }

      public static Builder fromDatacenter(Datacenter in) {
         return Datacenter.builder().id(in.getId()).name(in.getName())
               .longName(in.getLongName()).locationAddress(in.getLocationAddress())
               .regions(in.getRegions());
      }
   }

   private int id = -1;
   private String name;
   private String longName;
   private Address locationAddress;
   private Set<Region> regions = Sets.newLinkedHashSet();
   // for deserializer
   Datacenter() {

   }

   public Datacenter(int id, String name, String longName, Address locationAddress, Iterable<Region> regions) {
      this.id = id;
      this.name = name;
      this.longName = longName;
      this.locationAddress = locationAddress;
      this.regions = ImmutableSet.<Region> copyOf(checkNotNull(regions, "regions"));
   }

   @Override
   public int compareTo(Datacenter arg0) {
      return new Integer(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public int getId() {
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

   /**
    * @return A location's physical address (optional).
    */
   public Address getLocationAddress() {
      return locationAddress;
   }

   /**
    * A location can be a member of 1 or more regions.
    * Sometimes the list of regions is empty, for example as a new Datacenter is being added.
    * The list of regions usually contains one with keyName=FIRST_AVAILABLE which should be ignored.
    * @return The regions to which a location belongs.
    */
   public Set<Region> getRegions() {
      return regions;
   }

   public Builder toBuilder() {
      return Builder.fromDatacenter(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (id ^ (id >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Datacenter other = (Datacenter) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", country=" + name + ", state=" + longName + "], locationAddress=" + locationAddress + ", regions="+regions+"]";
   }
   
   
}
