/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Zone;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author Adrian Cole
 */
public class ZonePredicates {

   public static class SupportsNetworkType implements Predicate<Zone> {
      private final org.jclouds.cloudstack.domain.NetworkType type;

      public SupportsNetworkType(org.jclouds.cloudstack.domain.NetworkType type) {
         this.type = checkNotNull(type, "type");
      }

      @Override
      public boolean apply(Zone input) {
         return type.equals(checkNotNull(input, "zone").getNetworkType());
      }

      @Override
      public String toString() {
         return "supportsNetworkType(" + type + ")";
      }
   }

   static Predicate<Zone> supportsAdvancedNetworks = new SupportsNetworkType(
            org.jclouds.cloudstack.domain.NetworkType.ADVANCED);

   /**
    * 
    * @return true, if the zone supports {@link NetworkType.ADVANCED}
    */
   public static Predicate<Zone> supportsAdvancedNetworks() {
      return supportsAdvancedNetworks;
   }

   /**
    * 
    * @return always returns true.
    */
   public static Predicate<Zone> any() {
      return Predicates.alwaysTrue();
   }
}
