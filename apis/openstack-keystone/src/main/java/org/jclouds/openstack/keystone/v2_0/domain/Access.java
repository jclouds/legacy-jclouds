/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, User 2.0 (the
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
 * TODO
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Service_API_Client_Operations.html"
 *      />
 */
public class Access implements Comparable<Access> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromAccess(this);
   }

   public static class Builder {
      protected Token token;
      protected User user;
      protected Set<Service> serviceCatalog = ImmutableSet.of();

      /**
       * @see Access#getToken()
       */
      public Builder token(Token token) {
         this.token = checkNotNull(token, "token");
         return this;
      }

      /**
       * @see Access#getUser()
       */
      public Builder user(User user) {
         this.user = checkNotNull(user, "user");
         return this;
      }

      /**
       * @see Access#getServiceCatalog()
       */
      public Builder serviceCatalog(Service... serviceCatalog) {
         return serviceCatalog(ImmutableSet.copyOf(checkNotNull(serviceCatalog, "serviceCatalog")));
      }

      /**
       * @see Access#getServiceCatalog()
       */
      public Builder serviceCatalog(Set<Service> serviceCatalog) {
         this.serviceCatalog = ImmutableSet.copyOf(checkNotNull(serviceCatalog, "serviceCatalog"));
         return this;
      }

      public Access build() {
         return new Access(token, user, serviceCatalog);
      }

      public Builder fromAccess(Access from) {
         return token(from.getToken()).user(from.getUser()).serviceCatalog(from.getServiceCatalog());
      }
   }


   protected Access() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }

   protected Token token;
   protected User user;
   protected Set<Service> serviceCatalog = ImmutableSet.of();

   public Access(Token token, User user, Set<Service> serviceCatalog) {
      this.token = checkNotNull(token, "token");
      this.user = checkNotNull(user, "user");
      this.serviceCatalog = ImmutableSet.copyOf(checkNotNull(serviceCatalog, "serviceCatalog"));
   }

   /**
    * TODO
    */
   public Token getToken() {
      return token;
   }

   /**
    * TODO
    */
   public User getUser() {
      return user;
   }

   /**
    * TODO
    */
   public Set<Service> getServiceCatalog() {
      return serviceCatalog;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Access) {
         final Access other = Access.class.cast(object);
         return equal(token, other.token) && equal(user, other.user) && equal(serviceCatalog, other.serviceCatalog);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(token, user, serviceCatalog);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("token", token).add("user", user).add("serviceCatalog", serviceCatalog).toString();
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
