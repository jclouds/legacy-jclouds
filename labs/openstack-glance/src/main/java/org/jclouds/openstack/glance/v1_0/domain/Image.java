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

import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.gson.annotations.SerializedName;

/**
 * An image the Glance server knows about
 * 
 * @author Adrian Cole
 * @see <a href= "http://glance.openstack.org/glanceapi.html" />
 * @see <a href= "https://github.com/openstack/glance/blob/master/glance/api/v1/images.py" />
 */
public class Image extends Resource {
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

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T> {
      private Optional<ContainerFormat> containerFormat = Optional.absent();
      private Optional<DiskFormat> diskFormat = Optional.absent();
      private Optional<Long> size = Optional.absent();
      private Optional<String> checksum = Optional.absent();

      /**
       * @see Image#getContainerFormat()
       */
      public T containerFormat(Optional<ContainerFormat> containerFormat) {
         this.containerFormat = containerFormat;
         return self();
      }

      /**
       * @see Image#getDiskFormat()
       */
      public T diskFormat(Optional<DiskFormat> diskFormat) {
         this.diskFormat = diskFormat;
         return self();
      }

      /**
       * @see Image#getSize()
       */
      public T size(Optional<Long> size) {
         this.size = size;
         return self();
      }

      /**
       * @see Image#getSize()
       */
      public T checksum(Optional<String> checksum) {
         this.checksum = checksum;
         return self();
      }

      /**
       * @see Image#getContainerFormat()
       */
      public T containerFormat(ContainerFormat containerFormat) {
         return containerFormat(Optional.of(containerFormat));
      }

      /**
       * @see Image#getDiskFormat()
       */
      public T diskFormat(DiskFormat diskFormat) {
         return diskFormat(Optional.of(diskFormat));
      }

      /**
       * @see Image#getSize()
       */
      public T size(long size) {
         return size(Optional.of(size));
      }

      /**
       * @see Image#getSize()
       */
      public T checksum(String checksum) {
         return checksum(Optional.of(checksum));
      }

      public Image build() {
         return new Image(this);
      }

      public T fromImage(Image in) {
         return super.fromResource(in).containerFormat(in.getContainerFormat()).diskFormat(in.getDiskFormat()).size(
                  in.getSize()).checksum(in.getChecksum());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected Image() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }

   // | container_format | varchar(20) | YES | | NULL | |
   @SerializedName("container_format")
   private Optional<ContainerFormat> containerFormat = Optional.absent();
   // | disk_format | varchar(20) | YES | | NULL | |
   @SerializedName("disk_format")
   private Optional<DiskFormat> diskFormat = Optional.absent();
   // | size | bigint(20) | YES | | NULL | |
   private Optional<Long> size = Optional.absent();
   // | checksum | varchar(32) | YES | | NULL | |
   private Optional<String> checksum = Optional.absent();

   protected Image(Builder<?> builder) {
      super(builder);
      this.containerFormat = builder.containerFormat;
      this.diskFormat = builder.diskFormat;
      this.size = builder.size;
      this.checksum = builder.checksum;
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
      return checksum;
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("containerFormat", containerFormat).add("diskFormat", diskFormat).add("size", size)
               .add("checksum", checksum);
   }
}