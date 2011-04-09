/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev.domain;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 
 * The current state of the image.
 * 
 * @author Adrian Cole
 */
public class Image {

   public static enum State {
      NEW, AVAILABLE, UNAVAILABLE, DELETED, CAPTURING;
      public static State fromValue(int v) {
         switch (v) {
         case 0:
            return NEW;
         case 1:
            return AVAILABLE;
         case 2:
            return UNAVAILABLE;
         case 3:
            return DELETED;
         case 4:
            return CAPTURING;
         default:
            throw new IllegalArgumentException("invalid state:" + v);
         }
      }
   }

   public static enum Visibility {

      PUBLIC,

      SHARED,

      PRIVATE;
   }

   private String name;

   private URI manifest;
   private int state;
   private Visibility visibility;
   private String owner;
   private String platform;
   private Date createdTime;
   private String location;
   private Set<InstanceType> supportedInstanceTypes = Sets.newLinkedHashSet();
   private Set<String> productCodes = Sets.newLinkedHashSet();
   private URI documentation;
   private String id;
   private String description;

   Image() {

   }

   public Image(String name, URI manifest, int state, Visibility visibility, String owner, String platform,
         Date createdTime, String location, Set<InstanceType> supportedInstanceTypes, Set<String> productCodes,
         URI documentation, String id, String description) {
      this.name = name;
      this.manifest = manifest;
      this.state = state;
      this.visibility = visibility;
      this.owner = owner;
      this.platform = platform;
      this.createdTime = createdTime;
      this.location = location;
      this.supportedInstanceTypes = supportedInstanceTypes;
      this.productCodes = productCodes;
      this.documentation = documentation;
      this.id = id;
      this.description = description;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public URI getManifest() {
      return manifest;
   }

   public void setManifest(URI manifest) {
      this.manifest = manifest;
   }

   public State getState() {
      return State.fromValue(state);
   }

   public void setState(int state) {
      this.state = state;
   }

   public Visibility getVisibility() {
      return visibility;
   }

   public void setVisibility(Visibility visibility) {
      this.visibility = visibility;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public String getPlatform() {
      return platform;
   }

   public void setPlatform(String platform) {
      this.platform = platform;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public Set<InstanceType> getSupportedInstanceTypes() {
      return supportedInstanceTypes;
   }

   public void setSupportedInstanceTypes(Set<InstanceType> supportedInstanceTypes) {
      this.supportedInstanceTypes = supportedInstanceTypes;
   }

   public Set<String> getProductCodes() {
      return productCodes;
   }

   public void setProductCodes(Set<String> productCodes) {
      this.productCodes = productCodes;
   }

   public URI getDocumentation() {
      return documentation;
   }

   public void setDocumentation(URI documentation) {
      this.documentation = documentation;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((createdTime == null) ? 0 : createdTime.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((documentation == null) ? 0 : documentation.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((manifest == null) ? 0 : manifest.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((platform == null) ? 0 : platform.hashCode());
      result = prime * result + ((productCodes == null) ? 0 : productCodes.hashCode());
      result = prime * result + ((supportedInstanceTypes == null) ? 0 : supportedInstanceTypes.hashCode());
      result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
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
      if (createdTime == null) {
         if (other.createdTime != null)
            return false;
      } else if (!createdTime.equals(other.createdTime))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (documentation == null) {
         if (other.documentation != null)
            return false;
      } else if (!documentation.equals(other.documentation))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (manifest == null) {
         if (other.manifest != null)
            return false;
      } else if (!manifest.equals(other.manifest))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (platform == null) {
         if (other.platform != null)
            return false;
      } else if (!platform.equals(other.platform))
         return false;
      if (productCodes == null) {
         if (other.productCodes != null)
            return false;
      } else if (!productCodes.equals(other.productCodes))
         return false;
      if (supportedInstanceTypes == null) {
         if (other.supportedInstanceTypes != null)
            return false;
      } else if (!supportedInstanceTypes.equals(other.supportedInstanceTypes))
         return false;
      if (visibility == null) {
         if (other.visibility != null)
            return false;
      } else if (!visibility.equals(other.visibility))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Image [id=" + id + ", name=" + name + ", location=" + location + ", manifest=" + manifest + ", platform="
            + platform + ", state=" + getState() + ", supportedInstanceTypes=" + supportedInstanceTypes
            + ", visibility=" + visibility + "]";
   }
}