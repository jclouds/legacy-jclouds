/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.compute.domain;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;

/**
 * 
 * @author Adrian Cole
 */
public class CloudServersImage implements Image {
   private final org.jclouds.rackspace.cloudservers.domain.Image image;
   private final String location;
   private final Architecture architecture;
   private final OsFamily os;
   private final String osDescription;
   private final String version;

   public CloudServersImage(org.jclouds.rackspace.cloudservers.domain.Image image, String location,
            Architecture architecture, OsFamily os, String osDescription, String version) {
      this.location = location;
      this.architecture = architecture;
      this.image = image;
      this.os = os;
      this.osDescription = osDescription;
      this.version = version;
   }

   @Override
   public Architecture getArchitecture() {
      return architecture;
   }

   @Override
   public String getDescription() {
      return getImage().getName();
   }

   @Override
   public String getId() {
      return getImage().getId()+"";
   }

   @Override
   public String getLocation() {
      return location;
   }

   @Override
   public OsFamily getOsFamily() {
      return os;
   }

   @Override
   public String getOsDescription() {
      return osDescription;
   }

   @Override
   public String getVersion() {
      return version;
   }

   public org.jclouds.rackspace.cloudservers.domain.Image getImage() {
      return image;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((architecture == null) ? 0 : architecture.hashCode());
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((osDescription == null) ? 0 : osDescription.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      CloudServersImage other = (CloudServersImage) obj;
      if (architecture == null) {
         if (other.architecture != null)
            return false;
      } else if (!architecture.equals(other.architecture))
         return false;
      if (image == null) {
         if (other.image != null)
            return false;
      } else if (!image.equals(other.image))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (osDescription == null) {
         if (other.osDescription != null)
            return false;
      } else if (!osDescription.equals(other.osDescription))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", version=" + version + ", location=" + getLocation()
               + ", architecture=" + getArchitecture() + ", operatingSystem="
               + getOsFamily() + ", operatingSystemVersion=" + getOsDescription()
               + ", description=" + getDescription() + "]";
   }

}
