/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.elasticstack.domain;

import org.jclouds.compute.domain.OsFamily;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public class WellKnownImage {
   private String loginUser = "toor";
   private String uuid;
   private String description;
   private OsFamily osFamily;
   private String osVersion;
   private int size;
   private boolean is64bit = true;

   // intended only for serialization
   WellKnownImage() {

   }

   // performance isn't a concern on a infrequent object like this, so using shortcuts;

   public String getUuid() {
      return uuid;
   }

   public String getDescription() {
      return description;
   }

   public OsFamily getOsFamily() {
      return osFamily;
   }

   public String getOsVersion() {
      return osVersion;
   }

   public int getSize() {
      return size;
   }

   public boolean is64bit() {
      return is64bit;
   }

   public String getLoginUser() {
      return loginUser;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(uuid, description, osFamily, osVersion, size, is64bit, loginUser);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("uuid", uuid).add("description", description).add("osFamily", osFamily)
               .add("osVersion", osVersion).add("size", size).add("is64bit", is64bit).add("loginUser", loginUser)
               .toString();
   }

}