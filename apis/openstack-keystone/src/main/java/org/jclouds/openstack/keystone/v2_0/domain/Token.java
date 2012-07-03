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
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A token is an arbitrary bit of text that is used to access resources. Each token has a scope
 * which describes which resources are accessible with it. A token may be revoked at anytime and is
 * valid for a finite duration.
 * <p/>
 * While Keystone supports token-based authentication in this release, the intention is for it to
 * support additional protocols in the future. The intent is for it to be an integration service
 * foremost, and not a aspire to be a full-fledged identity store and management solution.
 *
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Identity-Service-Concepts-e1362.html"
/>
 */
public class Token implements Comparable<Token> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromToken(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected Date expires;
      protected Tenant tenant;

      /**
       * @see Token#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Token#getExpires()
       */
      public T expires(Date expires) {
         this.expires = expires;
         return self();
      }

      /**
       * @see Token#getTenant()
       */
      public T tenant(Tenant tenant) {
         this.tenant = tenant;
         return self();
      }

      public Token build() {
         return new Token(id, expires, tenant);
      }

      public T fromToken(Token in) {
         return this
               .id(in.getId())
               .expires(in.getExpires())
               .tenant(in.getTenant());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final Date expires;
   private final Tenant tenant;

   @ConstructorProperties({
         "id", "expires", "tenant"
   })
   protected Token(String id, Date expires, Tenant tenant) {
      this.id = checkNotNull(id, "id");
      this.expires = checkNotNull(expires, "expires");
      this.tenant = checkNotNull(tenant, "tenant");
   }

   /**
    * When providing an ID, it is assumed that the token exists in the current OpenStack deployment
    *
    * @return the id of the token in the current OpenStack deployment
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the expires of the token
    */
   public Date getExpires() {
      return this.expires;
   }

   /**
    * @return the tenant assigned to the token
    */
   public Tenant getTenant() {
      return this.tenant;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, expires, tenant);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Token that = Token.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.expires, that.expires)
            && Objects.equal(this.tenant, that.tenant);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("expires", expires).add("tenant", tenant);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Token that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.id.compareTo(that.id);
   }
}
