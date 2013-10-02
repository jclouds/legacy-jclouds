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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.domain.Credentials;

import com.google.common.base.Objects;

/**
 * Session credentials for API authentication.
 * 
 * @see <a href= "https://manage.dynect.net/help/docs/api2/rest/" />
 * 
 * @author Adrian Cole
 */
public final class SessionCredentials extends Credentials {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder extends Credentials.Builder<SessionCredentials> {
      private String customerName;
      private String userName;
      private String password;

      /**
       * @see SessionCredentials#getCustomerName()
       */
      public Builder customerName(String customerName) {
         this.customerName = customerName;
         return this;
      }

      @Override
      public Builder identity(String identity) {
         return userName(identity);
      }

      @Override
      public Builder credential(String credential) {
         return password(credential);
      }

      /**
       * @see SessionCredentials#getUserName()
       */
      public Builder userName(String userName) {
         this.userName = userName;
         return this;
      }

      /**
       * @see SessionCredentials#getPassword()
       */
      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public SessionCredentials build() {
         return new SessionCredentials(customerName, userName, password);
      }

      public Builder from(SessionCredentials in) {
         return this.userName(in.identity).password(in.credential).customerName(in.customerName);
      }
   }

   @Named("customer_name")
   private final String customerName;
   @Named("user_name")
   private final String userName;
   private final String password;

   @ConstructorProperties({ "customer_name", "user_name", "password" })
   private SessionCredentials(String customerName, String userName, String password) {
      super(checkNotNull(userName, "userName"), checkNotNull(password, "password for %s", userName));
      this.userName = userName;
      this.password = password;
      this.customerName = checkNotNull(customerName, "customerName for %s", userName);
   }

   /**
    * UserName ID that identifies the temporary credentials.
    */
   public String getUserName() {
      return userName;
   }

   /**
    * The Secret Access Key to sign requests.
    */
   public String getPassword() {
      return password;
   }

   /**
    * The security token that users must pass to the service API to use the
    * temporary credentials.
    */
   public String getCustomerName() {
      return customerName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(customerName, userName, password);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SessionCredentials other = SessionCredentials.class.cast(obj);
      return Objects.equal(this.userName, other.userName) && Objects.equal(this.password, other.password)
            && Objects.equal(this.customerName, other.customerName);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("userName", identity).add("customerName", customerName).toString();
   }
}
