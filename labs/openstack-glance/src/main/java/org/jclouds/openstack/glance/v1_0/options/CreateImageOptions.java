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

import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.StoreType;

/**
 * 
 * <h2></h2>Usage</h2> The recommended way to instantiate a CreateImageOptions object is to statically import
 * CreateImageOptions.Builder.* and invoke a static creation method for each option as needed:
 * <p/>
 * <code>
 * import static org.jclouds.openstack.glance.v1_0.options.CreateImageOptions.Builder.*
 *
 *
 * // this will create an image with the name "imageName", minimum required disk of 10GB, etc. 
 * details = api.create("imageName", minDisk(10), isPublic(true), property("mykey", "somevalue"));
 * <code>

 * @author Adam Lowe
 * @see <a href="http://glance.openstack.org/glanceapi.html"/>
 */
public class CreateImageOptions extends UpdateImageOptions {

   /**
    * When present, Glance will use the supplied identifier for the image instead of generating one. If the identifier
    * already exists in that Glance node, then a 409 Conflict will be returned by Glance. The value of the header must
    * be a uuid in hexadecimal string notation (i.e. 71c675ab-d94f-49cd-a114-e12490b328d9).
    */
   public CreateImageOptions id(String id) {
      headers.put(ImageField.ID.asHeader(), id);
      return this;
   }

   public static class Builder {
      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#id
       */
      public static CreateImageOptions id(String id) {
         return new CreateImageOptions().id(id);
      }
      
      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#storeType
       */
      public static CreateImageOptions storeType(StoreType storeType) {
         return CreateImageOptions.class.cast(new CreateImageOptions().storeType(storeType));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#diskFormat
       */
      public static CreateImageOptions diskFormat(DiskFormat diskFormat) {
         return CreateImageOptions.class.cast(new CreateImageOptions().diskFormat(diskFormat));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#containerFormat
       */
      public static CreateImageOptions containerFormat(ContainerFormat containerFormat) {
         return CreateImageOptions.class.cast(new CreateImageOptions().containerFormat(containerFormat));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#size
       */
      public static CreateImageOptions size(long size) {
         return CreateImageOptions.class.cast(new CreateImageOptions().size(size));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#checksum
       */
      public static CreateImageOptions checksum(String checksum) {
         return CreateImageOptions.class.cast(new CreateImageOptions().checksum(checksum));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#isPublic
       */
      public static CreateImageOptions isPublic(boolean isPublic) {
         return CreateImageOptions.class.cast(new CreateImageOptions().isPublic(isPublic));
      }
      
      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#isProtected
       */
      public static CreateImageOptions isProtected(boolean isProtected) {
         return CreateImageOptions.class.cast(new CreateImageOptions().isProtected(isProtected));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#minRam
       */
      public static CreateImageOptions minRam(long ram) {
         return CreateImageOptions.class.cast(new CreateImageOptions().minRam(ram));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#minDisk
       */
      public static CreateImageOptions minDisk(long disk) {
         return CreateImageOptions.class.cast(new CreateImageOptions().minDisk(disk));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#owner
       */
      public static CreateImageOptions owner(String owner) {
         return CreateImageOptions.class.cast(new CreateImageOptions().owner(owner));
      }

      /**
       * @see org.jclouds.openstack.glance.v1_0.options.CreateImageOptions#property
       */
      public static CreateImageOptions property(String key, String value) {
         return CreateImageOptions.class.cast(new CreateImageOptions().property(key, value));
      }
   }
}
