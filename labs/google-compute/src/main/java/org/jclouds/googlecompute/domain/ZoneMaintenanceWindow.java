/*
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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;

import java.beans.ConstructorProperties;
import java.util.Date;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Scheduled maintenance windows for the zone. When the zone is in a maintenance window,
 * all resources which reside in the zone will be unavailable.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/zones"/>
 */
public class ZoneMaintenanceWindow {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromZoneMaintenanceWindow(this);
   }

   public static class Builder {

      private String name;
      private String description;
      private Date beginTime;
      private Date endTime;


      /**
       * @see ZoneMaintenanceWindow#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see ZoneMaintenanceWindow#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see ZoneMaintenanceWindow#getBeginTime()
       */
      public Builder beginTime(Date beginTime) {
         this.beginTime = beginTime;
         return this;
      }

      /**
       * @see ZoneMaintenanceWindow#getEndTime()
       */
      public Builder endTime(Date endTime) {
         this.endTime = endTime;
         return this;
      }


      public ZoneMaintenanceWindow build() {
         return new ZoneMaintenanceWindow(name, description, beginTime, endTime);
      }

      public Builder fromZoneMaintenanceWindow(ZoneMaintenanceWindow in) {
         return new Builder().name(in.getName()).description(in.getDescription()).beginTime(in.getBeginTime())
                 .endTime(in.getEndTime());
      }
   }


   private final String name;
   private final String description;
   private final Date beginTime;
   private final Date endTime;

   @ConstructorProperties({
           "name", "description", "beginTime", "endTime"
   })
   private ZoneMaintenanceWindow(String name, String description, Date beginTime, Date endTime) {
      this.name = checkNotNull(name);
      this.description = description;
      this.beginTime = beginTime;
      this.endTime = endTime;

   }

   /**
    * @return name of the maintenance window.
    */
   public String getName() {
      return name;
   }

   /**
    * @return textual description of the maintenance window.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return begin time of the maintenance window, in RFC 3339 format.
    */
   public Date getBeginTime() {
      return beginTime;
   }

   /**
    * @return end time of the maintenance window, in RFC 3339 format.
    */
   public Date getEndTime() {
      return endTime;
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, description, beginTime, endTime);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ZoneMaintenanceWindow that = ZoneMaintenanceWindow.class.cast(obj);
      return equal(this.name, that.name)
              && equal(this.description, that.description)
              && equal(this.beginTime, that.beginTime)
              && equal(this.endTime, that.endTime);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("name", name).add("description", description).add("beginTime", beginTime).add("endTime",
                      endTime);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
