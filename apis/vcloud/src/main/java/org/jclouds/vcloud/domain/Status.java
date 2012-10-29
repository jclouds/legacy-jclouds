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

/**
 * Objects such as vAppTemplate, vApp, and Vm have a status attribute whose value indicates the
 * state of the object. Status for an object, such as a vAppTemplate or vApp, whose Children (Vm
 * objects) each have a status of their own, is computed from the status of the Children.
 * 
 * <h2>NOTE</h2>
 * <p/>
 * The deployment status of an object is indicated by the value of its deployed attribute.
 * 
 * @since vcloud api 0.8
 * 
 * @author Adrian Cole
 */
public enum Status {
   /**
    * The {@link VAppTemplate}, {@link VApp}, or {@link Vm} could not be created.
    * 
    * @since vcloud api 1.0
    */
   ERROR,
   /**
    * The {@link VAppTemplate}, {@link VApp}, or {@link Vm} is unresolved.
    * 
    * @since vcloud api 0.8
    */
   UNRESOLVED,
   /**
    * The {@link VAppTemplate}, {@link VApp}, or {@link Vm} is resolved.
    * 
    * @since vcloud api 0.8
    */
   RESOLVED,
   /**
    * The object is deployed.
    * <p/>
    * note that the documentation does not reference use of this.
    * 
    * @since vcloud api 1.0
    */
   DEPLOYED,
   /**
    * The {@link VApp} or {@link Vm} is suspended.
    * 
    * @since vcloud api 0.8
    */
   SUSPENDED,
   /**
    * The {@link VApp} or {@link Vm} is powered on
    * 
    * @since vcloud api 0.8
    */
   ON,
   /**
    * The {@link VApp} or {@link Vm} waiting for user input.
    * 
    * @since vcloud api 1.0
    */
   WAITING_FOR_INPUT,
   /**
    * The {@link VAppTemplate}, {@link VApp}, or {@link Vm} is in an unknown state.
    * 
    * @since vcloud api 1.0
    */
   UNKNOWN,
   /**
    * The {@link VAppTemplate}, {@link VApp}, or {@link Vm} is in an unrecognized state.
    * 
    * @since vcloud api 1.0
    */
   UNRECOGNIZED,
   /**
    * The {@link VAppTemplate}, {@link VApp}, or {@link Vm} is off.
    * 
    * @since vcloud api 0.8
    */
   OFF,
   /**
    * The {@link VApp} or {@link Vm} is in an inconsistent state.
    * 
    * @since vcloud api 1.0
    */
   INCONSISTENT,
   /**
    * The {@link VAppTemplate} or {@link VApp} have children do not all have the same status.
    * 
    * @since vcloud api 1.0
    */
   MIXED,
   /**
    * The {@link VAppTemplate} Upload initiated, OVF descriptor pending
    * 
    * @since vcloud api 1.0
    */
   PENDING_DESCRIPTOR,
   /**
    * The {@link VAppTemplate} Upload initiated, copying contents
    * 
    * @since vcloud api 1.0
    */
   COPYING,
   /**
    * The {@link VAppTemplate} Upload initiated, disk contents pending
    * 
    * @since vcloud api 1.0
    */
   PENDING_CONTENTS,
   /**
    * The {@link VAppTemplate} Upload has been quarantined
    * 
    * @since vcloud api 1.0
    */
   QUARANTINED,
   /**
    * The {@link VAppTemplate} Upload quarantine period has expired
    * 
    * @since vcloud api 1.0
    */
   QUARANTINE_EXPIRED, 
   /**
    * The {@link VAppTemplate} rejected
    * 
    * @since vcloud api 1.0
    */
   REJECTED, 
   /**
    * The {@link VAppTemplate} transfer timeout
    * 
    * @since vcloud api 1.0
    */
   TRANSFER_TIMEOUT;

   public String value() {
      switch (this) {
         case UNRESOLVED:
            return "0";
         case RESOLVED:
            return "1";
         case DEPLOYED:
            return "2";
         case SUSPENDED:
            return "3";
         case ON:
            return "4";
         case WAITING_FOR_INPUT:
            return "5";
         case UNKNOWN:
            return "6";
         case UNRECOGNIZED:
            return "7";
         case OFF:
            return "8";
         case INCONSISTENT:
            return "9";
         case MIXED:
            return "10";
         case PENDING_DESCRIPTOR:
            return "11";
         case COPYING:
            return "12";
         case PENDING_CONTENTS:
            return "13";
         case QUARANTINED:
            return "14";
         case QUARANTINE_EXPIRED:
            return "15";
         case REJECTED:
            return "16";
         case TRANSFER_TIMEOUT:
            return "17";
         default:
            return "7";
      }
   }

   public static Status fromValue(String status) {
      try {
         return fromValue(Integer.parseInt(checkNotNull(status, "status")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

   public static Status fromValue(int v) {
      switch (v) {
         case 0:
            return UNRESOLVED;
         case 1:
            return RESOLVED;
         case 2:
            return DEPLOYED;
         case 3:
            return SUSPENDED;
         case 4:
            return ON;
         case 5:
            return WAITING_FOR_INPUT;
         case 6:
            return UNKNOWN;
         case 7:
            return UNRECOGNIZED;
         case 8:
            return OFF;
         case 9:
            return INCONSISTENT;
         case 10:
            return MIXED;
         case 11:
            return PENDING_DESCRIPTOR;
         case 12:
            return COPYING;
         case 13:
            return PENDING_CONTENTS;
         case 14:
            return QUARANTINED;
         case 15:
            return QUARANTINE_EXPIRED;
         case 16:
            return REJECTED;
         case 17:
            return TRANSFER_TIMEOUT;
         default:
            return UNRECOGNIZED;
      }
   }

}
