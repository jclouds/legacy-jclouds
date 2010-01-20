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
package org.jclouds.compute.domain.internal;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;

/**
 * @author Adrian Cole
 */
public class ImageImpl implements Image {

   private final String id;
   private final String description;
   private final OperatingSystem operatingSystem;
   private final String version;
   private final String location;
   private final Architecture architecture;

   public ImageImpl(String id, String description, OperatingSystem operatingSystem, String version,
            String location, Architecture architecture) {
      this.id = id;
      this.description = description;
      this.operatingSystem = operatingSystem;
      this.version = version;
      this.location = location;
      this.architecture = architecture;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((architecture == null) ? 0 : architecture.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ImageImpl other = (ImageImpl) obj;
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
      if (operatingSystem == null) {
         if (other.operatingSystem != null)
            return false;
      } else if (!operatingSystem.equals(other.operatingSystem))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "ImageImpl [architecture=" + architecture + ", description=" + description + ", id="
               + id + ", location=" + location + ", operatingSystem=" + operatingSystem
               + ", version=" + version + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVersion() {
      return version;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getLocation() {
      return location;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Architecture getArchitecture() {
      return architecture;
   }

}
