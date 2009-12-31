/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Indicates the status of the VApp.
 * 
 * @author Adrian Cole
 */
public enum VAppStatus {
   /**
    * The vApp is unresolved (one or more file references are unavailable in the cloud)
    */
   UNRESOLVED,
   /**
    * The vApp is resolved (all file references are available in the cloud) but not deployed
    */
   RESOLVED,
   /**
    * The vApp is deployed and powered off
    */
   OFF,
   /**
    * The vApp is deployed and suspended
    */
   SUSPENDED,
   /**
    * The vApp is deployed and powered on
    */
   ON;

   public String value() {
      switch (this) {
         case UNRESOLVED:
            return "0";
         case RESOLVED:
            return "1";
         case OFF:
            return "2";
         case SUSPENDED:
            return "3";
         case ON:
            return "4";
         default:
            throw new IllegalArgumentException("invalid status:" + this);
      }
   }

   public static VAppStatus fromValue(String status) {
      return fromValue(Integer.parseInt(checkNotNull(status, "status")));
   }

   public static VAppStatus fromValue(int v) {
      switch (v) {
         case 0:
            return UNRESOLVED;
         case 1:
            return RESOLVED;
         case 2:
            return OFF;
         case 3:
            return SUSPENDED;
         case 4:
            return ON;
         default:
            throw new IllegalArgumentException("invalid status:" + v);
      }
   }

}