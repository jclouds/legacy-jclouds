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
package org.jclouds.ibm.smartcloud.domain;

import java.net.URI;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * 
 * The current state of the image.
 * 
 * @author Adrian Cole
 */
public class Image implements Comparable<Image> {

   public static enum State {
      NEW, AVAILABLE, UNAVAILABLE, DELETED, CAPTURING, UNRECOGNIZED;
      public static State fromValue(String v) {
         switch (Integer.parseInt(v)) {
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
               return UNRECOGNIZED;
         }
      }
   }

   public static enum Visibility {

      PUBLIC,

      SHARED,

      PRIVATE;
   }

   public static enum Architecture {
      I386, X86_64, UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      public static Architecture fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private String name;

   private URI manifest;
   private State state;
   private Visibility visibility;
   private String owner;
   private String platform;
   private Architecture architecture;
   private Date createdTime;
   private String location;
   // for testing to be the same order each time
   private SortedSet<InstanceType> supportedInstanceTypes = Sets.newTreeSet();
   private SortedSet<String> productCodes = Sets.newTreeSet();
   private URI documentation;
   private String id;
   private String description;

   Image() {

   }

   public Image(String name, URI manifest, State state, Visibility visibility, String owner, String platform,
            Architecture architecture, Date createdTime, String location, Set<InstanceType> supportedInstanceTypes,
            Set<String> productCodes, URI documentation, String id, String description) {
      this.name = name;
      this.manifest = manifest;
      this.state = state;
      this.visibility = visibility;
      this.owner = owner;
      this.platform = platform;
      this.architecture = architecture;
      this.createdTime = createdTime;
      this.location = location;
      this.supportedInstanceTypes = ImmutableSortedSet.copyOf(supportedInstanceTypes);
      this.productCodes = ImmutableSortedSet.copyOf(productCodes);
      this.documentation = documentation;
      this.id = id;
      this.description = description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
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
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[id=%s, name=%s, architecture=%s, createdTime=%s, description=%s, documentation=%s, location=%s, manifest=%s, owner=%s, platform=%s, productCodes=%s, state=%s, supportedInstanceTypes=%s, visibility=%s]",
                        id, name, architecture, createdTime, description, documentation, location, manifest, owner,
                        platform, productCodes, state, supportedInstanceTypes, visibility);
   }

   public String getName() {
      return name;
   }

   public URI getManifest() {
      return manifest;
   }

   public State getState() {
      return state;
   }

   public Visibility getVisibility() {
      return visibility;
   }

   public String getOwner() {
      return owner;
   }

   public String getPlatform() {
      return platform;
   }

   public Architecture getArchitecture() {
      return architecture;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public String getLocation() {
      return location;
   }

   public Set<InstanceType> getSupportedInstanceTypes() {
      return supportedInstanceTypes;
   }

   public Set<String> getProductCodes() {
      return productCodes;
   }

   public URI getDocumentation() {
      return documentation;
   }

   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public int compareTo(Image arg0) {
      return id.compareTo(arg0.getId());
   }
}
