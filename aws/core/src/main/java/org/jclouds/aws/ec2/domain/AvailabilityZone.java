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
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * 
 * Availability zones used for all ec2 instance commands.
 * 
 * @author Adrian Cole
 */
public enum AvailabilityZone {

   UNKNOWN, EU_WEST_1A, EU_WEST_1B, US_EAST_1A, US_EAST_1B, US_EAST_1C, US_EAST_1D, US_WEST_1A, US_WEST_1B, AP_SOUTHEAST_1A, AP_SOUTHEAST_1B;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static AvailabilityZone fromValue(String availablilityZone) {
      return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(
               availablilityZone, "availablilityZone")));
   }
}