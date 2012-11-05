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
package org.jclouds.fujitsu.fgcp.domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a disk image with pre-installed OS and/or software.
 * <p>
 * It is used as base for the system disk of a virtual server.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "diskimage")
public class DiskImage {
   @XmlElement(name = "diskimageId")
   private String id;

   @XmlElement(name = "diskimageName")
   private String name;

   private int size;

   private String osName;

   private String osType;

   private String creatorName;

   private String registrant;

   private String licenseInfo;

   private String description;

   @XmlElementWrapper(name = "softwares")
   @XmlElement(name = "software")
   private Set<Software> software = Sets.newLinkedHashSet();

   public String getId() {
      return id;
   }

   public int getSize() {
      return size;
   }

   public String getOsName() {
      return osName;
   }

   public String getOsType() {
      return osType;
   }

   public String getCreatorName() {
      return creatorName;
   }

   public String getRegistrant() {
      return registrant;
   }

   public String getLicenseInfo() {
      return licenseInfo;
   }

   public String getDescription() {
      return description;
   }

   public String getName() {
      return name;
   }

   public Set<Software> getSoftware() {
      return software == null ? ImmutableSet.<Software> of() : ImmutableSet
            .copyOf(software);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private int size;
      private String osName;
      private String osType;
      private String creatorName;
      private String registrant;
      private String licenseInfo;
      private String description;
      private Set<Software> software;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder osName(String osName) {
         this.osName = osName;
         return this;
      }

      public Builder osType(String osType) {
         this.osType = osType;
         return this;
      }

      public Builder creatorName(String creatorName) {
         this.creatorName = creatorName;
         return this;
      }

      public Builder registrant(String registrant) {
         this.registrant = registrant;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public DiskImage build() {
         DiskImage image = new DiskImage();

         image.id = id;
         image.name = name;
         image.size = size;
         image.osName = osName;
         image.osType = osType;
         image.creatorName = creatorName;
         image.registrant = registrant;
         image.licenseInfo = licenseInfo;
         image.description = description;
         image.software = software;

         return image;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DiskImage that = DiskImage.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("name", name).add("osName", osName).add("osType", osType)
            .add("size", size).add("creatorName", creatorName)
            .add("description", description)
            .add("licenseInfo", licenseInfo).add("registrant", registrant)
            .add("software", software).toString();
   }
}
