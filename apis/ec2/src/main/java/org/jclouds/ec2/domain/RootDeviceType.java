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
package org.jclouds.ec2.domain;

import com.google.common.base.CaseFormat;

/**
 * The root device type used by the AMI. The AMI can use an Amazon EBS or instance store root
 * device.
 * 
 * @author Adrian Cole
 */
public enum RootDeviceType {

   INSTANCE_STORE,

   EBS, UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
   }

   public String toString() {
      return value();
   }

   public static RootDeviceType fromValue(String v) {
      try {
         return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, v));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
