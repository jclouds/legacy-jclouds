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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * The AllocationModel element defines how resources are allocated in a vDC.
 */
public enum AllocationModel {
   /**
    * Resources are committed to a vDC only when vApps are created in it
    */
   ALLOCATION_VAPP,
   /**
    * Only a percentage of the resources you allocate are committed to the organization vDC.
    */
   ALLOCATION_POOL,
   /**
    * All the resources you allocate are committed as a pool to the organization vDC. vApps in vDCs
    * that support this allocation model can specify values for resource and limit.
    */
   RESERVATION_POOL,
   /**
    * The VCloud API returned a model unsupported in the version 1.0 spec.
    */
   UNRECOGNIZED;

   public String value() {
      switch (this) {
         case ALLOCATION_VAPP:
            return "AllocationVApp";
         case ALLOCATION_POOL:
            return "AllocationPool";
         case RESERVATION_POOL:
            return "ReservationPool";
         default:
            return "UnrecognizedModel";
      }
   }

   @Override
   public String toString() {
      return value();
   }

   public static AllocationModel fromValue(String model) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(model, "model")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
