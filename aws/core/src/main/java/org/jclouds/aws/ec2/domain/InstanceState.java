package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.ec2.EC2Client;

/**
 * 
 * The current state of the instance..
 * 
 * @author Adrian Cole
 * @see EC2Client#describeInstances
 * @see EC2Client#runInstances
 * @see EC2Client#terminateInstances
 * 
 */
public enum InstanceState {

   /**
    * the instance is in the process of being launched
    */
   PENDING,

   /**
    * the instance launched (although the boot process might not be completed)
    */
   RUNNING,

   /**
    * the instance started shutting down
    */
   SHUTTING_DOWN,
   /**
    * the instance terminated
    */
   TERMINATED;

   public String value() {
      return name().toLowerCase().replaceAll("_", "-");
   }

   @Override
   public String toString() {
      return value();
   }

   public static InstanceState fromValue(String state) {
      return valueOf(checkNotNull(state, "state").replaceAll("-", "_").toUpperCase());
   }

   public static InstanceState fromValue(int v) {
      switch (v) {
         case 0:
            return PENDING;
         case 16:
            return RUNNING;
         case 32:
            return SHUTTING_DOWN;
         case 48:
            return TERMINATED;
         default:
            throw new IllegalArgumentException("invalid state:" + v);
      }
   }
}