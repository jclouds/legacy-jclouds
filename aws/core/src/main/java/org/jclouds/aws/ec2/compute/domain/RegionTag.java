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
package org.jclouds.aws.ec2.compute.domain;

/**
 * 
 * @author Adrian Cole
 */
public class RegionTag {
   protected final String region;
   protected final String tag;

   public RegionTag(String region, String tag) {
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

   public String getRegion() {
      return region;
   }

   public String getTag() {
      return tag;
   }

   @Override
   public String toString() {
      return "RegionTag [region=" + region + ", tag=" + tag + "]";
   }

}
