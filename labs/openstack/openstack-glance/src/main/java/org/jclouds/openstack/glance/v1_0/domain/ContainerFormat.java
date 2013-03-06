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
 * The container format refers to whether the virtual machine image is in a file format that also
 * contains metadata about the actual virtual machine.
 * 
 * <h3>Note</h3>
 * 
 * Note that the container format string is not currently used by Glance or other OpenStack
 * components, so it is safe to simply specify {@link #BARE} as the container format if you are
 * unsure.
 * 
 * @author Adrian Cole
 * @see <a href= "http://glance.openstack.org/formats.html" />
 */
public enum ContainerFormat {
   /**
    * This indicates there is no container or metadata envelope for the image
    */
   BARE,

   /**
    * This is the OVF container format
    */
   OVF,

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

   public static ContainerFormat fromValue(String containerFormat) {
      try {
         return valueOf(checkNotNull(containerFormat, "containerFormat").toUpperCase());
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
