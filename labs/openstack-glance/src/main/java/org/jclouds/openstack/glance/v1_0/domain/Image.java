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

import java.beans.ConstructorProperties;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * An image the Glance server knows about
 *
 * @author Adrian Cole
 * @see <a href= "http://glance.openstack.org/glanceapi.html" />
 * @see <a href= "https://github.com/openstack/glance/blob/master/glance/api/v1/images.py" />
 */
public class Image extends Resource {

   /**
    */
   public static enum Status {

      UNRECOGNIZED, ACTIVE, SAVING, QUEUED, KILLED, PENDING_DELETE, DELETED;

      public String value() {
         return name();
      }

      public static Status fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromImage(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      protected ContainerFormat containerFormat;
      protected DiskFormat diskFormat;
      protected Long size;
      protected String checksum;

      /**
       * @see Image#getContainerFormat()
       */
      public T containerFormat(ContainerFormat containerFormat) {
         this.containerFormat = containerFormat;
         return self();
      }

      /**
       * @see Image#getDiskFormat()
       */
      public T diskFormat(DiskFormat diskFormat) {
         this.diskFormat = diskFormat;
         return self();
      }

      /**
       * @see Image#getSize()
       */
      public T size(Long size) {
         this.size = size;
         return self();
      }

      /**
       * @see Image#getChecksum()
       */
      public T checksum(String checksum) {
         this.checksum = checksum;
         return self();
      }

      public Image build() {
         return new Image(id, name, links, containerFormat, diskFormat, size, checksum);
      }

      public T fromImage(Image in) {
         return super.fromResource(in)
               .containerFormat(in.getContainerFormat().orNull())
               .diskFormat(in.getDiskFormat().orNull())
               .size(in.getSize().orNull())
               .checksum(in.getChecksum().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("container_format")
   private final Optional<ContainerFormat> containerFormat;
   @Named("disk_format")
   private final Optional<DiskFormat> diskFormat;
   private final Optional<Long> size;
   private final Optional<String> checksum;

   @ConstructorProperties({
         "id", "name", "links", "container_format", "disk_format", "size", "checksum"
   })
   protected Image(String id, @Nullable String name, Set<Link> links, @Nullable ContainerFormat containerFormat,
                   @Nullable DiskFormat diskFormat, @Nullable Long size, @Nullable String checksum) {
      super(id, name, links);
      this.containerFormat = Optional.fromNullable(containerFormat);
      this.diskFormat = Optional.fromNullable(diskFormat);
      this.size = Optional.fromNullable(size);
      this.checksum = Optional.fromNullable(checksum);
   }

   public Optional<ContainerFormat> getContainerFormat() {
      return this.containerFormat;
   }

   public Optional<DiskFormat> getDiskFormat() {
      return this.diskFormat;
   }

   public Optional<Long> getSize() {
      return this.size;
   }

   public Optional<String> getChecksum() {
      return this.checksum;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), containerFormat, diskFormat, size, checksum);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Image that = Image.class.cast(obj);
      return super.equals(that) && Objects.equal(this.containerFormat, that.containerFormat)
            && Objects.equal(this.diskFormat, that.diskFormat)
            && Objects.equal(this.size, that.size)
            && Objects.equal(this.checksum, that.checksum);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("containerFormat", containerFormat).add("diskFormat", diskFormat).add("size", size).add("checksum", checksum);
   }

}
