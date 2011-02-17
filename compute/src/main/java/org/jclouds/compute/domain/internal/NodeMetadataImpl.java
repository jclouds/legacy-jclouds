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
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class NodeMetadataImpl extends ComputeMetadataImpl implements NodeMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7924307572338157887L;

   private final NodeState state;
   private final int loginPort;
   private final Set<String> publicAddresses;
   private final Set<String> privateAddresses;
   @Nullable
   private final String adminPassword;
   @Nullable
   private final Credentials credentials;
   @Nullable
   private final String group;
   @Nullable
   private final String imageId;
   @Nullable
   private final Hardware hardware;
   @Nullable
   private final OperatingSystem os;

   public NodeMetadataImpl(String providerId, String name, String id, Location location, URI uri,
            Map<String, String> userMetadata, @Nullable String group, @Nullable Hardware hardware,
            @Nullable String imageId, @Nullable OperatingSystem os, NodeState state, int loginPort,
            Iterable<String> publicAddresses, Iterable<String> privateAddresses, @Nullable String adminPassword,
            @Nullable Credentials credentials) {
      super(ComputeType.NODE, providerId, name, id, location, uri, userMetadata);
      this.group = group;
      this.hardware = hardware;
      this.imageId = imageId;
      this.os = os;
      this.state = checkNotNull(state, "state");
      this.loginPort = loginPort;
      this.publicAddresses = ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses"));
      this.privateAddresses = ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses"));
      this.adminPassword = adminPassword;
      this.credentials = credentials;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTag() {
      return getGroup();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getGroup() {
      return group;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Hardware getHardware() {
      return hardware;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getAdminPassword() {
      return adminPassword;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Credentials getCredentials() {
      return credentials;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getPublicAddresses() {
      return publicAddresses;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getPrivateAddresses() {
      return privateAddresses;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeState getState() {
      return state;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getLoginPort() {
      return this.loginPort;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getImageId() {
      return imageId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OperatingSystem getOperatingSystem() {
      return os;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", providerId=" + getProviderId() + ", group=" + getTag() + ", name=" + getName()
               + ", location=" + getLocation() + ", uri=" + getUri() + ", imageId=" + getImageId() + ", os="
               + getOperatingSystem() + ", state=" + getState() + ", loginPort=" + getLoginPort()
               + ", privateAddresses=" + privateAddresses + ", publicAddresses=" + publicAddresses + ", hardware="
               + getHardware() + ", loginUser=" + ((credentials != null) ? credentials.identity : null)
               + ", userMetadata=" + getUserMetadata() + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + loginPort;
      result = prime * result + ((privateAddresses == null) ? 0 : privateAddresses.hashCode());
      result = prime * result + ((publicAddresses == null) ? 0 : publicAddresses.hashCode());
      result = prime * result + ((group == null) ? 0 : group.hashCode());
      result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
      result = prime * result + ((hardware == null) ? 0 : hardware.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((adminPassword == null) ? 0 : adminPassword.hashCode());
      result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
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
      NodeMetadataImpl other = (NodeMetadataImpl) obj;
      if (loginPort != other.loginPort)
         return false;
      if (privateAddresses == null) {
         if (other.privateAddresses != null)
            return false;
      } else if (!privateAddresses.equals(other.privateAddresses))
         return false;
      if (publicAddresses == null) {
         if (other.publicAddresses != null)
            return false;
      } else if (!publicAddresses.equals(other.publicAddresses))
         return false;
      if (group == null) {
         if (other.group != null)
            return false;
      } else if (!group.equals(other.group))
         return false;
      if (imageId == null) {
         if (other.imageId != null)
            return false;
      } else if (!imageId.equals(other.imageId))
         return false;
      if (hardware == null) {
         if (other.hardware != null)
            return false;
      } else if (!hardware.equals(other.hardware))
         return false;
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (adminPassword == null) {
         if (other.adminPassword != null)
            return false;
      } else if (!adminPassword.equals(other.adminPassword))
         return false;
      if (credentials == null) {
         if (other.credentials != null)
            return false;
      } else if (!credentials.equals(other.credentials))
         return false;
      return true;
   }

}
