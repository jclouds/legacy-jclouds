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
package org.jclouds.aws.ec2.compute;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Image implements Image {
   private final org.jclouds.aws.ec2.domain.Image image;
   private final OperatingSystem os;
   private final String osVersion;
   private final String version;

   public EC2Image(org.jclouds.aws.ec2.domain.Image image, OperatingSystem os, String osVersion,
            String version) {
      this.image = image;
      this.os = os;
      this.osVersion = osVersion;
      this.version = version;
   }

   @Override
   public Architecture getArchitecture() {
      return getImage().getArchitecture() == org.jclouds.aws.ec2.domain.Image.Architecture.I386 ? Architecture.X86_32
               : Architecture.X86_64;
   }

   @Override
   public String getDescription() {
      return getImage().getDescription();
   }

   @Override
   public String getId() {
      return getImage().getId();
   }

   @Override
   public String getLocation() {
      return getImage().getRegion().toString();
   }

   @Override
   public OperatingSystem getOperatingSystem() {
      return os;
   }

   @Override
   public String getOperatingSystemVersion() {
      return osVersion;
   }

   @Override
   public String getVersion() {
      return version;
   }

   public org.jclouds.aws.ec2.domain.Image getImage() {
      return image;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((osVersion == null) ? 0 : osVersion.hashCode());
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
      EC2Image other = (EC2Image) obj;
      if (image == null) {
         if (other.image != null)
            return false;
      } else if (!image.equals(other.image))
         return false;
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (osVersion == null) {
         if (other.osVersion != null)
            return false;
      } else if (!osVersion.equals(other.osVersion))
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
               + getOperatingSystem() + ", operatingSystemVersion=" + getOperatingSystemVersion()
               + ", description=" + getDescription() + "]";
   }

}
