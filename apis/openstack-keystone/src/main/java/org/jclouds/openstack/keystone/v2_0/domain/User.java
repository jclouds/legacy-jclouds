/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Name 2.0 (the
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

package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * A digital representation of a person, system, or service who uses OpenStack cloud services.
 * Keystone authentication services will validate that incoming request are being made by the user
 * who claims to be making the call. Users have a login and may be assigned tokens to access users.
 * Users may be directly assigned to a particular tenant and behave as if they are contained in that
 * tenant.
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Identity-Service-Concepts-e1362.html"
 *      />
 */
public class User implements Comparable<User> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromUser(this);
   }

   public static class Builder {
      protected String id;
      protected String name;
      protected Set<Role> roles = ImmutableSet.of();

      /**
       * @see User#getId()
       */
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
         return this;
      }

      /**
       * @see User#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see User#getRoles()
       */
      public Builder roles(Role... roles) {
         return roles(ImmutableSet.copyOf(checkNotNull(roles, "roles")));
      }

      /**
       * @see User#getRoles()
       */
      public Builder roles(Set<Role> roles) {
         this.roles = ImmutableSet.copyOf(checkNotNull(roles, "roles"));
         return this;
      }

      public User build() {
         return new User(id, name, roles);
      }

      public Builder fromUser(User from) {
         return id(from.getId()).name(from.getName()).roles(from.getRoles());
      }
   }
   
   protected User() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
   
   protected String id;
   protected String name;
   protected Set<Role> roles = ImmutableSet.of();

   protected User(String id, String name, Set<Role> roles) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.roles = ImmutableSet.copyOf(checkNotNull(roles, "roles"));
   }

   /**
    * When providing an ID, it is assumed that the user exists in the current OpenStack deployment
    * 
    * @return the id of the user in the current OpenStack deployment
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name of the user
    */
   public String getName() {
      return name;
   }

   /**
    * @return the roles assigned to the user
    */
   public Set<Role> getRoles() {
      return roles;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof User) {
         final User other = User.class.cast(object);
         return equal(id, other.id) && equal(name, other.name) && equal(roles, other.roles);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, roles);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("roles", roles).toString();
   }

   @Override
   public int compareTo(User that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.id.compareTo(that.id);
   }

}
