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

package org.jclouds.byon;

import java.util.Set;

import com.google.common.base.Objects;

/**
 * This would be replaced with the real java object related to the underlying server
 * 
 * @author Adrian Cole
 */
public class Node {
   private String id;
   private String description;
   private String hostname;
   private String osArch;
   private String osFamily;
   private String osName;
   private String osVersion;
   private Set<String> tags;
   private String username;
   private String credential;

   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public String getHostname() {
      return hostname;
   }

   public String getOsArch() {
      return osArch;
   }

   public String getOsFamily() {
      return osFamily;
   }

   public String getOsName() {
      return osName;
   }

   public String getOsVersion() {
      return osVersion;
   }

   public Set<String> getTags() {
      return tags;
   }

   public String getUsername() {
      return username;
   }

   public String getCredential() {
      return credential;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("description", description).add("hostname", hostname)
            .add("osArch", osArch).add("osFamily", osFamily).add("osName", osName).add("osVersion", osVersion)
            .add("tags", tags).add("username", username).toString();
   }

}