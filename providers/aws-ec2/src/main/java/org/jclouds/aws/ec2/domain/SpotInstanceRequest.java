/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.CaseFormat;

/**
 * 
 * @author Adrian Cole
 */
public class SpotInstanceRequest implements Comparable<SpotInstanceRequest> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String region;
      private String availabilityZoneGroup;
      private Date createTime;
      private String fault;
      private String instanceId;
      private String launchGroup;
      private String launchSpecification;
      private String productDescription;
      private String id;
      private float spotPrice;
      private String state;
      private Type type;
      private Date validFrom;
      private Date validUntil;

      public Builder region(String region) {
         this.region = region;
         return this;
      }

      public Builder availabilityZoneGroup(String availabilityZoneGroup) {
         this.availabilityZoneGroup = availabilityZoneGroup;
         return this;
      }

      public Builder createTime(Date createTime) {
         this.createTime = createTime;
         return this;
      }

      public Builder fault(String fault) {
         this.fault = fault;
         return this;
      }

      public Builder instanceId(String instanceId) {
         this.instanceId = instanceId;
         return this;
      }

      public Builder launchGroup(String launchGroup) {
         this.launchGroup = launchGroup;
         return this;
      }

      public Builder launchSpecification(String launchSpecification) {
         this.launchSpecification = launchSpecification;
         return this;
      }

      public Builder productDescription(String productDescription) {
         this.productDescription = productDescription;
         return this;
      }

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder spotPrice(float spotPrice) {
         this.spotPrice = spotPrice;
         return this;
      }

      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder validFrom(Date validFrom) {
         this.validFrom = validFrom;
         return this;
      }

      public Builder validUntil(Date validUntil) {
         this.validUntil = validUntil;
         return this;
      }

      public SpotInstanceRequest build() {
         return new SpotInstanceRequest(region, availabilityZoneGroup, createTime, fault, instanceId, launchGroup,
               launchSpecification, productDescription, id, spotPrice, state, type, validFrom, validUntil);
      }
   }

   public enum Type {
      ONE_TIME, PERSISTENT, UNRECOGNIZED;

      public String value() {
         return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name()));
      }

      @Override
      public String toString() {
         return value();
      }

      public static Type fromValue(String type) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final String region;
   private final String availabilityZoneGroup;
   private final Date createTime;
   private final String fault;
   private final String instanceId;
   private final String launchGroup;
   private final String launchSpecification;
   private final String productDescription;
   private final String id;
   private final float spotPrice;
   private final String state;
   private final Type type;
   private final Date validFrom;
   private final Date validUntil;

   public SpotInstanceRequest(String region, String availabilityZoneGroup, Date createTime, String fault,
         String instanceId, String launchGroup, String launchSpecification, String productDescription, String id,
         float spotPrice, String state, Type type, Date validFrom, Date validUntil) {
      this.region = checkNotNull(region, "region");
      this.availabilityZoneGroup = availabilityZoneGroup;
      this.createTime = createTime;
      this.fault = fault;
      this.instanceId = instanceId;
      this.launchGroup = launchGroup;
      this.launchSpecification = launchSpecification;
      this.productDescription = productDescription;
      this.id = checkNotNull(id, "id");
      this.spotPrice = spotPrice;
      this.state = checkNotNull(state, "state");
      this.type = checkNotNull(type, "type");
      this.validFrom = validFrom;
      this.validUntil = validUntil;
   }

   /**
    * @return spot instance requests are in a region
    */
   public String getRegion() {
      return region;
   }

   public String getAvailabilityZoneGroup() {
      return availabilityZoneGroup;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public String getFault() {
      return fault;
   }

   public String getInstanceId() {
      return instanceId;
   }

   public String getLaunchGroup() {
      return launchGroup;
   }

   public String getLaunchSpecification() {
      return launchSpecification;
   }

   public String getProductDescription() {
      return productDescription;
   }

   public String getId() {
      return id;
   }

   public float getSpotPrice() {
      return spotPrice;
   }

   public String getState() {
      return state;
   }

   public Type getType() {
      return type;
   }

   public Date getValidFrom() {
      return validFrom;
   }

   public Date getValidUntil() {
      return validUntil;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((availabilityZoneGroup == null) ? 0 : availabilityZoneGroup.hashCode());
      result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
      result = prime * result + ((fault == null) ? 0 : fault.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((launchGroup == null) ? 0 : launchGroup.hashCode());
      result = prime * result + ((launchSpecification == null) ? 0 : launchSpecification.hashCode());
      result = prime * result + ((productDescription == null) ? 0 : productDescription.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + Float.floatToIntBits(spotPrice);
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
      result = prime * result + ((validUntil == null) ? 0 : validUntil.hashCode());
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
      SpotInstanceRequest other = (SpotInstanceRequest) obj;
      if (availabilityZoneGroup == null) {
         if (other.availabilityZoneGroup != null)
            return false;
      } else if (!availabilityZoneGroup.equals(other.availabilityZoneGroup))
         return false;
      if (createTime == null) {
         if (other.createTime != null)
            return false;
      } else if (!createTime.equals(other.createTime))
         return false;
      if (fault == null) {
         if (other.fault != null)
            return false;
      } else if (!fault.equals(other.fault))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (launchGroup == null) {
         if (other.launchGroup != null)
            return false;
      } else if (!launchGroup.equals(other.launchGroup))
         return false;
      if (launchSpecification == null) {
         if (other.launchSpecification != null)
            return false;
      } else if (!launchSpecification.equals(other.launchSpecification))
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
      if (type != other.type)
         return false;
      if (validFrom == null) {
         if (other.validFrom != null)
            return false;
      } else if (!validFrom.equals(other.validFrom))
         return false;
      if (validUntil == null) {
         if (other.validUntil != null)
            return false;
      } else if (!validUntil.equals(other.validUntil))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[region=" + region + ", id=" + id + ", spotPrice=" + spotPrice + ", state=" + state
            + ", availabilityZoneGroup=" + availabilityZoneGroup + ", createTime=" + createTime + ", fault=" + fault
            + ", type=" + type + ", instanceId=" + instanceId + ", launchGroup=" + launchGroup
            + ", launchSpecification=" + launchSpecification + ", productDescription=" + productDescription
            + ", validFrom=" + validFrom + ", validUntil=" + validUntil + "]";
   }

   @Override
   public int compareTo(SpotInstanceRequest arg0) {
      return createTime.compareTo(arg0.createTime);
   }

}
