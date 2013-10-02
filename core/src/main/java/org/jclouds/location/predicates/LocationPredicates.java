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
package org.jclouds.location.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
public class LocationPredicates {

   public static Predicate<Location> isProvider() {
      return IsProvider.INSTANCE;
   }

   @Singleton
   static enum IsProvider implements Predicate<Location> {
      INSTANCE;
      @Override
      public boolean apply(Location input) {
         return input.getScope() == LocationScope.PROVIDER;
      }

      @Override
      public String toString() {
         return "isProvider()";
      }
   }

   public static Predicate<Location> isZone() {
      return IsZone.INSTANCE;
   }

   @Singleton
   static enum IsZone implements Predicate<Location> {
      INSTANCE;
      @Override
      public boolean apply(Location input) {
         return input.getScope() == LocationScope.ZONE;
      }

      @Override
      public String toString() {
         return "isZone()";
      }
   }

   public static Predicate<Location> isRegion() {
      return IsRegion.INSTANCE;
   }

   static enum IsRegion implements Predicate<Location> {
      INSTANCE;
      @Override
      public boolean apply(Location input) {
         return input.getScope() == LocationScope.REGION;
      }

      @Override
      public String toString() {
         return "isRegion()";
      }
   }

   public static Predicate<Location> isSystem() {
       return IsSystem.INSTANCE;
    }

    static enum IsSystem implements Predicate<Location> {
       INSTANCE;
       @Override
       public boolean apply(Location input) {
          return input.getScope() == LocationScope.SYSTEM;
       }

       @Override
       public String toString() {
          return "isSystem()";
       }
    }

    public static Predicate<Location> isNetwork() {
        return IsNetwork.INSTANCE;
     }

     static enum IsNetwork implements Predicate<Location> {
        INSTANCE;
        @Override
        public boolean apply(Location input) {
           return input.getScope() == LocationScope.NETWORK;
        }

        @Override
        public String toString() {
           return "isNetwork()";
        }
     }

   public static Predicate<Location> idEquals(String id) {
      return new IdEquals(id);
   }

   static class IdEquals implements Predicate<Location> {

      private final String id;

      IdEquals(String id) {
         this.id = checkNotNull(id, "id");
      }

      @Override
      public boolean apply(Location input) {

         return input.getId().equals(id);
      }

      @Override
      public String toString() {
         return "idEquals(" + id + ")";
      }
   }
   
   public static Predicate<Location> isZoneOrRegionWhereRegionIdEquals(String region) {
      return new IsZoneOrRegionWhereRegionIdEquals(region);
   }

   static class IsZoneOrRegionWhereRegionIdEquals implements Predicate<Location> {

      private final String region;

      IsZoneOrRegionWhereRegionIdEquals(String region) {
         this.region = checkNotNull(region, "region");
      }

      @Override
      public boolean apply(Location input) {
         switch (input.getScope()) {
         case ZONE:
            return input.getParent().getId().equals(region);
         case REGION:
            return input.getId().equals(region);
         default:
            return false;
         }
      }

      @Override
      public String toString() {
         return "isRegionAndIdEqualsOrIsZoneParentIdEquals(" + region + ")";
      }
   }

}
