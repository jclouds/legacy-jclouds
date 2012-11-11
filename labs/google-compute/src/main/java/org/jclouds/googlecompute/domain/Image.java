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
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a disk image to use on an instance.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/images"/>
 */
public class Image extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromImage(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private String sourceType;
      private String preferredKernel;
      private ImageRawDisk rawDisk;
      private ImageDiskSnapshot diskSnapshot;

      /**
       * @see Image#getSourceType()
       */
      public T sourceType(String sourceType) {
         this.sourceType = checkNotNull(sourceType);
         return self();
      }

      /**
       * @see Image#getPreferredKernel()
       */
      public T preferredKernel(String preferredKernel) {
         this.preferredKernel = preferredKernel;
         return self();
      }

      /**
       * @see Image#getRawDisk()
       */
      public T rawDisk(ImageRawDisk rawDisk) {
         this.rawDisk = checkNotNull(rawDisk);
         return self();
      }

      /**
       * @see Image#getSourceType()
       */
      public T diskSnapshot(ImageDiskSnapshot diskSnaphshot) {
         this.diskSnapshot = checkNotNull(diskSnaphshot);
         return self();
      }

      public Image build() {
         return new Image(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, sourceType, preferredKernel, rawDisk, diskSnapshot);
      }

      public T fromImage(Image in) {
         return super.fromResource(in).sourceType(in.getSourceType()).preferredKernel(in.getPreferredKernel())
                 .rawDisk(in.getRawDisk()).diskSnapshot(in.getDiskSnapshot());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String sourceType;
   private final String preferredKernel;
   private final ImageRawDisk rawDisk;
   private final ImageDiskSnapshot diskSnapshot;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "sourceType", "preferredKernel",
           "rawDisk", "diskSnapshot"
   })
   protected Image(String id, Date creationTimestamp, String selfLink, String name, String description,
                   String sourceType, String preferredKernel, ImageRawDisk rawDisk, ImageDiskSnapshot diskSnapshot) {
      super(Kind.IMAGE, id, creationTimestamp, selfLink, name, description);
      this.sourceType = sourceType;
      this.preferredKernel = preferredKernel;
      this.rawDisk = rawDisk;
      this.diskSnapshot = diskSnapshot;
   }

   /**
    * @return must be RAW; provided by the client when the disk image is created.
    */
   public String getSourceType() {
      return sourceType;
   }

   /**
    * @return An optional URL of the preferred kernel for use with this disk image. If not specified,
    *         a server defined default kernel will be use.
    */
   @Nullable
   public String getPreferredKernel() {
      return preferredKernel;
   }

   /**
    * @return the raw disk image parameters.
    */
   public ImageRawDisk getRawDisk() {
      return rawDisk;
   }

   /**
    * @return the disk snapshot
    */
   public ImageDiskSnapshot getDiskSnapshot() {
      return diskSnapshot;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, sourceType, preferredKernel,
              rawDisk, diskSnapshot);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Image that = Image.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.sourceType, that.sourceType)
              && Objects.equal(this.preferredKernel, that.preferredKernel)
              && Objects.equal(this.rawDisk, that.rawDisk)
              && Objects.equal(this.diskSnapshot, that.diskSnapshot);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("sourceType", sourceType).add("preferredKernel",
                      preferredKernel).add("rawDisk", rawDisk).add("diskSnapshot", diskSnapshot);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

}
