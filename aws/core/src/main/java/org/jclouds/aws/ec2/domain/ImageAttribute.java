package org.jclouds.aws.ec2.domain;

import org.jclouds.aws.ec2.EC2Client;

/**
 * 
 * An attribute of an AMI.
 * 
 * @author Adrian Cole
 * @see EC2Client#modifyImageAttribute
 * @see EC2Client#resetImageAttribute
 * @see EC2Client#describeImageAttribute
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
   BLOCK_DEVICE_MAPPING;
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
         throw new IllegalArgumentException("unmapped attribute: " + attribute);
   }

}