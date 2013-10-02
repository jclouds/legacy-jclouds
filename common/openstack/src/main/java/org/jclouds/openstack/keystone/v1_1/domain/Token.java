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

import java.util.Date;

import com.google.common.base.Objects;

/**
 * 
 * Tokens are valid for a finite duration. The expires attribute denotes the
 * time after which the token will automatically become invalid. A token may be
 * manually revoked before the time identified by the expires attribute; expires
 * predicts a token's maximum possible lifespan but does not guarantee that it
 * will reach that lifespan. Clients are encouraged to cache a token until it
 * expires.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.rackspace.com/loadbalancers/api/v1.0/clb-devguide/content/Service_Access_Endpoints-d1e517.html"
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

      public Token build() {
         return new Token(id, expires);
      }

      public Builder fromToken(Token from) {
         return id(from.getId()).expires(from.getExpires());
      }
   }

   protected Token() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   protected String id;
   protected Date expires;

   public Token(String id, Date expires) {
      this.id = checkNotNull(id, "id");
      this.expires = checkNotNull(expires, "expires");
   }

   /**
    * When providing an ID, it is assumed that the token exists in the current
    * OpenStack deployment
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

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Token) {
         final Token other = Token.class.cast(object);
         return equal(id, other.id) && equal(expires, other.expires);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, expires);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("expires", expires).toString();
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
