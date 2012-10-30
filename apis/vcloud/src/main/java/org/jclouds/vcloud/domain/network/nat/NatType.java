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
package org.jclouds.vcloud.domain.network.nat;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * 
 * The NatType element specifies how network address translation is implemented by the NAT service.
 * 
 * @since vcloud api 0.9
 * 
 * @author Adrian Cole
 */
public enum NatType {
   /**
    * NAT service implemented by IP address translation
    * 
    * @since vcloud api 0.9
    */
   IP_TRANSLATION,
   /**
    * NAT service implemented by network port forwarding
    * 
    * @since vcloud api 0.9
    */
   PORT_FORWARDING, UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static NatType fromValue(String natType) {
      try {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(natType, "natType")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
