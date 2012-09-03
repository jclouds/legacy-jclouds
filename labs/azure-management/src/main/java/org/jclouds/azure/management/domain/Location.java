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
package org.jclouds.azure.management.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * A geographical region in which a service or storage account will be hosted.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 * 
 * @author Adrian Cole
 */
public class Location {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromLocation(this);
   }

   public static class Builder {

      private String name;
      private String displayName;
      private ImmutableSet.Builder<String> availableServices = ImmutableSet.<String> builder();

      /**
       * @see Location#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Location#getDisplayName()
       */
      public Builder displayName(String displayName) {
         this.displayName = displayName;
         return this;
      }

      /**
       * @see Location#getAvailableServices()
       */
      public Builder addAvailableService(String availableService) {
         this.availableServices.add(checkNotNull(availableService, "availableService"));
         return this;
      }

      /**
       * @see Location#getAvailableServices()
       */
      public Builder availableServices(Iterable<String> availableServices) {
         this.availableServices = ImmutableSet.<String> builder().addAll(
                  checkNotNull(availableServices, "availableServices"));
         return this;
      }

      public Location build() {
         return new Location(name, displayName, availableServices.build());
      }

      public Builder fromLocation(Location in) {
         return this.name(in.getName()).displayName(in.getDisplayName()).availableServices(in.getAvailableServices());
      }
   }

   private final String name;
   private final String displayName;
   private final Set<String> availableServices;

   protected Location(String name, String displayName, Iterable<String> availableServices) {
      this.name = checkNotNull(name, "name");
      this.displayName = checkNotNull(displayName, "displayName for %s", name);
      this.availableServices = ImmutableSet.copyOf(checkNotNull(availableServices, "availableServices for %s", name));
   }

   /**
    * 
    * The name of a data center location that is valid for your subscription. For example:
    * {@code West Europe}
    */
   public String getName() {
      return name;
   }

   /**
    * The localized name of data center location.
    */
   public String getDisplayName() {
      return displayName;
   }

   /**
    * Indicates the services available at a location.
    * 
    * Returned values are none, one, or both of the values listed below.
    * 
    * Compute
    * 
    * Storage
    */
   public Set<String> getAvailableServices() {
      return availableServices;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Location other = (Location) obj;
      return Objects.equal(this.name, other.name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("displayName", displayName)
               .add("availableServices", availableServices).toString();
   }

}
