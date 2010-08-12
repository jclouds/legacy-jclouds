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

package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

/**
 * @author Adrian Cole
 */
public class ImageImpl extends ComputeMetadataImpl implements Image {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7856744554191025307L;

   private final String version;
   private final String description;
   private final OsFamily osFamily;
   private final String osDescription;
   private final Architecture architecture;
   private final Credentials defaultCredentials;

   public ImageImpl(String providerId, String name, String id, Location location, URI uri,
            Map<String, String> userMetadata, String description, String version,
            @Nullable OsFamily osFamily, String osDescription, Architecture architecture,
            Credentials defaultCredentials) {
      super(ComputeType.IMAGE, providerId, name, id, location, uri, userMetadata);
      this.version = checkNotNull(version, "version");
      this.osFamily = osFamily;
      this.description = checkNotNull(description, "description");
      this.osDescription = checkNotNull(osDescription, "osDescription");
      this.architecture = checkNotNull(architecture, "architecture");
      this.defaultCredentials = defaultCredentials;
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
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OsFamily getOsFamily() {
      return osFamily;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getOsDescription() {
      return osDescription;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Architecture getArchitecture() {
      return architecture;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Credentials getDefaultCredentials() {
      return defaultCredentials;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", providerId=" + getProviderId() + ", name=" + getName()
               + ", locationId=" + (getLocation() != null ? getLocation().getId() : "null")
               + ", architecture=" + architecture + ", osDescription=" + osDescription
               + ", version=" + version + ", osFamily=" + osFamily + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((architecture == null) ? 0 : architecture.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((osDescription == null) ? 0 : osDescription.hashCode());
      result = prime * result + ((osFamily == null) ? 0 : osFamily.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
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
      if (osDescription == null) {
         if (other.osDescription != null)
            return false;
      } else if (!osDescription.equals(other.osDescription))
         return false;
      if (osFamily == null) {
         if (other.osFamily != null)
            return false;
      } else if (!osFamily.equals(other.osFamily))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

}
