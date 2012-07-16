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

import static org.jclouds.openstack.glance.v1_0.options.ImageField.CHECKSUM;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.CONTAINER_FORMAT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.DISK_FORMAT;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.IS_PUBLIC;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.LOCATION;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.MIN_DISK;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.MIN_RAM;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.NAME;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.OWNER;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.PROPERTY;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.PROTECTED;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.SIZE;
import static org.jclouds.openstack.glance.v1_0.options.ImageField.STORE;

import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.StoreType;

/**
 * <h2></h2>Usage</h2> The recommended way to instantiate a UpdateImageOptions object is to statically import
 * UpdateImageOptions.Builder.* and invoke a static creation method for each option as needed:
 * <p/>
 * <code>
 * import static org.jclouds.openstack.glance.v1_0.options.UpdateImageOptions.Builder.*
 *
 *
 * // this will adjust the image with id 'id' the name "newName", minimum required disk of 5GB, etc. 
 * details = api.update(id, name("newName"), minDisk(5), isPublic(true), property("mykey", "somevalue"));
 * <code>
 * @author Adam Lowe
 * @see <a href="http://glance.openstack.org/glanceapi.html"/>
 */
public class UpdateImageOptions extends BaseHttpRequestOptions {

   /**
    * Adjust the name of the image
    */
   public UpdateImageOptions name(String name) {
      headers.put(NAME.asHeader(), name);
      return this;
   }
   
   /**
    * When present, Glance will attempt to store the disk image data in the backing store indicated by the value of the
    * header. If the Glance node does not support the backing store, Glance will return a 400 Bad Request.
    */
   public UpdateImageOptions storeType(StoreType storeType) {
      headers.put(STORE.asHeader(), storeType.toString());
      return this;
   }

   public UpdateImageOptions diskFormat(DiskFormat diskFormat) {
      headers.put(DISK_FORMAT.asHeader(), diskFormat.toString());
      return this;
   }

   public UpdateImageOptions containerFormat(ContainerFormat containerFormat) {
      headers.put(CONTAINER_FORMAT.asHeader(), containerFormat.toString());
      return this;
   }

   /**
    * When present, Glance assumes that the expected size of the request body will be the value of this header. If the
    * length in bytes of the request body does not match the value of this header, Glance will return a 400 Bad Request.
    */
   public UpdateImageOptions size(long size) {
      headers.put(SIZE.asHeader(), Long.toString(size));
      return this;
   }

   /**
    * MD5 checksum of the image
    * <p/>
    * When present, Glance will verify the checksum generated from the backend store when storing your image against
    * this value and return a 400 Bad Request if the values do not match.
    */
   public UpdateImageOptions checksum(String checksum) {
      headers.put(CHECKSUM.asHeader(), checksum);
      return this;
   }

   public UpdateImageOptions location(String location) {
      headers.put(LOCATION.asHeader(), location);
      return this;
   }

   /**
    * Mark the image as public, meaning that any user may view its metadata and may read the disk image
    * from Glance.
    */
   public UpdateImageOptions isPublic(boolean isPublic) {
      headers.put(IS_PUBLIC.asHeader(), Boolean.toString(isPublic));
      return this;
   }

   /**
    * Mark the image as protected - if set to true the image cannot be deleted till it is unset.
    */
   public UpdateImageOptions isProtected(boolean isProtected) {
      headers.put(PROTECTED.asHeader(), Boolean.toString(isProtected));
      return this;
   }

   /**
    * The expected minimum ram required in megabytes to run this image on a server (default 0).
    */
   public UpdateImageOptions minRam(long ram) {
      headers.put(MIN_RAM.asHeader(), Long.toString(ram));
      return this;
   }

   /**
    * The expected minimum disk required in gigabytes to run this image on a server (default 0).
    */
   public UpdateImageOptions minDisk(long disk) {
      headers.put(MIN_DISK.asHeader(), Long.toString(disk));
      return this;
   }

   /**
    * Glance normally sets the owner of an image to be the tenant or user (depending on the “owner_is_tenant”
    * configuration option) of the authenticated user issuing the request. However, if the authenticated user has the
    * Admin role, this default may be overridden by setting this header to null or to a string identifying the owner of
    * the image.
    */
   public UpdateImageOptions owner(String owner) {
      headers.put(OWNER.asHeader(), owner);
      return this;
   }

   /**
    * Custom, free-form image properties stored with the image.
    */
   public UpdateImageOptions property(String key, String value) {
      if (!key.toLowerCase().startsWith(PROPERTY.asHeader() + "-")) {
         key = PROPERTY.asHeader() + "-" + key;
      }
      headers.put(key, value);
      return this;
   }

   public static class Builder {
      /**
       * @see UpdateImageOptions#name
       */
      public static UpdateImageOptions name(String name) {
         return new UpdateImageOptions().name(name);
      }

      /**
       * @see UpdateImageOptions#storeType
       */
      public static UpdateImageOptions storeType(StoreType storeType) {
         return new UpdateImageOptions().storeType(storeType);
      }

      /**
       * @see UpdateImageOptions#diskFormat
       */
      public static UpdateImageOptions diskFormat(DiskFormat diskFormat) {
         return new UpdateImageOptions().diskFormat(diskFormat);
      }

      /**
       * @see UpdateImageOptions#containerFormat
       */
      public static UpdateImageOptions containerFormat(ContainerFormat containerFormat) {
         return new UpdateImageOptions().containerFormat(containerFormat);
      }

      /**
       * @see UpdateImageOptions#size
       */
      public static UpdateImageOptions size(long size) {
         return new UpdateImageOptions().size(size);
      }

      /**
       * @see UpdateImageOptions#checksum
       */
      public static UpdateImageOptions checksum(String checksum) {
         return new UpdateImageOptions().checksum(checksum);
      }

      /**
       * @see UpdateImageOptions#location
       */
      public static UpdateImageOptions location(String location) {
         return new UpdateImageOptions().location(location);
      }

      /**
       * @see UpdateImageOptions#isPublic
       */
      public static UpdateImageOptions isPublic(boolean isPublic) {
         return new UpdateImageOptions().isPublic(isPublic);
      }

      /**
       * @see UpdateImageOptions#isProtected
       */
      public static UpdateImageOptions isProtected(boolean isProtected) {
         return new UpdateImageOptions().isProtected(isProtected);
      }

      /**
       * @see UpdateImageOptions#minRam
       */
      public static UpdateImageOptions minRam(long ram) {
         return new UpdateImageOptions().minRam(ram);
      }

      /**
       * @see UpdateImageOptions#minDisk
       */
      public static UpdateImageOptions minDisk(long disk) {
         return new UpdateImageOptions().minDisk(disk);
      }

      /**
       * @see UpdateImageOptions#owner
       */
      public static UpdateImageOptions owner(String owner) {
         return new UpdateImageOptions().owner(owner);
      }

      /**
       * @see UpdateImageOptions#property
       */
      public static UpdateImageOptions property(String key, String value) {
         return new UpdateImageOptions().property(key, value);
      }
   }
}
