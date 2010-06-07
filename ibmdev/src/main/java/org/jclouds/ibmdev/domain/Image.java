/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.domain;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 
 * The current state of the image.
 * 
 * @author Adrian Cole
 */
public class Image {
   public enum Visibility {

      PUBLIC,

      SHARED,

      PRIVATE;
   }

   private String name;
   /**
    * Note that this isn't a URI, as parsing fails due to IBM including '{' characters in the path.
    */
   private String manifest;
   private int state;
   private Visibility visibility;
   private String owner;
   private String architecture;
   private String platform;
   private long createdTime;
   private long location;
   private Set<String> supportedInstanceTypes = Sets.newLinkedHashSet();
   private Set<String> productCodes = Sets.newLinkedHashSet();
   /**
    * Note that this isn't a URI, as parsing fails due to IBM including '{' characters in the path.
    */
   private String documentation;
   private long id;
   private String description;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getManifest() {
      return manifest;
   }

   public void setManifest(String manifest) {
      this.manifest = manifest;
   }

   public int getState() {
      return state;
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

   public String getArchitecture() {
      return architecture;
   }

   public void setArchitecture(String architecture) {
      this.architecture = architecture;
   }

   public String getPlatform() {
      return platform;
   }

   public void setPlatform(String platform) {
      this.platform = platform;
   }

   public long getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(long createdTime) {
      this.createdTime = createdTime;
   }

   public long getLocation() {
      return location;
   }

   public void setLocation(long location) {
      this.location = location;
   }

   public Set<String> getSupportedInstanceTypes() {
      return supportedInstanceTypes;
   }

   public void setSupportedInstanceTypes(Set<String> supportedInstanceTypes) {
      this.supportedInstanceTypes = supportedInstanceTypes;
   }

   public Set<String> getProductCodes() {
      return productCodes;
   }

   public void setProductCodes(Set<String> productCodes) {
      this.productCodes = productCodes;
   }

   public String getDocumentation() {
      return documentation;
   }

   public void setDocumentation(String documentation) {
      this.documentation = documentation;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
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
      result = prime * result + ((architecture == null) ? 0 : architecture.hashCode());
      result = prime * result + (int) (createdTime ^ (createdTime >>> 32));
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((documentation == null) ? 0 : documentation.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (int) (location ^ (location >>> 32));
      result = prime * result + ((manifest == null) ? 0 : manifest.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((platform == null) ? 0 : platform.hashCode());
      result = prime * result + ((productCodes == null) ? 0 : productCodes.hashCode());
      result = prime * result + state;
      result = prime * result
               + ((supportedInstanceTypes == null) ? 0 : supportedInstanceTypes.hashCode());
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
      if (architecture == null) {
         if (other.architecture != null)
            return false;
      } else if (!architecture.equals(other.architecture))
         return false;
      if (createdTime != other.createdTime)
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
      if (id != other.id)
         return false;
      if (location != other.location)
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
      if (state != other.state)
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
      return "Image [id=" + id + ", name=" + name + ", location=" + location + ", manifest="
               + manifest + ", platform=" + platform + ", state=" + state
               + ", supportedInstanceTypes=" + supportedInstanceTypes + ", visibility="
               + visibility + "]";
   }
}