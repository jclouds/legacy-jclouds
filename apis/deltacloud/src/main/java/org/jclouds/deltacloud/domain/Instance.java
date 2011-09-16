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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.http.HttpRequest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * An instance is a concrete machine realized from an image.
 * 
 * @author Adrian Cole
 */
public class Instance {
   public static interface Authentication {

   }

   public static enum State {
      /**
       * initial state, before instance is created.
       */
      START,
      /**
       * the instance is in the process of being launched
       */
      PENDING,
      /**
       * the instance launched (although the boot process might not be completed)
       */
      RUNNING,
      /**
       * the instance is shutting down
       */
      SHUTTING_DOWN,
      /**
       * the instance is stopped
       */
      STOPPED,
      /**
       * the instance is terminated
       */
      FINISH,
      /**
       * state returned as something besides the above.
       */
      UNRECOGNIZED;

      public static State fromValue(String state) {
         try {
            return valueOf(checkNotNull(state, "state").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static enum Action {

      CREATE,

      RUN,

      REBOOT,

      START,

      STOP,

      DESTROY,

      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Action fromValue(String action) {
         try {
            return valueOf(checkNotNull(action, "action").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final URI href;
   private final String id;
   private final String ownerId;
   @Nullable
   private final String name;
   private final URI image;
   private final URI hardwareProfile;
   private final URI realm;
   private final State state;
   private final Map<Action, HttpRequest> actions;
   @Nullable
   private final Authentication authentication;
   private final Set<String> publicAddresses;
   private final Set<String> privateAddresses;

   public Instance(URI href, String id, String ownerId, @Nullable String name, URI image, URI hardwareProfile,
            URI realm, State state, Map<Action, HttpRequest> actions, @Nullable Authentication authentication,
            Set<String> publicAddresses, Set<String> privateAddresses) {
      this.href = checkNotNull(href, "href");
      this.id = checkNotNull(id, "id");
      this.ownerId = checkNotNull(ownerId, "ownerId");
      this.name = name;
      this.image = checkNotNull(image, "image");
      this.hardwareProfile = checkNotNull(hardwareProfile, "hardwareProfile");
      this.realm = checkNotNull(realm, "realm");
      this.state = checkNotNull(state, "state");
      this.actions = ImmutableMap.copyOf(checkNotNull(actions, "actions"));
      this.authentication = authentication;
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
   public State getState() {
      return state;
   }

   /**
    * 
    * @return valid actions for the instance, along with the URL which may be used to perform the
    *         action
    */
   public Map<Action, HttpRequest> getActions() {
      return actions;
   }

   /**
    * 
    * @return authentication of the instance or null
    */
   @Nullable
   public Authentication getAuthentication() {
      return authentication;
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
      result = prime * result + ((href == null) ? 0 : href.hashCode());
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
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[id=%s, href=%s, image=%s, name=%s, state=%s, realm=%s, ownerId=%s, hardwareProfile=%s, actions=%s, authentication=%s, privateAddresses=%s, publicAddresses=%s]",
                        id, href, image, name, state, realm, ownerId, hardwareProfile, actions, authentication,
                        privateAddresses, publicAddresses);
   }

}
