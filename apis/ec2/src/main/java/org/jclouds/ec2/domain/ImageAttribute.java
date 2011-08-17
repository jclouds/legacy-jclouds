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

import org.jclouds.ec2.EC2AsyncClient;

/**
 * 
 * An attribute of an AMI.
 * 
 * @author Adrian Cole
 * @see EC2AsyncClient#modifyImageAttribute
 * @see EC2AsyncClient#resetImageAttribute
 * @see EC2AsyncClient#describeImageAttribute
 * 
 */
public enum ImageAttribute {

   /**
    * the product code associated with the AMI.
    */
   PRODUCT_CODES,

   /**
    * the ID of the RAM disk associated with the AMI.
    */
   RAMDISK,

   /**
    * the ID of the kernel associated with the AMI.
    */
   KERNEL,
   /**
    * the launch permissions of the AMI.
    */
   LAUNCH_PERMISSION,
   /**
    * the operating system platform.
    */
   PLATFORM,
   /**
    * the mapping that defines native device names to use when exposing virtual devices.
    */
   BLOCK_DEVICE_MAPPING, UNRECOGNIZED;
   public String value() {
      switch (this) {
         case PRODUCT_CODES:
            return "productCodes";
         case RAMDISK:
            return "ramdisk";
         case KERNEL:
            return "kernel";
         case LAUNCH_PERMISSION:
            return "launchPermission";
         case PLATFORM:
            return "platform";
         case BLOCK_DEVICE_MAPPING:
            return "blockDeviceMapping";
         default:
            throw new IllegalArgumentException("unmapped attribute: " + super.name());
      }
   }

   @Override
   public String toString() {
      return value();
   }

   public static ImageAttribute fromValue(String attribute) {
      if ("productCodes".equals(attribute))
         return PRODUCT_CODES;
      else if ("ramdisk".equals(attribute))
         return RAMDISK;
      else if ("kernel".equals(attribute))
         return KERNEL;
      else if ("launchPermission".equals(attribute))
         return LAUNCH_PERMISSION;
      else if ("platform".equals(attribute))
         return PLATFORM;
      else if ("blockDeviceMapping".equals(attribute))
         return BLOCK_DEVICE_MAPPING;
      else
         return UNRECOGNIZED;
   }

}
