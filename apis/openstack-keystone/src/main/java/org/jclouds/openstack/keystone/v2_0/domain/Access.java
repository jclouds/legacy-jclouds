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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * TODO
 *
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Service_API_Client_Operations.html"
/>
 */
public class Access implements Comparable<Access> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAccess(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected Token token;
      protected User user;
      protected Set<Service> serviceCatalog = ImmutableSet.of();

      /**
       * @see Access#getToken()
       */
      public T token(Token token) {
         this.token = token;
         return self();
      }

      /**
       * @see Access#getUser()
       */
      public T user(User user) {
         this.user = user;
         return self();
      }

      /**
       * @see Access#getServiceCatalog()
       */
      public T serviceCatalog(Set<Service> serviceCatalog) {
         this.serviceCatalog = ImmutableSet.copyOf(checkNotNull(serviceCatalog, "serviceCatalog"));
         return self();
      }

      public T serviceCatalog(Service... in) {
         return serviceCatalog(ImmutableSet.copyOf(in));
      }

      public Access build() {
         return new Access(token, user, serviceCatalog);
      }

      public T fromAccess(Access in) {
         return this
               .token(in.getToken())
               .user(in.getUser())
               .serviceCatalog(in.getServiceCatalog());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Token token;
   private final User user;
   private final Set<Service> serviceCatalog;

   @ConstructorProperties({
         "token", "user", "serviceCatalog"
   })
   protected Access(Token token, User user, Set<Service> serviceCatalog) {
      this.token = checkNotNull(token, "token");
      this.user = checkNotNull(user, "user");
      this.serviceCatalog = ImmutableSet.copyOf(checkNotNull(serviceCatalog, "serviceCatalog"));
   }

   /**
    * TODO
    */
   public Token getToken() {
      return this.token;
   }

   /**
    * TODO
    */
   public User getUser() {
      return this.user;
   }

   /**
    * TODO
    */
   public Set<Service> getServiceCatalog() {
      return this.serviceCatalog;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(token, user, serviceCatalog);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Access that = Access.class.cast(obj);
      return Objects.equal(this.token, that.token)
            && Objects.equal(this.user, that.user)
            && Objects.equal(this.serviceCatalog, that.serviceCatalog);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("token", token).add("user", user).add("serviceCatalog", serviceCatalog);
   }

   @Override
   public String toString() {
      return string().toString();
   }
   
   @Override
   public int compareTo(Access that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.token.compareTo(that.token);
   }

}
