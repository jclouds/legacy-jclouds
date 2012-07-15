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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

/**
 * Session and its corresponding token
 *
 * @author Adrian Cole
 */
public class SessionWithToken {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSessionWithToken(this);
   }

   public static class Builder {

      protected Session session;
      protected String token;

      /**
       * @see SessionWithToken#getSession()
       */
      public Builder session(Session session) {
         this.session = session;
         return this;
      }

      /**
       * @see SessionWithToken#getToken()
       */
      public Builder token(String token) {
         this.token = token;
         return this;
      }

      public SessionWithToken build() {
         return new SessionWithToken(token, session);
      }

      protected Builder fromSessionWithToken(SessionWithToken in) {
         return session(in.getSession()).token(in.getToken());
      }

   }

   private Session session;
   private String token;

   protected SessionWithToken(String token, Session session) {
      this.session = session;
      this.token = token;
   }

   protected SessionWithToken() {
      // For JAXB
   }

   /**
    * TODO
    */
   public Session getSession() {
      return session;
   }

   /**
    * An object reference, expressed in URL format. Because this URL includes the object identifier
    * portion of the id attribute value, it uniquely identifies the object, persists for the life of
    * the object, and is never reused. The value of the token attribute is a reference to a view of
    * the object, and can be used to access a representation of the object that is valid in a
    * particular context. Although URLs have a well-known syntax and a well-understood
    * interpretation, a api should treat each token as an opaque string. The rules that govern
    * how the server constructs token strings might change in future releases.
    *
    * @return an opaque reference and should never be parsed
    */
   public String getToken() {
      return token;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SessionWithToken that = SessionWithToken.class.cast(o);
      return equal(token, that.token) && equal(session, that.session);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(session, token);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("session", session).add("token", token).toString();
   }
}
