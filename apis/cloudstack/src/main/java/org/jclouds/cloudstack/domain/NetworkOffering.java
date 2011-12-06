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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkOffering implements Comparable<NetworkOffering> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String name;
      private String displayText;
      private Date created;
      private NetworkOfferingAvailabilityType availability;
      private Integer maxConnections;
      private int networkRate;
      private boolean isDefault;
      private boolean supportsVLAN;
      private TrafficType trafficType;
      private GuestIPType guestIPType;
      private Set<String> tags = ImmutableSet.of();

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder displayText(String displayText) {
         this.displayText = displayText;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder availability(NetworkOfferingAvailabilityType availability) {
         this.availability = availability;
         return this;
      }

      public Builder maxConnections(Integer maxConnections) {
         this.maxConnections = maxConnections;
         return this;
      }

      public Builder isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return this;
      }

      public Builder networkRate(int networkRate) {
         this.networkRate = networkRate;
         return this;
      }

      public Builder supportsVLAN(boolean supportsVLAN) {
         this.supportsVLAN = supportsVLAN;
         return this;
      }

      public Builder trafficType(TrafficType trafficType) {
         this.trafficType = trafficType;
         return this;
      }

      public Builder guestIPType(GuestIPType guestIPType) {
         this.guestIPType = guestIPType;
         return this;
      }

      public Builder tags(Set<String> tags) {
         this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
         return this;
      }

      public NetworkOffering build() {
         return new NetworkOffering(id, name, displayText, created, availability, supportsVLAN, maxConnections,
               isDefault, trafficType, guestIPType, networkRate, tags);
      }
   }

   private long id;
   private String name;
   @SerializedName("displaytext")
   private String displayText;
   private Date created;
   @SerializedName("availability")
   private NetworkOfferingAvailabilityType availability;
   @SerializedName("maxconnections")
   private Integer maxConnections;
   @SerializedName("isdefault")
   private boolean isDefault;
   @SerializedName("specifyvlan")
   private boolean supportsVLAN;
   @SerializedName("traffictype")
   private TrafficType trafficType;
   @SerializedName("guestiptype")
   private GuestIPType guestIPType;
   @SerializedName("networkrate")
   private int networkRate = -1;
   private String tags;

   public NetworkOffering(long id, String name, String displayText, @Nullable Date created,
         NetworkOfferingAvailabilityType availability, boolean supportsVLAN, @Nullable Integer maxConnections,
         boolean isDefault, TrafficType trafficType, GuestIPType guestIPType, int networkRate, Set<String> tags) {
      this.id = id;
      this.name = name;
      this.displayText = displayText;
      this.created = created;
      this.availability = availability;
      this.supportsVLAN = supportsVLAN;
      this.maxConnections = maxConnections;
      this.isDefault = isDefault;
      this.trafficType = trafficType;
      this.guestIPType = guestIPType;
      this.networkRate = networkRate;
      this.tags = tags.size() == 0 ? null : Joiner.on(',').join(tags);
   }

   /**
    * present only for serializer
    * 
    */
   NetworkOffering() {

   }

   /**
    * 
    * @return the id of the network offering
    */
   public long getId() {
      return id;
   }

   /**
    * 
    * @return the name of the network offering
    */

   public String getName() {
      return name;
   }

   /**
    * 
    * @return an alternate display text of the network offering.
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * 
    * @return the date this network offering was created
    */
   @Nullable
   public Date getCreated() {
      return created;
   }

   /**
    * 
    * @return Availability name for the offering
    */
   public NetworkOfferingAvailabilityType getAvailability() {
      return availability;
   }

   /**
    * 
    * @return true if network offering supports vlans, false otherwise
    */
   public boolean supportsVLAN() {
      return supportsVLAN;
   }

   /**
    * 
    * @return the max number of concurrent connection the network offering
    *         supports
    */
   @Nullable
   public Integer getMaxConnections() {
      return maxConnections;
   }

   /**
    * 
    * @return true if network offering is default, false otherwise
    */
   public boolean isDefault() {
      return isDefault;
   }

   /**
    * 
    * @return the traffic type for this network offering
    */
   public TrafficType getTrafficType() {
      return trafficType;
   }

   /**
    * 
    * @return the guest ip type for this network offering
    */
   public GuestIPType getGuestIPType() {
      return guestIPType;
   }

   /**
    * 
    * @return data transfer rate in megabits per second allowed.
    */
   public int getNetworkRate() {
      return networkRate;
   }

   /**
    * 
    * @return the tags for the network offering
    */
   public Set<String> getTags() {
      return tags != null ? ImmutableSet.copyOf(Splitter.on(',').split(tags)) : ImmutableSet.<String> of();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((availability == null) ? 0 : availability.hashCode());
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + ((displayText == null) ? 0 : displayText.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (isDefault ? 1231 : 1237);
      result = prime * result + ((maxConnections == null) ? 0 : maxConnections.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (supportsVLAN ? 1231 : 1237);
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
      result = prime * result + ((trafficType == null) ? 0 : trafficType.hashCode());
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
      NetworkOffering other = (NetworkOffering) obj;
      if (availability == null) {
         if (other.availability != null)
            return false;
      } else if (!availability.equals(other.availability))
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (displayText == null) {
         if (other.displayText != null)
            return false;
      } else if (!displayText.equals(other.displayText))
         return false;
      if (id != other.id)
         return false;
      if (isDefault != other.isDefault)
         return false;
      if (maxConnections == null) {
         if (other.maxConnections != null)
            return false;
      } else if (!maxConnections.equals(other.maxConnections))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (supportsVLAN != other.supportsVLAN)
         return false;
      if (tags == null) {
         if (other.tags != null)
            return false;
      } else if (!tags.equals(other.tags))
         return false;
      if (trafficType != other.trafficType)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", displayText=" + displayText + ", created=" + created
            + ", maxConnections=" + maxConnections + ", trafficType=" + trafficType + ", isDefault=" + isDefault
            + ", availability=" + availability + ", supportsVLAN=" + supportsVLAN + ", tags=" + getTags() + "]";
   }

   @Override
   public int compareTo(NetworkOffering arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

}
