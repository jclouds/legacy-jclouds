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

/**
 * The disk format of a virtual machine image is the format of the underlying disk image. Virtual
 * appliance vendors have different formats for laying out the information contained in a virtual
 * machine disk image.
 * 
 * @author Adrian Cole
 * @see <a href= "http://glance.openstack.org/formats.html" />
 */
public enum DiskFormat {
   /**
    * This is an unstructured disk image format
    */
   RAW,
   /**
    * This is the VHD disk format, a common disk format used by virtual machine monitors from
    * VMWare, Xen, Microsoft, VirtualBox, and others
    */
   VHD,
   /**
    * Another common disk format supported by many common virtual machine monitors
    */
   VMDK,
   /**
    * A disk format supported by VirtualBox virtual machine monitor and the QEMU emulator
    */
   VDI,
   /**
    * An archive format for the data contents of an optical disc (e.g. CDROM).
    */
   ISO,
   /**
    * A disk format supported by the QEMU emulator that can expand dynamically and supports Copy on
    * Write
    */
   QCOW2,

   /**
    * This indicates what is stored in Glance is an Amazon kernel image
    */
   AKI,

   /**
    * This indicates what is stored in Glance is an Amazon ramdisk image
    */
   ARI,

   /**
    * This indicates what is stored in Glance is an Amazon machine image
    */
   AMI,

   /**
    * Type unknown to jclouds
    */
   UNRECOGNIZED;

   public String value() {
      return name().toLowerCase();
   }

   @Override
   public String toString() {
      return value();
   }

   public static DiskFormat fromValue(String diskFormat) {
      try {
         return valueOf(checkNotNull(diskFormat, "diskFormat").toUpperCase());
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
