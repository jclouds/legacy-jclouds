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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

/**
 * An image is a platonic form of a machine. Images are not directly executable, but are a template
 * for creating actual instances of machines.
 * 
 * @author Adrian Cole
 */
public class Image {
   private final URI href;
   private final String id;
   private final String ownerId;
   @Nullable
   private final String name;
   @Nullable
   private final String description;
   private final String architecture;

   public Image(URI href, String id, String ownerId, @Nullable String name, String description, String architecture) {
      this.href = checkNotNull(href, "href");
      this.id = checkNotNull(id, "id");
      this.ownerId = checkNotNull(ownerId, "ownerId");
      this.name = name;
      this.description = description;
      this.architecture = checkNotNull(architecture, "architecture");
   }

   /**
    * 
    * @return URL to manipulate a specific image
    */
   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return A unique identifier for the image
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return An opaque identifier which indicates the owner of an image
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * 
    * @return An optional short label describing the image
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * 
    * @return An optional description describing the image more fully
    */
   @Nullable
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return A description of the machine architecture of the image which may contain values such
    *         as: i386, x86_64
    */
   public String getArchitecture() {
      return architecture;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((architecture == null) ? 0 : architecture.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
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
      Image other = (Image) obj;
      if (architecture == null) {
         if (other.architecture != null)
            return false;
      } else if (!architecture.equals(other.architecture))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (ownerId == null) {
         if (other.ownerId != null)
            return false;
      } else if (!ownerId.equals(other.ownerId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", id=" + id + ", ownerId=" + ownerId + ", name=" + name + ", description="
            + description + ", architecture=" + architecture + "]";
   }
}
