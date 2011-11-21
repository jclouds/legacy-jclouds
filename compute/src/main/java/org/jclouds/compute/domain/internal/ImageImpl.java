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
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.javax.annotation.Nullable;

/**
 * @author Adrian Cole
 */
public class ImageImpl extends ComputeMetadataImpl implements Image {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7856744554191025307L;

   private final OperatingSystem operatingSystem;
   private final String version;
   private final String description;
   private final LoginCredentials defaultCredentials;

   /**
    * <h4>will be removed in jclouds 1.4.0</h4>
    */
   @Deprecated
   public ImageImpl(String providerId, String name, String id, Location location, URI uri,
         Map<String, String> userMetadata, Set<String> tags, OperatingSystem operatingSystem, String description,
         @Nullable String version, @Nullable String adminPassword, @Nullable Credentials defaultCredentials) {
      super(ComputeType.IMAGE, providerId, name, id, location, uri, userMetadata, tags);
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      this.version = version;
      this.description = checkNotNull(description, "description");
      Builder builder = LoginCredentials.builder(defaultCredentials);
      if (adminPassword != null) {
         builder.authenticateSudo(true);
         builder.password(adminPassword);
      }
      this.defaultCredentials = builder.build();
   }

   public ImageImpl(String providerId, String name, String id, Location location, URI uri,
         Map<String, String> userMetadata, Set<String> tags, OperatingSystem operatingSystem, String description,
         @Nullable String version, @Nullable LoginCredentials defaultCredentials) {
      super(ComputeType.IMAGE, providerId, name, id, location, uri, userMetadata, tags);
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
   public LoginCredentials getDefaultCredentials() {
      return defaultCredentials;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Deprecated
   public String getAdminPassword() {
      return (defaultCredentials != null && defaultCredentials.shouldAuthenticateSudo()) ? defaultCredentials
            .getPassword() : null;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", name=" + getName() + ", operatingSystem=" + operatingSystem + ", description="
            + description + ", version=" + version + ", location=" + getLocation() + ", loginUser="
            + ((defaultCredentials != null) ? defaultCredentials.identity : null) + ", userMetadata="
            + getUserMetadata() + ", tags=" + tags + "]";
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
