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
package org.jclouds.elb.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * Specifies the type of LoadBalancer. This option is only available for
 * LoadBalancers attached to an Amazon VPC.
 */
public enum Scheme {

   /**
    * the LoadBalancer has a publicly resolvable DNS name that resolves to
    * public IP addresses
    */
   INTERNET_FACING,
   /**
    * the LoadBalancer has a publicly resolvable DNS name that resolves to
    * private IP addresses.
    */
   INTERNAL,
   /**
    * The scheme was returned unrecognized.
    */
   UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static Scheme fromValue(String scheme) {
      try {
         return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(scheme, "scheme")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
