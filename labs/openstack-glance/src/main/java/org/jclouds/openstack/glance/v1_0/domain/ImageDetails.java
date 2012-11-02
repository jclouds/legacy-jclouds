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
package org.jclouds.openstack.glance.v1_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

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

   public abstract static class Builder<T extends Builder<T>> extends Image.Builder<T>  {
      protected long minDisk;
      protected long minRam;
      protected String location;
      protected String owner;
      protected Date updatedAt;
      protected Date createdAt;
      protected Date deletedAt;
      protected Image.Status status;
      protected boolean isPublic;
      protected Map<String, String> properties = ImmutableMap.of();

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
      public T location(String location) {
         this.location = location;
         return self();
      }

      /**
       * @see ImageDetails#getOwner()
       */
      public T owner(String owner) {
         this.owner = owner;
         return self();
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
      public T deletedAt(Date deletedAt) {
         this.deletedAt = deletedAt;
         return self();
      }

      /**
       * @see ImageDetails#getStatus()
       */
      public T status(Image.Status status) {
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
         this.properties = ImmutableMap.copyOf(checkNotNull(properties, "properties"));
         return self();
      }

      public ImageDetails build() {
         return new ImageDetails(id, name, links, containerFormat, diskFormat, size, checksum, minDisk, minRam, location, owner, updatedAt, createdAt, deletedAt, status, isPublic, properties);
      }

      public T fromImageDetails(ImageDetails in) {
         return super.fromImage(in)
               .minDisk(in.getMinDisk())
               .minRam(in.getMinRam())
               .location(in.getLocation().orNull())
               .owner(in.getOwner().orNull())
               .updatedAt(in.getUpdatedAt())
               .createdAt(in.getCreatedAt())
               .deletedAt(in.getDeletedAt().orNull())
               .status(in.getStatus())
               .isPublic(in.isPublic())
               .properties(in.getProperties());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("min_disk")
   private final long minDisk;
   @Named("min_ram")
   private final long minRam;
   private final Optional<String> location;
   private final Optional<String> owner;
   @Named("updated_at")
   private final Date updatedAt;
   @Named("created_at")
   private final Date createdAt;
   @Named("deleted_at")
   private final Optional<Date> deletedAt;
   private final Image.Status status;
   @Named("is_public")
   private final boolean isPublic;
   private final Map<String, String> properties;

   @ConstructorProperties({
         "id", "name", "links", "container_format", "disk_format", "size", "checksum", "min_disk", "min_ram", "location", "owner", "updated_at", "created_at", "deleted_at", "status", "is_public", "properties"
   })
   protected ImageDetails(String id, @Nullable String name, Set<Link> links, @Nullable ContainerFormat containerFormat,
                          @Nullable DiskFormat diskFormat, @Nullable Long size, @Nullable String checksum, long minDisk,
                          long minRam, @Nullable String location, @Nullable String owner, Date updatedAt, 
                          Date createdAt, @Nullable Date deletedAt, Image.Status status, boolean isPublic, 
                          Map<String, String> properties) {
      super(id, name, links, containerFormat, diskFormat, size, checksum);
      this.minDisk = minDisk;
      this.minRam = minRam;
      this.location = Optional.fromNullable(location);
      this.owner = Optional.fromNullable(owner);
      this.updatedAt = checkNotNull(updatedAt, "updatedAt");
      this.createdAt = checkNotNull(createdAt, "createdAt");
      this.deletedAt = Optional.fromNullable(deletedAt);
      this.status = checkNotNull(status, "status");
      this.isPublic = isPublic;
      this.properties = ImmutableMap.copyOf(checkNotNull(properties, "properties"));
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

   public Optional<String> getLocation() {
      return this.location;
   }

   public Optional<String> getOwner() {
      return this.owner;
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

   public Image.Status getStatus() {
      return this.status;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(minDisk, minRam, location, owner, updatedAt, createdAt, deletedAt, status, isPublic, properties);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ImageDetails that = ImageDetails.class.cast(obj);
      return super.equals(that) && Objects.equal(this.minDisk, that.minDisk)
            && Objects.equal(this.minRam, that.minRam)
            && Objects.equal(this.location, that.location)
            && Objects.equal(this.owner, that.owner)
            && Objects.equal(this.updatedAt, that.updatedAt)
            && Objects.equal(this.createdAt, that.createdAt)
            && Objects.equal(this.deletedAt, that.deletedAt)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.isPublic, that.isPublic)
            && Objects.equal(this.properties, that.properties);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("minDisk", minDisk).add("minRam", minRam).add("location", location).add("owner", owner).add("updatedAt", updatedAt).add("createdAt", createdAt).add("deletedAt", deletedAt).add("status", status).add("isPublic", isPublic).add("properties", properties);
   }

}
