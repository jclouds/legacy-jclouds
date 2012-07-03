/*
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
package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
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
public class User {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUser(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String name;
      protected Set<Role> roles = ImmutableSet.of();

      /**
       * @see User#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see User#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see User#getRoles()
       */
      public T roles(Set<Role> roles) {
         this.roles = ImmutableSet.copyOf(checkNotNull(roles, "roles"));
         return self();
      }

      public T roles(Role... in) {
         return roles(ImmutableSet.copyOf(in));
      }

      public User build() {
         return new User(id, name, roles);
      }

      public T fromUser(User in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .roles(in.getRoles());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final Set<Role> roles;

   @ConstructorProperties({
         "id", "name", "roles"
   })
   protected User(String id, String name, @Nullable Set<Role> roles) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.roles = roles == null ? ImmutableSet.<Role>of() : ImmutableSet.copyOf(checkNotNull(roles, "roles"));
   }

   /**
    * When providing an ID, it is assumed that the user exists in the current OpenStack deployment
    *
    * @return the id of the user in the current OpenStack deployment
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of the user
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the roles assigned to the user
    */
   public Set<Role> getRoles() {
      return this.roles;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, roles);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      User that = User.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.roles, that.roles);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("roles", roles);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
