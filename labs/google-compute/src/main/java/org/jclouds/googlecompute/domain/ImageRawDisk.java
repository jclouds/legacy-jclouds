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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A raw disk image, usually the base for an image.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/images"/>
 */
public class ImageRawDisk {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromImageRawDisk(this);
   }

   public static class Builder {

      private String source;
      private String containerType;
      private String sha1Checksum;

      /**
       * @see ImageRawDisk#getSource()
       */
      public Builder source(String source) {
         this.source = checkNotNull(source);
         return this;
      }

      /**
       * @see ImageRawDisk#getContainerType()
       */
      public Builder containerType(String containerType) {
         this.containerType = checkNotNull(containerType);
         return this;
      }

      /**
       * @see ImageRawDisk#getSha1Checksum()
       */
      public Builder sha1Checksum(String sha1Checksum) {
         this.sha1Checksum = sha1Checksum;
         return this;
      }

      public ImageRawDisk build() {
         return new ImageRawDisk(source, containerType, sha1Checksum);
      }

      public Builder fromImageRawDisk(ImageRawDisk rawDisk) {
         return new Builder().source(rawDisk.getSource()).containerType(rawDisk.getContainerType()).sha1Checksum
                 (rawDisk.getSha1Checksum());
      }
   }

   private final String source;
   private final String containerType;
   private final String sha1Checksum;

   @ConstructorProperties({
           "source", "containerType", "sha1Checksum"
   })
   private ImageRawDisk(String source, String containerType, String sha1Checksum) {
      this.source = checkNotNull(source);
      this.containerType = checkNotNull(containerType);
      this.sha1Checksum = sha1Checksum;
   }

   /**
    * @return the full Google Cloud Storage URL where the disk image is stored; provided by the client when the disk
    *         image is created.
    */
   public String getSource() {
      return source;
   }

   /**
    * @return the format used to encode and transmit the block device. Should be TAR. This is just a container and
    *         transmission format and not a runtime format. Provided by the client when the disk image is created.
    */
   public String getContainerType() {
      return containerType;
   }

   /**
    * @return an optional SHA1 checksum of the disk image before unpackaging; provided by the client when the disk
    *         image is created.
    */
   @Nullable
   public String getSha1Checksum() {
      return sha1Checksum;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(source, containerType, sha1Checksum);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ImageRawDisk that = ImageRawDisk.class.cast(obj);
      return equal(this.source, that.source)
              && equal(this.containerType, that.containerType)
              && equal(this.sha1Checksum, that.sha1Checksum);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("source", source).add("containerType", containerType).add("sha1Checksum", sha1Checksum);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
