/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-AvailabilityZoneItemType.html"
 *      />
 * @author Adrian Cole
 */
public class AvailabilityZoneInfo implements Comparable<AvailabilityZoneInfo>{

   public static enum State {
      UNKNOWN, AVAILABLE;
      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static State fromValue(String state) {
         return valueOf(checkNotNull(state, "state").toUpperCase());
      }
   }

   private final String zoneName;
   private final AvailabilityZone zone;
   private final State state;
   private final Region region;
   private final Set<String> messages = Sets.newHashSet();

   public AvailabilityZoneInfo(String zoneName, AvailabilityZone zone, State zoneState,
            Region region, Iterable<String> messages) {
      this.zoneName = checkNotNull(zoneName, "zoneName");
      this.zone = checkNotNull(zone, "zone");
      this.state = checkNotNull(zoneState, "zoneState");
      this.region = checkNotNull(region, "region");
      Iterables.addAll(this.messages, checkNotNull(messages, "messages"));
   }

   /**
    * Name of the Availability Zone.
    */
   public String getZoneName() {
      return zoneName;
   }

   /**
    * the Availability Zone.
    */
   public AvailabilityZone getZone() {
      return zone;
   }

   /**
    * State of the Availability Zone.
    */
   public State getState() {
      return state;
   }

   /**
    * Name of the Region.
    */
   public Region getRegion() {
      return region;
   }

   /**
    * Messages
    */
   public Set<String> getMessages() {
      return messages;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((messages == null) ? 0 : messages.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((zone == null) ? 0 : zone.hashCode());
      result = prime * result + ((zoneName == null) ? 0 : zoneName.hashCode());
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
      AvailabilityZoneInfo other = (AvailabilityZoneInfo) obj;
      if (messages == null) {
         if (other.messages != null)
            return false;
      } else if (!messages.equals(other.messages))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (zone == null) {
         if (other.zone != null)
            return false;
      } else if (!zone.equals(other.zone))
         return false;
      if (zoneName == null) {
         if (other.zoneName != null)
            return false;
      } else if (!zoneName.equals(other.zoneName))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "AvailabilityZoneInfo [messages=" + messages + ", region=" + region + ", state="
               + state + ", zone=" + zone + ", zoneName=" + zoneName + "]";
   }

   @Override
   public int compareTo(AvailabilityZoneInfo that) {
      return zoneName.compareTo(that.zoneName);
   }

}