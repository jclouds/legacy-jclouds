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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;

/**
 * The root device type used by an AMI.
 * 
 * @author Adrian Cole
 */
public enum RootDeviceType {

   /** EC2 Instance Storage. */
   INSTANCE_STORE,

   /** Amazon Elastic Block Storage. */
   EBS,
   
   UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static RootDeviceType fromValue(@Nullable String v) {
      try {
         return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, v));
      } catch (NullPointerException e) {
         return null;
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
