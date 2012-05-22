/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Expires 2.0 (the
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

import java.util.Date;

import com.google.common.base.Objects;

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
 *      />
 */
public class Token implements Comparable<Token> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromToken(this);
   }

   public static class Builder {
      protected String id;
      protected Date expires;
      protected Tenant tenant;

      /**
       * @see Token#getId()
       */
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
         return this;
      }

      /**
       * @see Token#getExpires()
       */
      public Builder expires(Date expires) {
         this.expires = checkNotNull(expires, "expires");
         return this;
      }

      /**
       * @see Token#getTenant()
       */
      public Builder tenant(Tenant tenant) {
         this.tenant = checkNotNull(tenant, "tenant");
         return this;
      }

      public Token build() {
         return new Token(id, expires, tenant);
      }

      public Builder fromToken(Token from) {
         return id(from.getId()).expires(from.getExpires()).tenant(from.getTenant());
      }
   }
   
   protected Token() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
   
   protected String id;
   protected Date expires;
   protected Tenant tenant;

   public Token(String id, Date expires, Tenant tenant) {
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
      return id;
   }

   /**
    * @return the expires of the token
    */
   public Date getExpires() {
      return expires;
   }

   /**
    * @return the tenant assigned to the token
    */
   public Tenant getTenant() {
      return tenant;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Token) {
         final Token other = Token.class.cast(object);
         return equal(id, other.id) && equal(expires, other.expires) && equal(tenant, other.tenant);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, expires, tenant);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("expires", expires).add("tenant", tenant).toString();
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
