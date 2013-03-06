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
package org.jclouds.openstack.glance.v1_0.options;

import static org.jclouds.openstack.glance.v1_0.options.ImageField.CONTAINER_FORMAT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.DISK_FORMAT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.IS_PUBLIC;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.MIN_DISK;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.MIN_RAM;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.NAME;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.PROTECTED;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.SIZE_MAX;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.SIZE_MIN;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.STATUS;

import java.util.Date;

import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image.Status;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * <h2></h2>Usage</h2> The recommended way to instantiate a ListImageOptions object is to statically import
 * ListImageOptions.Builder.* and invoke a static creation method for each option as needed:
 * <p/>
 * <code>
 * import static org.jclouds.openstack.glance.v1_0.options.ListImageOptions.Builder.*
 *
 *
 * // this will list the first 10 images with the name "name", minimum required disk of 5GB.
 * list = api.list(name("newName"), limit(10), minDisk(5));
 * <code>
 * 
 * @author Adam Lowe
 * @see <a href="http://glance.openstack.org/glanceapi.html"/>
 */
public class ListImageOptions extends PaginationOptions {
   public static final ListImageOptions NONE = new ListImageOptions();

   /**
    * Return only those images having a matching name attribute
    */
   public ListImageOptions name(String name) {
      queryParameters.put(NAME.asParam(), name);
      return this;
   }

   /**
    * Return only those images that have the requested status
    */
   public ListImageOptions status(Status status) {
      queryParameters.put(STATUS.asParam(), status.toString());
      return this;
   }

   /**
    * Return only those images having a matching container format
    */
   public ListImageOptions containerFormat(ContainerFormat containerFormat) {
      queryParameters.put(CONTAINER_FORMAT.asParam(), containerFormat.toString());
      return this;
   }

   /**
    * Return only those images having a matching disk format
    */
   public ListImageOptions diskFormat(DiskFormat diskFormat) {
      queryParameters.put(DISK_FORMAT.asParam(), diskFormat.toString());
      return this;
   }

   /**
    * Return only those images having a matching min ram size
    */
   public ListImageOptions minRam(long ram) {
      queryParameters.put(MIN_RAM.asParam(), Long.toString(ram));
      return this;
   }

   /**
    * Return only those images having a matching min disk size
    */
   public ListImageOptions minDisk(long disk) {
      queryParameters.put(MIN_DISK.asParam(), Long.toString(disk));
      return this;
   }

   /**
    * Return those images that have a size attribute greater than or equal to size
    */
   public ListImageOptions minSize(long size) {
      queryParameters.put(SIZE_MIN.asParam(), Long.toString(size));
      return this;
   }

   /**
    * Return those images that have a size attribute less than or equal to size
    */
   public ListImageOptions maxSize(long size) {
      queryParameters.put(SIZE_MAX.asParam(), Long.toString(size));
      return this;
   }

   /**
    * Return only public images or only private images
    */
   public ListImageOptions isPublic(boolean isPublic) {
      queryParameters.put(IS_PUBLIC.asParam(), Boolean.toString(isPublic));
      return this;
   }

   /**
    * Filter to only protected or unprotected images
    */
   public ListImageOptions isProtected(boolean isProtected) {
      queryParameters.put(PROTECTED.asParam(), Boolean.toString(isProtected));
      return this;
   }

   /**
    * Results will be ordered by the specified image attribute.
    */
   public ListImageOptions sortBy(ImageField key) {
      queryParameters.put("sort_key", key.asParam());
      return this;
   }

   /**
    * Ascending sort order (smallest first).
    * <p/>
    * NOTE: default behavior is to sort descending (largest first)
    */
   public ListImageOptions sortAscending() {
      queryParameters.put("sort_dir", "asc");
      return this;
   }

   public static class Builder {
      /**
       * @see ListImageOptions#name
       */
      public static ListImageOptions name(String name) {
         return new ListImageOptions().name(name);
      }

      /**
       * @see ListImageOptions#diskFormat
       */
      public static ListImageOptions diskFormat(DiskFormat diskFormat) {
         return new ListImageOptions().diskFormat(diskFormat);
      }

      /**
       * @see ListImageOptions#containerFormat
       */
      public static ListImageOptions containerFormat(ContainerFormat containerFormat) {
         return new ListImageOptions().containerFormat(containerFormat);
      }

      /**
       * @see ListImageOptions#minRam
       */
      public static ListImageOptions minRam(long size) {
         return new ListImageOptions().minRam(size);
      }


      /**
       * @see ListImageOptions#minDisk
       */
      public static ListImageOptions minDisk(long size) {
         return new ListImageOptions().minDisk(size);
      }

      /**
       * @see ListImageOptions#minSize
       */
      public static ListImageOptions minSize(long size) {
         return new ListImageOptions().minSize(size);
      }

      /**
       * @see ListImageOptions#maxSize
       */
      public static ListImageOptions maxSize(long size) {
         return new ListImageOptions().maxSize(size);
      }

      /**
       * @see ListImageOptions#sortBy
       */
      public static ListImageOptions status(Status status) {
         return new ListImageOptions().status(status);
      }

      /**
       * @see ListImageOptions#sortBy
       */
      public static ListImageOptions sortBy(ImageField sortKey) {
         return new ListImageOptions().sortBy(sortKey);
      }

      /**
       * @see ListImageOptions#sortAscending
       */
      public static ListImageOptions sortAscending() {
         return new ListImageOptions().sortAscending();
      }

      /**
       * @see ListImageOptions#isPublic
       */
      public static ListImageOptions isPublic(boolean isPublic) {
         return ListImageOptions.class.cast(new ListImageOptions().isPublic(isPublic));
      }

      /**
       * @see ListImageOptions#isProtected
       */
      public static ListImageOptions isProtected(boolean isProtected) {
         return ListImageOptions.class.cast(new ListImageOptions().isProtected(isProtected));
      }

      /**
       * @see ListImageOptions#limit
       */
      public static ListImageOptions limit(int limit) {
         return new ListImageOptions().limit(limit);
      }

      /**
       * @see ListImageOptions#marker
       */
      public static ListImageOptions marker(String marker) {
         return new ListImageOptions().marker(marker);
      }
   }

   @Override
   public ListImageOptions changesSince(Date ifModifiedSince) {
      return ListImageOptions.class.cast(super.changesSince(ifModifiedSince));
   }

   @Override
   public ListImageOptions marker(String marker) {
      return ListImageOptions.class.cast(super.marker(marker));
   }

   @Override
   public ListImageOptions limit(int limit) {
      return ListImageOptions.class.cast(super.limit(limit));
   }
}
