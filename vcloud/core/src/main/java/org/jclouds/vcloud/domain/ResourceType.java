package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.vcloud.VCloudAsyncClient;

/**
 * 
 * VirtualResource such as disks or CPU
 * 
 * @author Adrian Cole
 * @see VCloudAsyncClient#getVApp
 * 
 * 
 */
public enum ResourceType {

   VIRTUAL_CPU,

   MEMORY,

   SCSI_CONTROLLER,

   VIRTUAL_DISK;

   public String value() {
      switch (this) {
         case VIRTUAL_CPU:
            return "3";
         case MEMORY:
            return "4";
         case SCSI_CONTROLLER:
            return "6";
         case VIRTUAL_DISK:
            return "17";
         default:
            throw new IllegalArgumentException("invalid type:" + this);
      }
   }

   public static ResourceType fromValue(String type) {
      return fromValue(Integer.parseInt(checkNotNull(type, "type")));
   }

   public static ResourceType fromValue(int v) {
      switch (v) {
         case 3:
            return VIRTUAL_CPU;
         case 4:
            return MEMORY;
         case 6:
            return SCSI_CONTROLLER;
         case 17:
            return VIRTUAL_DISK;
         default:
            throw new IllegalArgumentException("invalid type:" + v);
      }
   }
}