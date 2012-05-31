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
package org.jclouds.openstack.glance.v1_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * Detailed listing of an Image
 * 
 * @author Adrian Cole
 * @see <a href= "http://glance.openstack.org/glanceapi.html" />
 * @see <a href= "https://github.com/openstack/glance/blob/master/glance/api/v1/images.py" />
 * @see <a href= "https://github.com/openstack/glance/blob/master/glance/registry/db/api.py" />
 */
public class ImageDetails extends Image {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromImageDetails(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Image.Builder<T> {
      private long minDisk;
      private long minRam;
      private Optional<String> location = Optional.absent();
      private Optional<String> owner = Optional.absent();
      private Date updatedAt;
      private Date createdAt;
      private Optional<Date> deletedAt = Optional.absent();
      private Status status = Status.UNRECOGNIZED;
      private boolean isPublic;
      private Map<String, String> properties = ImmutableMap.of();

      /**
       * @see ImageDetails#getMinDisk()
       */
      public T minDisk(long minDisk) {
         this.minDisk = minDisk;
         return self();
      }

      /**
       * @see ImageDetails#getMinRam()
       */
      public T minRam(long minRam) {
         this.minRam = minRam;
         return self();
      }

      /**
       * @see ImageDetails#getLocation()
       */
      public T location(Optional<String> location) {
         this.location = location;
         return self();
      }

      /**
       * @see ImageDetails#getLocation()
       */
      public T location(String location) {
         return location(Optional.of(location));
      }

      /**
       * @see ImageDetails#getOwner()
       */
      public T owner(Optional<String> owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see ImageDetails#getOwner()
       */
      public T owner(String owner) {
         return owner(Optional.of(owner));
      }

      /**
       * @see ImageDetails#getUpdatedAt()
       */
      public T updatedAt(Date updatedAt) {
         this.updatedAt = updatedAt;
         return self();
      }

      /**
       * @see ImageDetails#getCreatedAt()
       */
      public T createdAt(Date createdAt) {
         this.createdAt = createdAt;
         return self();
      }

      /**
       * @see ImageDetails#getDeletedAt()
       */
      public T deletedAt(Optional<Date> deletedAt) {
         this.deletedAt = deletedAt;
         return self();
      }

      /**
       * @see ImageDetails#getDeletedAt()
       */
      public T deletedAt(Date deletedAt) {
         return deletedAt(Optional.of(deletedAt));
      }

      /**
       * @see ImageDetails#getStatus()
       */
      public T status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see ImageDetails#isPublic()
       */
      public T isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      /**
       * @see ImageDetails#getProperties()
       */
      public T properties(Map<String, String> properties) {
         this.properties = properties;
         return self();
      }

      public ImageDetails build() {
         return new ImageDetails(this);
      }

      public T fromImageDetails(ImageDetails in) {
         return super.fromImage(in).minDisk(in.getMinDisk()).minRam(in.getMinRam()).location(in.getLocation())
                  .updatedAt(in.getUpdatedAt()).createdAt(in.getCreatedAt()).deletedAt(in.getDeletedAt()).status(
                           in.getStatus()).isPublic(in.isPublic()).properties(in.getProperties());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected ImageDetails() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }

   // | min_disk | int(11) | YES | | NULL | |
   @SerializedName("min_disk")
   private long minDisk;
   // | min_ram | int(11) | YES | | NULL | |
   @SerializedName("min_ram")
   private long minRam;
   // | location | text | YES | | NULL | |
   private Optional<String> location = Optional.absent();
   // | owner | varchar(255) | YES | | NULL | |
   private Optional<String> owner = Optional.absent();
   // | updated_at | datetime | YES | | NULL | |
   @SerializedName("updated_at")
   private Date updatedAt;
   // | created_at | datetime | NO | | NULL | |
   @SerializedName("created_at")
   private Date createdAt;
   @SerializedName("deleted_at")
   private Optional<Date> deletedAt = Optional.absent();
   // | status | varchar(30) | NO | | NULL | |
   private Status status = Status.UNRECOGNIZED;
   // | is_public | tinyint(1) | NO | | NULL | |
   @SerializedName("is_public")
   private boolean isPublic;
   private Map<String, String> properties = ImmutableMap.of();

   protected ImageDetails(Builder<?> builder) {
      super(builder);
      this.minDisk = builder.minDisk;
      this.minRam = checkNotNull(builder.minRam, "minRam");
      this.location = checkNotNull(builder.location, "location");
      this.owner = checkNotNull(builder.owner, "owner");
      this.updatedAt = checkNotNull(builder.updatedAt, "updatedAt");
      this.createdAt = checkNotNull(builder.createdAt, "createdAt");
      this.deletedAt = checkNotNull(builder.deletedAt, "deletedAt");
      this.status = checkNotNull(builder.status, "status");
      this.isPublic = checkNotNull(builder.isPublic, "isPublic");
      this.properties = ImmutableMap.copyOf(builder.properties);
   }

   /**
    * Note this could be zero if unset
    */
   public long getMinDisk() {
      return this.minDisk;
   }

   /**
    * Note this could be zero if unset
    */
   public long getMinRam() {
      return this.minRam;
   }

   public Status getStatus() {
      return this.status;
   }

   public Optional<String> getLocation() {
      return this.location;
   }

   public Optional<String> getOwner() {
      return owner;
   }
   
   public Date getUpdatedAt() {
      return this.updatedAt;
   }

   public Date getCreatedAt() {
      return this.createdAt;
   }

   public Optional<Date> getDeletedAt() {
      return this.deletedAt;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public Map<String, String> getProperties() {
      // in case this was assigned in gson
      return ImmutableMap.copyOf(Maps.filterValues(this.properties, Predicates.notNull()));
   }

   // hashCode/equals from super is ok

   @Override
   protected ToStringHelper string() {
      return super.string().add("minDisk", minDisk).add("minRam", minRam).add("location", location).add("deletedAt",
               getDeletedAt()).add("updatedAt", updatedAt).add("createdAt", createdAt).add("status", status).add(
               "location", location).add("owner", owner).add("isPublic", isPublic).add("properties", properties);
   }

}