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

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

/**
 * @author Adrian Cole
 */
public class ImageImpl extends ComputeMetadataImpl implements Image {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7856744554191025307L;

   private final OperatingSystem operatingSystem;
   private final String version;
   private final String description;
   private final Credentials defaultCredentials;

   public ImageImpl(String providerId, String name, String id, Location location, URI uri,
         Map<String, String> userMetadata, OperatingSystem operatingSystem, String description,
         @Nullable String version, @Nullable Credentials defaultCredentials) {
      super(ComputeType.IMAGE, providerId, name, id, location, uri, userMetadata);
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      this.version = version;
      this.description = checkNotNull(description, "description");
      this.defaultCredentials = defaultCredentials;
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
   public String getDescription() {
      return description;
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
      return "[id=" + getId() + ", name=" + getName() + ", operatingSystem=" + operatingSystem + ", description="
            + description + ", version=" + version + ", location=" + getLocation() + ", loginUser="
            + ((defaultCredentials != null) ? defaultCredentials.identity : null) + ", userMetadata="
            + getUserMetadata() + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((defaultCredentials == null) ? 0 : defaultCredentials.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
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
      if (defaultCredentials == null) {
         if (other.defaultCredentials != null)
            return false;
      } else if (!defaultCredentials.equals(other.defaultCredentials))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
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

}
