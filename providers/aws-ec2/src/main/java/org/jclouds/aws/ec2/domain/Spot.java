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
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

/**
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSpotPriceHistory.html"
 *      />
 * @author Adrian Cole
 */
public class Spot implements Comparable<Spot> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String region;
      private String instanceType;
      private String productDescription;
      private float spotPrice;
      private Date timestamp;

      public void clear() {
         this.region = null;
         this.instanceType = null;
         this.productDescription = null;
         this.spotPrice = 0.0f;
         this.timestamp = null;
      }

      public Builder region(String region) {
         this.region = region;
         return this;
      }

      public Builder instanceType(String instanceType) {
         this.instanceType = instanceType;
         return this;
      }

      public Builder productDescription(String productDescription) {
         this.productDescription = productDescription;
         return this;
      }

      public Builder spotPrice(float spotPrice) {
         this.spotPrice = spotPrice;
         return this;
      }

      public Builder timestamp(Date timestamp) {
         this.timestamp = timestamp;
         return this;
      }

      public Spot build() {
         return new Spot(region, instanceType, productDescription, spotPrice, timestamp);
      }
   }

   private final String region;
   private final String instanceType;
   private final String productDescription;
   private final float spotPrice;
   private final Date timestamp;

   public Spot(String region, String instanceType, String productDescription, float spotPrice, Date timestamp) {
      this.region = checkNotNull(region, "region");
      this.instanceType = checkNotNull(instanceType, "instanceType");
      this.productDescription = checkNotNull(productDescription, "productDescription");
      this.spotPrice = spotPrice;
      this.timestamp = checkNotNull(timestamp, "timestamp");
   }

   public String getRegion() {
      return region;
   }

   public String getInstanceType() {
      return instanceType;
   }

   public String getProductDescription() {
      return productDescription;
   }

   public float getSpotPrice() {
      return spotPrice;
   }

   public Date getTimestamp() {
      return timestamp;
   }

   @Override
   public int compareTo(Spot o) {
      return Float.compare(spotPrice, o.spotPrice);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      result = prime * result + ((productDescription == null) ? 0 : productDescription.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + Float.floatToIntBits(spotPrice);
      result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
      Spot other = (Spot) obj;
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
      if (Float.floatToIntBits(spotPrice) != Float.floatToIntBits(other.spotPrice))
         return false;
      if (timestamp == null) {
         if (other.timestamp != null)
            return false;
      } else if (!timestamp.equals(other.timestamp))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[region=" + region + ", instanceType=" + instanceType + ", productDescription=" + productDescription
            + ", spotPrice=" + spotPrice + ", timestamp=" + timestamp + "]";
   }

}