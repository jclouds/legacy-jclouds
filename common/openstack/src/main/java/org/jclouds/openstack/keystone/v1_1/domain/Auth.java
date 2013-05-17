/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.keystone.v1_1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * TODO
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.rackspace.com/loadbalancers/api/v1.0/clb-devguide/content/Endpoint_Access_Endpoints-d1e517.html"
 *      />
 */
public class Auth implements Comparable<Auth> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromAccess(this);
   }

   public static class Builder {
      protected Token token;
      protected Multimap<String, Endpoint> serviceCatalog = ImmutableMultimap.of();

      /**
       * @see Auth#getToken()
       */
      public Builder token(Token token) {
         this.token = checkNotNull(token, "token");
         return this;
      }

      /**
       * @see Auth#getServiceCatalog()
       */
      public Builder serviceCatalog(Multimap<String, Endpoint> serviceCatalog) {
         this.serviceCatalog = ImmutableMultimap.copyOf(checkNotNull(serviceCatalog, "serviceCatalog"));
         return this;
      }

      public Auth build() {
         return new Auth(token, serviceCatalog);
      }

      public Builder fromAccess(Auth from) {
         return token(from.getToken()).serviceCatalog(from.getServiceCatalog());
      }
   }
  
   protected final Token token;
   protected final Multimap<String, Endpoint> serviceCatalog;

   public Auth(Token token, Multimap<String, Endpoint> serviceCatalog) {
      this.token = checkNotNull(token, "token");
      this.serviceCatalog = ImmutableMultimap.copyOf(checkNotNull(serviceCatalog, "serviceCatalog"));
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
   public Multimap<String, Endpoint> getServiceCatalog() {
      return serviceCatalog;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Auth) {
         final Auth other = Auth.class.cast(object);
         return equal(token, other.token) && equal(serviceCatalog, other.serviceCatalog);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(token, serviceCatalog);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("token", token).add("serviceCatalog", serviceCatalog).toString();
   }

   @Override
   public int compareTo(Auth that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.token.compareTo(that.token);
   }

}
