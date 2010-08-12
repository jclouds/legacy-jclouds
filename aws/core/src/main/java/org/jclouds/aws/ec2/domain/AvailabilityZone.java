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

package org.jclouds.aws.ec2.domain;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * Availability zones used for all ec2 instance commands.
 * 
 * @author Adrian Cole
 */
public class AvailabilityZone {

   public static final String EU_WEST_1A = "eu-west-1a";
   public static final String EU_WEST_1B = "eu-west-1b";
   public static final String US_EAST_1A = "us-east-1a";
   public static final String US_EAST_1B = "us-east-1b";
   public static final String US_EAST_1C = "us-east-1c";
   public static final String US_EAST_1D = "us-east-1d";
   public static final String US_WEST_1A = "us-west-1a";
   public static final String US_WEST_1B = "us-west-1b";
   public static final String AP_SOUTHEAST_1A = "ap-southeast-1a";
   public static final String AP_SOUTHEAST_1B = "ap-southeast-1b";

   public static final Set<String> zones = ImmutableSet.of(EU_WEST_1A, EU_WEST_1B,
           US_EAST_1A, US_EAST_1B, US_EAST_1C, US_EAST_1D,
           US_WEST_1A, US_WEST_1B, AP_SOUTHEAST_1A, AP_SOUTHEAST_1B);

}