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

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
 *      />
 * @author Adrian Cole
 */
public class ReservedInstancesOffering implements Comparable<ReservedInstancesOffering> {
   private final String region;
   private final String availabilityZone;
   private final long duration;
   private final float fixedPrice;
   private final String instanceType;
   private final String productDescription;
   private final String id;
   private final float usagePrice;

   public ReservedInstancesOffering(String region, String availabilityZone, long duration, float fixedPrice, String instanceType,
         String productDescription, String reservedInstancesOfferingId, float usagePrice) {
      this.region=region;
      this.availabilityZone = availabilityZone;
      this.duration = duration;
      this.fixedPrice = fixedPrice;
      this.instanceType = instanceType;
      this.productDescription = productDescription;
      this.id = reservedInstancesOfferingId;
      this.usagePrice = usagePrice;
   }

   public String getRegion() {
      return region;
   }

   /**
    * @return The Availability Zone in which the Reserved Instance can be used.
    */
   public String getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * 
    * @return The duration of the Reserved Instance, in seconds
    */
   public long getDuration() {
      return duration;
   }

   /**
    * 
    * @return The purchase price of the Reserved Instance.
    */
   public float getFixedPrice() {
      return fixedPrice;
   }

   /**
    * 
    * @return The instance type on which the Reserved Instance can be used.
    */
   public String getInstanceType() {
      return instanceType;
   }

   /**
    * 
    * @return The Reserved Instance description.
    */
   public String getProductDescription() {
      return productDescription;
   }

   /**
    * @return The ID of the Reserved Instance offering.
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return The usage price of the Reserved Instance, per hour.
    */
   public float getUsagePrice() {
      return usagePrice;
   }

   @Override
   public int compareTo(ReservedInstancesOffering o) {
      return id.compareTo(o.id);
   }

   @Override
   public String toString() {
      return "[availabilityZone=" + availabilityZone + ", duration=" + duration
            + ", fixedPrice=" + fixedPrice + ", id=" + id + ", instanceType=" + instanceType + ", productDescription="
            + productDescription + ", region=" + region + ", usagePrice=" + usagePrice + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());
      result = prime * result + (int) (duration ^ (duration >>> 32));
      result = prime * result + Float.floatToIntBits(fixedPrice);
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      result = prime * result + ((productDescription == null) ? 0 : productDescription.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + Float.floatToIntBits(usagePrice);
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
      ReservedInstancesOffering other = (ReservedInstancesOffering) obj;
      if (availabilityZone == null) {
         if (other.availabilityZone != null)
            return false;
      } else if (!availabilityZone.equals(other.availabilityZone))
         return false;
      if (duration != other.duration)
         return false;
      if (Float.floatToIntBits(fixedPrice) != Float.floatToIntBits(other.fixedPrice))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (instanceType == null) {
         if (other.instanceType != null)
            return false;
      } else if (!instanceType.equals(other.instanceType))
         return false;
      if (productDescription == null) {
         if (other.productDescription != null)
            return false;
      } else if (!productDescription.equals(other.productDescription))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (Float.floatToIntBits(usagePrice) != Float.floatToIntBits(other.usagePrice))
         return false;
      return true;
   }

}
