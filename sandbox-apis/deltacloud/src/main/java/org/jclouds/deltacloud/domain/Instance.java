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

package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.http.HttpRequest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * An instance is a concrete machine realized from an image.
 * 
 * @author Adrian Cole
 */
public class Instance {
   private final URI href;
   private final String id;
   private final String ownerId;
   @Nullable
   private final String name;
   private final URI image;
   private final URI hardwareProfile;
   private final URI realm;
   private final InstanceState state;
   private final Map<InstanceAction, HttpRequest> actions;
   private final Set<String> publicAddresses;
   private final Set<String> privateAddresses;

   public Instance(URI href, String id, String ownerId, @Nullable String name, URI image, URI hardwareProfile,
         URI realm, InstanceState state, Map<InstanceAction, HttpRequest> actions, Set<String> publicAddresses,
         Set<String> privateAddresses) {
      this.href = checkNotNull(href, "href");
      this.id = checkNotNull(id, "id");
      this.ownerId = checkNotNull(ownerId, "ownerId");
      this.name = name;
      this.image = checkNotNull(image, "image");
      this.hardwareProfile = checkNotNull(hardwareProfile, "hardwareProfile");
      this.realm = checkNotNull(realm, "realm");
      this.state = checkNotNull(state, "state");
      this.actions = ImmutableMap.copyOf(checkNotNull(actions, "actions"));
      this.publicAddresses = ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses"));
      this.privateAddresses = ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses"));
   }

   /**
    * 
    * @return URL to manipulate a specific instance
    */
   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return A unique identifier for the instance
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return An opaque identifier which indicates the owner of an instance
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * 
    * @return An optional short label describing the instance
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * 
    * @return
    */
   public URI getImage() {
      return image;
   }

   /**
    * 
    * @return a link to the hardware profile in use by the instance
    */
   public URI getHardwareProfile() {
      return hardwareProfile;
   }

   /**
    * 
    * @return a link to the realm where the instance is deployed
    */
   public URI getRealm() {
      return realm;
   }

   /**
    * 
    * @return indicator of the instance's current state
    */
   public InstanceState getState() {
      return state;
   }

   /**
    * 
    * @return valid actions for the instance, along with the URL which may be used to perform the
    *         action
    */
   public Map<InstanceAction, HttpRequest> getActions() {
      return actions;
   }

   /**
    * 
    * @return publicly routable IP addresses or names for the instance
    */
   public Set<String> getPublicAddresses() {
      return publicAddresses;
   }

   /**
    * 
    * @return Private network IP addresses or names for the instance
    */
   public Set<String> getPrivateAddresses() {
      return privateAddresses;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((actions == null) ? 0 : actions.hashCode());
      result = prime * result + ((hardwareProfile == null) ? 0 : hardwareProfile.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((privateAddresses == null) ? 0 : privateAddresses.hashCode());
      result = prime * result + ((publicAddresses == null) ? 0 : publicAddresses.hashCode());
      result = prime * result + ((realm == null) ? 0 : realm.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
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
      Instance other = (Instance) obj;
      if (actions == null) {
         if (other.actions != null)
            return false;
      } else if (!actions.equals(other.actions))
         return false;
      if (hardwareProfile == null) {
         if (other.hardwareProfile != null)
            return false;
      } else if (!hardwareProfile.equals(other.hardwareProfile))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (image == null) {
         if (other.image != null)
            return false;
      } else if (!image.equals(other.image))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
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
      if (realm == null) {
         if (other.realm != null)
            return false;
      } else if (!realm.equals(other.realm))
         return false;
      if (state != other.state)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", id=" + id + ", name=" + name + ", image=" + image + ", hardwareProfile="
            + hardwareProfile + ", realm=" + realm + ", state=" + state + ", actions=" + actions + ", publicAddresses="
            + publicAddresses + ", privateAddresses=" + privateAddresses + "]";
   }

}
