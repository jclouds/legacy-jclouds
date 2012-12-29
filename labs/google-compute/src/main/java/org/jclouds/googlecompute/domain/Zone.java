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

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a zone resource.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/zones"/>
 */
@Beta
public final class Zone extends Resource {

   public enum Status {
      UP,
      DOWN
   }

   private final Status status;
   private final Set<MaintenanceWindow> maintenanceWindows;
   private final Set<String> availableMachineTypes;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "status", "maintenanceWindows",
           "availableMachineTypes"
   })
   private Zone(String id, Date creationTimestamp, URI selfLink, String name, String description,
                Status status, Set<MaintenanceWindow> maintenanceWindows, Set<String> availableMachineTypes) {
      super(Kind.ZONE, checkNotNull(id, "id of %name", name), fromNullable(creationTimestamp),
              checkNotNull(selfLink, "selfLink of %name", name), checkNotNull(name, "name"), fromNullable(description));
      this.status = checkNotNull(status, "status of %name", name);
      this.maintenanceWindows = maintenanceWindows == null ? ImmutableSet.<MaintenanceWindow>of() : ImmutableSet
              .copyOf(maintenanceWindows);
      this.availableMachineTypes = availableMachineTypes == null ? ImmutableSet.<String>of() : ImmutableSet
              .copyOf(availableMachineTypes);
   }

   /**
    * @return Status of the zone. "UP" or "DOWN".
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @return scheduled maintenance windows for the zone. When the zone is in a maintenance window,
    *         all resources which reside in the zone will be unavailable.
    */
   public Set<MaintenanceWindow> getMaintenanceWindows() {
      return maintenanceWindows;
   }

   /**
    * @return the machine types that can be used in this zone.
    */
   @Nullable
   public Set<String> getAvailableMachineTypes() {
      return availableMachineTypes;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("status", status)
              .add("maintenanceWindows", maintenanceWindows)
              .add("availableMachineTypes", availableMachineTypes);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromZone(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Status status;
      private ImmutableSet.Builder<MaintenanceWindow> maintenanceWindows = ImmutableSet.builder();
      private ImmutableSet.Builder<String> availableMachineTypes = ImmutableSet.builder();

      /**
       * @see Zone#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see Zone#getMaintenanceWindows()
       */
      public Builder addMaintenanceWindow(MaintenanceWindow maintenanceWindow) {
         this.maintenanceWindows.add(checkNotNull(maintenanceWindow, "maintenanceWindow"));
         return this;
      }

      /**
       * @see Zone#getMaintenanceWindows()
       */
      public Builder maintenanceWindows(Set<MaintenanceWindow> maintenanceWindows) {
         this.maintenanceWindows.addAll(checkNotNull(maintenanceWindows, "maintenanceWindows"));
         return this;
      }

      /**
       * @see Zone#getAvailableMachineTypes()
       */
      public Builder addAvailableMachineType(String availableMachineType) {
         this.availableMachineTypes.add(checkNotNull(availableMachineType, "availableMachineType"));
         return this;
      }

      /**
       * @see Zone#getAvailableMachineTypes()
       */
      public Builder availableMachineTypes(Set<String> availableMachineTypes) {
         this.availableMachineTypes.addAll(checkNotNull(availableMachineTypes, "availableMachineTypes"));
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Zone build() {
         return new Zone(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, status, maintenanceWindows.build(), availableMachineTypes.build());
      }

      public Builder fromZone(Zone in) {
         return super.fromResource(in)
                 .status(in.getStatus())
                 .maintenanceWindows(in.getMaintenanceWindows())
                 .availableMachineTypes(in.getAvailableMachineTypes());
      }
   }

   /**
    * Scheduled maintenance windows for the zone. When the zone is in a maintenance window,
    * all resources which reside in the zone will be unavailable.
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/zones"/>
    */
   public static final class MaintenanceWindow {

      private final String name;
      private final Optional<String> description;
      private final Date beginTime;
      private final Date endTime;

      @ConstructorProperties({
              "name", "description", "beginTime", "endTime"
      })
      private MaintenanceWindow(String name, String description, Date beginTime, Date endTime) {
         this.name = checkNotNull(name, "name");
         this.description = fromNullable(description);
         this.beginTime = checkNotNull(beginTime, "beginTime of %name", name);
         this.endTime = checkNotNull(endTime, "endTime of %name", name);
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
      public Optional<String> getDescription() {
         return description;
      }

      /**
       * @return begin time of the maintenance window.
       */
      public Date getBeginTime() {
         return beginTime;
      }

      /**
       * @return end time of the maintenance window.
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
         MaintenanceWindow that = MaintenanceWindow.class.cast(obj);
         return equal(this.name, that.name)
                 && equal(this.beginTime, that.beginTime)
                 && equal(this.endTime, that.endTime);
      }

      /**
       * {@inheritDoc}
       */
      protected Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("name", name)
                 .add("description", description.orNull())
                 .add("beginTime", beginTime)
                 .add("endTime", endTime);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromZoneMaintenanceWindow(this);
      }

      public static final class Builder {

         private String name;
         private String description;
         private Date beginTime;
         private Date endTime;

         /**
          * @see org.jclouds.googlecompute.domain.Zone.MaintenanceWindow#getName()
          */
         public Builder name(String name) {
            this.name = name;
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Zone.MaintenanceWindow#getDescription()
          */
         public Builder description(String description) {
            this.description = description;
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Zone.MaintenanceWindow#getBeginTime()
          */
         public Builder beginTime(Date beginTime) {
            this.beginTime = beginTime;
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Zone.MaintenanceWindow#getEndTime()
          */
         public Builder endTime(Date endTime) {
            this.endTime = endTime;
            return this;
         }


         public MaintenanceWindow build() {
            return new MaintenanceWindow(name, description, beginTime, endTime);
         }

         public Builder fromZoneMaintenanceWindow(MaintenanceWindow in) {
            return new Builder()
                    .name(in.getName())
                    .description(in.getDescription().orNull())
                    .beginTime(in.getBeginTime())
                    .endTime(in.getEndTime());
         }
      }
   }
}
