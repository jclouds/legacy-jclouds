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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

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
      private String launchedAvailabilityZone;
      private Date createTime;
      private String faultCode;
      private String faultMessage;
      private String instanceId;
      private String launchGroup;
      private LaunchSpecification launchSpecification;
      private String productDescription;
      private String id;
      private float spotPrice;
      private State state;
      private String rawState;
      private Type type;
      private Date validFrom;
      private Date validUntil;
      private Map<String, String> tags = Maps.newLinkedHashMap();

      public Builder clear() {
         this.region = null;
         this.availabilityZoneGroup = null;
         this.launchedAvailabilityZone = null;
         this.createTime = null;
         this.faultCode = null;
         this.faultMessage = null;
         this.instanceId = null;
         this.launchGroup = null;
         this.launchSpecification = null;
         this.productDescription = null;
         this.id = null;
         this.spotPrice = 0;
         this.state = null;
         this.rawState = null;
         this.type = null;
         this.validFrom = null;
         this.validUntil = null;
         tags = Maps.newLinkedHashMap();
         return this;
      }

      public Builder region(String region) {
         this.region = region;
         return this;
      }

      public Builder tags(Map<String, String> tags) {
         this.tags = ImmutableMap.copyOf(checkNotNull(tags, "tags"));
         return this;
      }

      public Builder tag(String key, String value) {
         if (key != null)
            this.tags.put(key, Strings.nullToEmpty(value));
         return this;
      }
      public Builder availabilityZoneGroup(String availabilityZoneGroup) {
         this.availabilityZoneGroup = availabilityZoneGroup;
         return this;
      }

      public Builder launchedAvailabilityZone(String launchedAvailabilityZone) {
         this.launchedAvailabilityZone = launchedAvailabilityZone;
         return this;
      }

      public Builder createTime(Date createTime) {
         this.createTime = createTime;
         return this;
      }

      public Builder faultCode(String faultCode) {
         this.faultCode = faultCode;
         return this;
      }

      public Builder faultMessage(String faultMessage) {
         this.faultMessage = faultMessage;
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

      public Builder launchSpecification(LaunchSpecification launchSpecification) {
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

      public Builder state(State state) {
         this.state = state;
         return this;
      }
      
      public Builder rawState(String rawState) {
         this.rawState = rawState;
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
         return new SpotInstanceRequest(region, availabilityZoneGroup, launchedAvailabilityZone, createTime, faultCode,
                  faultMessage, instanceId, launchGroup, launchSpecification, productDescription, id, spotPrice, state,
                  rawState, type, validFrom, validUntil, tags);
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

   public enum State {
      OPEN, ACTIVE, CANCELLED, CLOSED, UNRECOGNIZED;

      public String value() {
         return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name()));
      }

      @Override
      public String toString() {
         return value();
      }

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "type")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final String region;
   private final String availabilityZoneGroup;
   private final String launchedAvailabilityZone;
   private final Date createTime;
   private final String faultCode;
   private final String faultMessage;
   private final String instanceId;
   private final String launchGroup;
   private final LaunchSpecification launchSpecification;
   private final String productDescription;
   private final String id;
   private final float spotPrice;
   private final State state;
   private final String rawState;
   private final Type type;
   private final Date validFrom;
   private final Date validUntil;
   private final Map<String, String> tags;

   public SpotInstanceRequest(String region, String availabilityZoneGroup, @Nullable String launchedAvailabilityZone,
            Date createTime, String faultCode, String faultMessage, String instanceId, String launchGroup,
            LaunchSpecification launchSpecification, String productDescription, String id, float spotPrice,
            State state, String rawState, Type type, Date validFrom, Date validUntil, Map<String, String> tags) {
      this.region = checkNotNull(region, "region");
      this.availabilityZoneGroup = availabilityZoneGroup;
      this.launchedAvailabilityZone = launchedAvailabilityZone;
      this.createTime = createTime;
      this.faultCode = faultCode;
      this.faultMessage = faultMessage;
      this.instanceId = instanceId;
      this.launchGroup = launchGroup;
      this.launchSpecification = launchSpecification;
      this.productDescription = productDescription;
      this.id = checkNotNull(id, "id");
      this.spotPrice = spotPrice;
      this.state = checkNotNull(state, "state");
      this.rawState = checkNotNull(rawState, "rawState");
      this.type = checkNotNull(type, "type");
      this.validFrom = validFrom;
      this.validUntil = validUntil;
      this.tags = ImmutableMap.<String, String> copyOf(checkNotNull(tags, "tags"));
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

   public String getLaunchedAvailabilityZone() {
      return launchedAvailabilityZone;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public String getFaultCode() {
      return faultCode;
   }

   public String getFaultMessage() {
      return faultMessage;
   }

   public String getInstanceId() {
      return instanceId;
   }

   public String getLaunchGroup() {
      return launchGroup;
   }

   public LaunchSpecification getLaunchSpecification() {
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

   public State getState() {
      return state;
   }
   
   public String getRawState() {
      return rawState;
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

   /**
    * tags that are present in the instance
    */
   public Map<String, String> getTags() {
      return tags;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((availabilityZoneGroup == null) ? 0 : availabilityZoneGroup.hashCode());
      result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
      result = prime * result + ((faultCode == null) ? 0 : faultCode.hashCode());
      result = prime * result + ((faultMessage == null) ? 0 : faultMessage.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((launchGroup == null) ? 0 : launchGroup.hashCode());
      result = prime * result + ((launchSpecification == null) ? 0 : launchSpecification.hashCode());
      result = prime * result + ((launchedAvailabilityZone == null) ? 0 : launchedAvailabilityZone.hashCode());
      result = prime * result + ((productDescription == null) ? 0 : productDescription.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + Float.floatToIntBits(spotPrice);
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
      result = prime * result + ((validUntil == null) ? 0 : validUntil.hashCode());
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
      if (faultCode == null) {
         if (other.faultCode != null)
            return false;
      } else if (!faultCode.equals(other.faultCode))
         return false;
      if (faultMessage == null) {
         if (other.faultMessage != null)
            return false;
      } else if (!faultMessage.equals(other.faultMessage))
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
      if (launchedAvailabilityZone == null) {
         if (other.launchedAvailabilityZone != null)
            return false;
      } else if (!launchedAvailabilityZone.equals(other.launchedAvailabilityZone))
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
      if (state != other.state)
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
      if (tags == null) {
         if (other.tags != null)
            return false;
      } else if (!tags.equals(other.tags))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[region=" + region + ", availabilityZoneGroup=" + availabilityZoneGroup + ", launchedAvailabilityZone="
            + launchedAvailabilityZone + ", createTime=" + createTime + ", faultCode=" + faultCode + ", faultMessage="
            + faultMessage + ", instanceId=" + instanceId + ", launchGroup=" + launchGroup + ", launchSpecification="
            + launchSpecification + ", productDescription=" + productDescription + ", id=" + id + ", spotPrice="
            + spotPrice + ", state=" + rawState + ", type=" + type + ", validFrom=" + validFrom + ", validUntil="
            + validUntil + ", tags=" + tags + "]";
   }

   @Override
   public int compareTo(SpotInstanceRequest arg0) {
      return createTime.compareTo(arg0.createTime);
   }

}
