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
package org.jclouds.trmk.vcloud_0_8.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Objects such as vAppTemplate, vApp, and Vm have a status attribute whose
 * value indicates the state of the object. Status for an object, such as a
 * vAppTemplate or vApp, whose Children (Vm objects) each have a status of their
 * own, is computed from the status of the Children.
 * 
 * <h2>NOTE</h2>
 * <p/>
 * The deployment status of an object is indicated by the value of its deployed
 * attribute.
 * 
 * @since vcloud api 0.8
 * 
 * @author Adrian Cole
 */
public enum Status {

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
    * The {@link VAppTemplate}, {@link VApp}, or {@link Vm} is off.
    * 
    * @since vcloud api 0.8
    */
   OFF, UNRECOGNIZED, DEPLOYED;
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
         return OFF;
      case 3:
         return SUSPENDED;
      case 4:
         return ON;
      case 7:
         return UNRECOGNIZED;
      default:
         return UNRECOGNIZED;
      }
   }

}
