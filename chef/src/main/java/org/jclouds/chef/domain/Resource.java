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
package org.jclouds.chef.domain;

import java.net.URI;

/**
 * Cookbook object.
 * 
 * @author Adrian Cole
 */
public class Resource {

   private String name;
   private URI url;
   private String checksum;
   private String path;
   private String specificity;

   public Resource(String name, URI url, String checksum, String path,
         String specificity) {
      this.name = name;
      this.url = url;
      this.checksum = checksum;
      this.path = path;
      this.specificity = specificity;
   }

   public Resource() {
   }

   public String getName() {
      return name;
   }

   public URI getUrl() {
      return url;
   }
   public String getChecksum() {
      return checksum;
   }

   public String getPath() {
      return path;
   }

   public String getSpecificity() {
      return specificity;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((path == null) ? 0 : path.hashCode());
      result = prime * result
            + ((specificity == null) ? 0 : specificity.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Resource other = (Resource) obj;
      if (checksum == null) {
         if (other.checksum != null)
            return false;
      } else if (!checksum.equals(other.checksum))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (path == null) {
         if (other.path != null)
            return false;
      } else if (!path.equals(other.path))
         return false;
      if (specificity == null) {
         if (other.specificity != null)
            return false;
      } else if (!specificity.equals(other.specificity))
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Resource [checksum=" + checksum + ", name=" + name + ", path="
            + path + ", specificity=" + specificity + ", url=" + url + "]";
   }

}