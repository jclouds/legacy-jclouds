/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Class NetworkOffering
 *
 * @author Adrian Cole
 */
public class NetworkOffering implements Comparable<NetworkOffering> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromNetworkOffering(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String displayText;
      protected Date created;
      protected NetworkOfferingAvailabilityType availability;
      protected Integer maxConnections;
      protected boolean isDefault;
      protected boolean supportsVLAN;
      protected TrafficType trafficType;
      protected GuestIPType guestIPType;
      protected int networkRate;
      protected ImmutableSet.Builder<String> tags = ImmutableSet.<String>builder();

      /**
       * @see NetworkOffering#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see NetworkOffering#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see NetworkOffering#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @see NetworkOffering#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see NetworkOffering#getAvailability()
       */
      public T availability(NetworkOfferingAvailabilityType availability) {
         this.availability = availability;
         return self();
      }

      /**
       * @see NetworkOffering#getMaxConnections()
       */
      public T maxConnections(Integer maxConnections) {
         this.maxConnections = maxConnections;
         return self();
      }

      /**
       * @see NetworkOffering#isDefault()
       */
      public T isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return self();
      }

      /**
       * @see NetworkOffering#supportsVLAN()
       */
      public T supportsVLAN(boolean supportsVLAN) {
         this.supportsVLAN = supportsVLAN;
         return self();
      }

      /**
       * @see NetworkOffering#getTrafficType()
       */
      public T trafficType(TrafficType trafficType) {
         this.trafficType = trafficType;
         return self();
      }

      /**
       * @see NetworkOffering#getGuestIPType()
       */
      public T guestIPType(GuestIPType guestIPType) {
         this.guestIPType = guestIPType;
         return self();
      }

      /**
       * @see NetworkOffering#getNetworkRate()
       */
      public T networkRate(int networkRate) {
         this.networkRate = networkRate;
         return self();
      }
      
      /**
       * @see NetworkOffering#getTags()
       */
      public T tags(Iterable<String> tags) {
         this.tags = ImmutableSet.<String>builder().addAll(tags);
         return self();
      }
      
      /**
       * @see NetworkOffering#getTags()
       */
      public T tag(String tag) {
         this.tags.add(tag);
         return self();
      }
      

      public NetworkOffering build() {
         return new NetworkOffering(id, name, displayText, created, availability, maxConnections, isDefault, supportsVLAN, trafficType, guestIPType, networkRate, tags.build());
      }

      public T fromNetworkOffering(NetworkOffering in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .displayText(in.getDisplayText())
               .created(in.getCreated())
               .availability(in.getAvailability())
               .maxConnections(in.getMaxConnections())
               .isDefault(in.isDefault())
               .supportsVLAN(in.supportsVLAN())
               .trafficType(in.getTrafficType())
               .guestIPType(in.getGuestIPType())
               .networkRate(in.getNetworkRate())
               .tags(in.getTags());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final String displayText;
   private final Date created;
   private final NetworkOfferingAvailabilityType availability;
   private final Integer maxConnections;
   private final boolean isDefault;
   private final boolean supportsVLAN;
   private final TrafficType trafficType;
   private final GuestIPType guestIPType;
   private final int networkRate;
   private final Set<String> tags;

   @ConstructorProperties({
         "id", "name", "displaytext", "created", "availability", "maxconnections", "isdefault", "specifyvlan", "traffictype", "guestiptype", "networkrate", "tags"
   })
   protected NetworkOffering(String id, @Nullable String name, @Nullable String displayText, @Nullable Date created, @Nullable NetworkOfferingAvailabilityType availability, @Nullable Integer maxConnections, boolean isDefault, boolean supportsVLAN, @Nullable TrafficType trafficType, @Nullable GuestIPType guestIPType, int networkRate, @Nullable Iterable<String> tags) {
      this.id = checkNotNull(id, "id");
      this.name = name;
      this.displayText = displayText;
      this.created = created;
      this.availability = availability;
      this.maxConnections = maxConnections;
      this.isDefault = isDefault;
      this.supportsVLAN = supportsVLAN;
      this.trafficType = trafficType;
      this.guestIPType = guestIPType;
      this.networkRate = networkRate;
      this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.<String> of();
   }

   /**
    * @return the id of the network offering
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of the network offering
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return an alternate display text of the network offering.
    */
   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   /**
    * @return the date this network offering was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return Availability name for the offering
    */
   @Nullable
   public NetworkOfferingAvailabilityType getAvailability() {
      return this.availability;
   }

   /**
    * @return the max number of concurrent connection the network offering
    *         supports
    */
   @Nullable
   public Integer getMaxConnections() {
      return this.maxConnections;
   }

   /**
    * @return true if network offering is default, false otherwise
    */
   public boolean isDefault() {
      return this.isDefault;
   }

   /**
    * @return true if network offering supports vlans, false otherwise
    */
   public boolean supportsVLAN() {
      return this.supportsVLAN;
   }

   /**
    * @return the traffic type for this network offering
    */
   @Nullable
   public TrafficType getTrafficType() {
      return this.trafficType;
   }

   /**
    * @return the guest ip type for this network offering
    */
   @Nullable
   public GuestIPType getGuestIPType() {
      return this.guestIPType;
   }

   /**
    * @return data transfer rate in megabits per second allowed.
    */
   public int getNetworkRate() {
      return this.networkRate;
   }

   /**
    * @return the tags for the network offering
    */
   public Set<String> getTags() {
      return this.tags;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, displayText, created, availability, maxConnections, isDefault, supportsVLAN, trafficType, guestIPType, networkRate, tags);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      NetworkOffering that = NetworkOffering.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.availability, that.availability)
            && Objects.equal(this.maxConnections, that.maxConnections)
            && Objects.equal(this.isDefault, that.isDefault)
            && Objects.equal(this.supportsVLAN, that.supportsVLAN)
            && Objects.equal(this.trafficType, that.trafficType)
            && Objects.equal(this.guestIPType, that.guestIPType)
            && Objects.equal(this.networkRate, that.networkRate)
            && Objects.equal(this.tags, that.tags);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("displayText", displayText).add("created", created).add("availability", availability).add("maxConnections", maxConnections).add("isDefault", isDefault).add("supportsVLAN", supportsVLAN).add("trafficType", trafficType).add("guestIPType", guestIPType).add("networkRate", networkRate).add("tags", tags);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(NetworkOffering o) {
      return id.compareTo(o.getId());
   }

}
