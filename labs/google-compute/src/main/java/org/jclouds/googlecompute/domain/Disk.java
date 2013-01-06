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

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A persistent disk resource
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/disks"/>
 */
@Beta
public final class Disk extends Resource {

   private final Integer sizeGb;
   private final URI zone;
   private final String status;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "sizeGb", "zone",
           "status"
   })
   private Disk(String id, Date creationTimestamp, URI selfLink, String name, String description,
                Integer sizeGb, URI zone, String status) {
      super(Kind.DISK, checkNotNull(id, "id of %s", name), fromNullable(creationTimestamp), checkNotNull(selfLink,
              "selfLink of %s", name), checkNotNull(name, "name"), fromNullable(description));
      this.sizeGb = checkNotNull(sizeGb, "sizeGb of %s", name);
      this.zone = checkNotNull(zone, "zone of %s", name);
      this.status = checkNotNull(status, "status of %s", name);
   }

   /**
    * @return size of the persistent disk, specified in GB.
    */
   public int getSizeGb() {
      return sizeGb;
   }

   /**
    * @return URL for the zone where the persistent disk resides.
    */
   public URI getZone() {
      return zone;
   }

   /**
    * @return the status of disk creation.
    */
   public String getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("sizeGb", sizeGb)
              .add("zone", zone)
              .add("status", status);
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
      return new Builder().fromDisk(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Integer sizeGb;
      private URI zone;;
      private String status;

      /**
       * @see Disk#getSizeGb()
       */
      public Builder sizeGb(Integer sizeGb) {
         this.sizeGb = sizeGb;
         return this;
      }

      /**
       * @see Disk#getZone()
       */
      public Builder zone(URI zone) {
         this.zone = zone;
         return this;
      }

      /**
       * @see Disk#getStatus()
       */
      public Builder status(String status) {
         this.status = status;
         return this;
      }


      @Override
      protected Builder self() {
         return this;
      }

      public Disk build() {
         return new Disk(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, sizeGb, zone, status);
      }

      public Builder fromDisk(Disk in) {
         return super.fromResource(in)
                 .sizeGb(in.getSizeGb())
                 .zone(in.getZone())
                 .status(in.getStatus());
      }

   }

}
