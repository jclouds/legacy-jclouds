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
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a zone resource.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/zones"/>
 */
public class Zone extends Resource {

   public enum Status {
      UP,
      DOWN
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromZone(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private Status status;
      private ImmutableSet.Builder<ZoneMaintenanceWindow> maintenanceWindows = ImmutableSet.builder();
      private ImmutableSet.Builder<String> availableMachineTypes = ImmutableSet.builder();

      /**
       * @see Zone#getStatus()
       */
      public T status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see Zone#getMaintenanceWindows()
       */
      public T addMaintenanceWindow(ZoneMaintenanceWindow maintenanceWindow) {
         this.maintenanceWindows.add(checkNotNull(maintenanceWindow));
         return self();
      }

      /**
       * @see Zone#getMaintenanceWindows()
       */
      public T maintenanceWindows(Set<ZoneMaintenanceWindow> maintenanceWindows) {
         this.maintenanceWindows.addAll(checkNotNull(maintenanceWindows));
         return self();
      }

      /**
       * @see Zone#getAvailableMachineTypes()
       */
      public T addAvailableMachineType(String availableMachineType) {
         this.availableMachineTypes.add(checkNotNull(availableMachineType));
         return self();
      }

      /**
       * @see Zone#getAvailableMachineTypes()
       */
      public T availableMachineTypes(Set<String> availableMachineTypes) {
         this.availableMachineTypes.addAll(checkNotNull(availableMachineTypes));
         return self();
      }

      public Zone build() {
         return new Zone(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, status, maintenanceWindows.build(), availableMachineTypes.build());
      }

      public T fromZone(Zone in) {
         return super.fromResource(in).status(in.getStatus()).maintenanceWindows(in.getMaintenanceWindows())
                 .availableMachineTypes(in.getAvailableMachineTypes());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Status status;
   private final Set<ZoneMaintenanceWindow> maintenanceWindows;
   private final Set<String> availableMachineTypes;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "status", "maintenanceWindows",
           "availableMachineTypes"
   })
   protected Zone(String id, Date creationTimestamp, String selfLink, String name, String description,
                  Status status, Set<ZoneMaintenanceWindow> maintenanceWindows, Set<String> availableMachineTypes) {
      super(Kind.ZONE, id, creationTimestamp, selfLink, name, description);
      this.status = status;
      this.maintenanceWindows = nullCollectionOnNullOrEmpty(maintenanceWindows);
      this.availableMachineTypes = nullCollectionOnNullOrEmpty(availableMachineTypes);
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
   public Set<ZoneMaintenanceWindow> getMaintenanceWindows() {
      return maintenanceWindows;
   }

   /**
    * @return the machine types that can be used in this zone (output only).
    */
   @Nullable
   public Set<String> getAvailableMachineTypes() {
      return availableMachineTypes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, status, maintenanceWindows,
              availableMachineTypes);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Zone that = Zone.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.status, that.status)
              && Objects.equal(this.maintenanceWindows, that.maintenanceWindows)
              && Objects.equal(this.availableMachineTypes, that.availableMachineTypes);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("status", status).add("maintenanceWindows", maintenanceWindows).add("availableMachineTypes",
                      availableMachineTypes);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}