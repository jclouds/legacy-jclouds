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
package org.jclouds.openstack.nova.v1_1.domain;

/**
 * In-flight images will have the status attribute set to SAVING and the
 * conditional progress element (0-100% completion) will also be returned. Other
 * possible values for the status attribute include: UNKNOWN, ACTIVE, SAVING,
 * ERROR, and DELETED. Images with an ACTIVE status are available for install.
 * The optional minDisk and minRam attributes set the minimum disk and RAM
 * requirements needed to create a server with the image.
 * 
 * @author Adrian Cole
 */
public enum ImageStatus {

   UNRECOGNIZED, UNKNOWN, ACTIVE, SAVING, ERROR, DELETED;

   public String value() {
      return name();
   }

   public static ImageStatus fromValue(String v) {
      try {
         return valueOf(v);
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

}
