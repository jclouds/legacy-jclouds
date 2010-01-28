package org.jclouds.aws.ec2.compute.domain;

import org.jclouds.aws.domain.Region;


public class RegionTag {
   protected final Region region;
   protected final String tag;

   public RegionTag(Region region, String tag) {
      this.region = region;
      this.tag = tag;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((tag == null) ? 0 : tag.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RegionTag other = (RegionTag) obj;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (tag == null) {
         if (other.tag != null)
            return false;
      } else if (!tag.equals(other.tag))
         return false;
      return true;
   }

   public Region getRegion() {
      return region;
   }

   public String getTag() {
      return tag;
   }

}
