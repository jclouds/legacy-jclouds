package org.jclouds.deltacloud.collections;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * 
 * @author Adrian Cole
 */
public enum DeltacloudCollection {
   HARDWARE_PROFILES, INSTANCE_STATES, REALMS,

   @Images
   IMAGES,

   @Instances
   INSTANCES, UNRECOGNIZED;

   public String value() {
      return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name()));
   }

   @Override
   public String toString() {
      return value();
   }

   public static DeltacloudCollection fromValue(String link) {
      try {
         return valueOf(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(link, "link")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}