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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * @author Adrian Cole
 * @see org.jclouds.cloudstack.features.OfferingApi#listNetworkOfferings
 */
public enum GuestIPType {

   /**
    * guest IP address will be issued by Dhcp server in the guest virtual
    * network. Dhcp role is played by domain router.
    */
   VIRTUAL,

   /**
    * traffic directly to the network and VMs created here are assigned an IP
    * directly from the network as configured
    */
   DIRECT,

   /**
    * TODO: add comments to explain the meaning (cs3 only)
    */
   SHARED,
   ISOLATED,

   UNRECOGNIZED;

   @Override
   public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
   }

   public static GuestIPType fromValue(String type) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
