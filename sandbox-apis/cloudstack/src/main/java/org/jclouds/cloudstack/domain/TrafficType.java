/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * 
 * @author Adrian Cole
 * @see NetworkOfferingClient#listNetworkOfferings
 */
public enum TrafficType {

   /**
    * traffic to the public internet
    */
   PUBLIC,
   /**
    * VM-to-VM traffic and VMs are assigned a virtual IP created by CloudStack
    */
   GUEST,
   /**
    * System network that only Admin can view, and only when isSystem is specified.
    */
   STORAGE,
   /**
    * System network that only Admin can view, and only when isSystem is specified.
    */
   MANAGEMENT,
   /**
    * System network that only Admin can view, and only when isSystem is specified.
    */
   CONTROL,

   UNRECOGNIZED;
   @Override
   public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
   }

   public static TrafficType fromValue(String type) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
