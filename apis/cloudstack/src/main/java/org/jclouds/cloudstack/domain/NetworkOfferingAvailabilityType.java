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
package org.jclouds.cloudstack.domain;

import com.google.common.base.CaseFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Andrei Savu
 */
public enum NetworkOfferingAvailabilityType {

   DEFAULT,

   REQUIRED, /* default value for Guest Virtual network offering */

   OPTIONAL, /* default value for Guest Direct network offering */

   UNRECOGNIZED;

   @Override
   public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
   }

   public static NetworkOfferingAvailabilityType fromValue(String type) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
