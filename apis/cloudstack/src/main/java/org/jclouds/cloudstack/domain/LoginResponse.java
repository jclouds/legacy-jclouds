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
package org.jclouds.cloudstack.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Representation of the login API call response
 *
 * @author Andrei Savu
 */
public class LoginResponse {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromLoginResponse(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String username;
      protected String userId;
      protected String password;
      protected String domainId;
      protected long timeout;
      protected boolean registered;
      protected String accountName;
      protected String firstName;
      protected String lastName;
      protected Account.Type accountType;
      protected String timezone;
      protected String timezoneOffset;
      protected String sessionKey;
      protected String jSessionId;

      /**
       * @see LoginResponse#getUsername()
       */
      public T username(String username) {
         this.username = username;
         return self();
      }

      /**
       * @see LoginResponse#getUserId()
       */
      public T userId(String userId) {
         this.userId = userId;
         return self();
      }

      /**
       * @see LoginResponse#getPassword()
       */
      public T password(String password) {
         this.password = password;
         return self();
      }

      /**
       * @see LoginResponse#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see LoginResponse#getTimeout()
       */
      public T timeout(long timeout) {
         this.timeout = timeout;
         return self();
      }

      /**
       * @see LoginResponse#isRegistered()
       */
      public T registered(boolean registered) {
         this.registered = registered;
         return self();
      }

      /**
       * @see LoginResponse#getAccountName()
       */
      public T accountName(String accountName) {
         this.accountName = accountName;
         return self();
      }

      /**
       * @see LoginResponse#getFirstName()
       */
      public T firstName(String firstName) {
         this.firstName = firstName;
         return self();
      }

      /**
       * @see LoginResponse#getLastName()
       */
      public T lastName(String lastName) {
         this.lastName = lastName;
         return self();
      }

      /**
       * @see LoginResponse#getAccountType()
       */
      public T accountType(Account.Type accountType) {
         this.accountType = accountType;
         return self();
      }

      /**
       * @see LoginResponse#getTimezone()
       */
      public T timezone(String timezone) {
         this.timezone = timezone;
         return self();
      }

      /**
       * @see LoginResponse#getTimezoneOffset()
       */
      public T timezoneOffset(String timezoneOffset) {
         this.timezoneOffset = timezoneOffset;
         return self();
      }

      /**
       * @see LoginResponse#getSessionKey()
       */
      public T sessionKey(String sessionKey) {
         this.sessionKey = sessionKey;
         return self();
      }

      /**
       * @see LoginResponse#getJSessionId()
       */
      public T jSessionId(String jSessionId) {
         this.jSessionId = jSessionId;
         return self();
      }

      public LoginResponse build() {
         return new LoginResponse(username, userId, password, domainId, timeout, registered, accountName, firstName, lastName, accountType, timezone, timezoneOffset, sessionKey, jSessionId);
      }

      public T fromLoginResponse(LoginResponse in) {
         return this
               .username(in.getUsername())
               .userId(in.getUserId())
               .password(in.getPassword())
               .domainId(in.getDomainId())
               .timeout(in.getTimeout())
               .registered(in.isRegistered())
               .accountName(in.getAccountName())
               .firstName(in.getFirstName())
               .lastName(in.getLastName())
               .accountType(in.getAccountType())
               .timezone(in.getTimezone())
               .timezoneOffset(in.getTimezoneOffset())
               .sessionKey(in.getSessionKey())
               .jSessionId(in.getJSessionId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String username;
   private final String userId;
   private final String password;
   private final String domainId;
   private final long timeout;
   private final boolean registered;
   private final String accountName;
   private final String firstName;
   private final String lastName;
   private final Account.Type accountType;
   private final String timezone;
   private final String timezoneOffset;
   private final String sessionKey;
   private final String jSessionId;

   @ConstructorProperties({
         "username", "userid", "password", "domainid", "timeout", "registered", "account", "firstname", "lastname", "type", "timezone", "timezoneoffset", "sessionkey", "jSessionId"
   })
   protected LoginResponse(@Nullable String username, @Nullable String userId, @Nullable String password, @Nullable String domainId, long timeout, boolean registered, @Nullable String accountName, @Nullable String firstName, @Nullable String lastName, @Nullable Account.Type accountType, @Nullable String timezone, @Nullable String timezoneOffset, @Nullable String sessionKey, @Nullable String jSessionId) {
      this.username = username;
      this.userId = userId;
      this.password = password;
      this.domainId = domainId;
      this.timeout = timeout;
      this.registered = registered;
      this.accountName = accountName;
      this.firstName = firstName;
      this.lastName = lastName;
      this.accountType = accountType;
      this.timezone = timezone;
      this.timezoneOffset = timezoneOffset;
      this.sessionKey = sessionKey;
      this.jSessionId = jSessionId;
   }

   @Nullable
   public String getUsername() {
      return this.username;
   }

   @Nullable
   public String getUserId() {
      return this.userId;
   }

   @Nullable
   public String getPassword() {
      return this.password;
   }

   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   public long getTimeout() {
      return this.timeout;
   }

   public boolean isRegistered() {
      return this.registered;
   }

   @Nullable
   public String getAccountName() {
      return this.accountName;
   }

   @Nullable
   public String getFirstName() {
      return this.firstName;
   }

   @Nullable
   public String getLastName() {
      return this.lastName;
   }

   @Nullable
   public Account.Type getAccountType() {
      return this.accountType;
   }

   @Nullable
   public String getTimezone() {
      return this.timezone;
   }

   @Nullable
   public String getTimezoneOffset() {
      return this.timezoneOffset;
   }

   @Nullable
   public String getSessionKey() {
      return this.sessionKey;
   }

   @Nullable
   public String getJSessionId() {
      return this.jSessionId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(username, userId, password, domainId, timeout, registered, accountName, firstName, lastName, accountType, timezone, timezoneOffset, sessionKey, jSessionId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      LoginResponse that = LoginResponse.class.cast(obj);
      return Objects.equal(this.username, that.username)
            && Objects.equal(this.userId, that.userId)
            && Objects.equal(this.password, that.password)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.timeout, that.timeout)
            && Objects.equal(this.registered, that.registered)
            && Objects.equal(this.accountName, that.accountName)
            && Objects.equal(this.firstName, that.firstName)
            && Objects.equal(this.lastName, that.lastName)
            && Objects.equal(this.accountType, that.accountType)
            && Objects.equal(this.timezone, that.timezone)
            && Objects.equal(this.timezoneOffset, that.timezoneOffset)
            && Objects.equal(this.sessionKey, that.sessionKey)
            && Objects.equal(this.jSessionId, that.jSessionId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("username", username).add("userId", userId).add("password", password).add("domainId", domainId).add("timeout", timeout).add("registered", registered).add("accountName", accountName).add("firstName", firstName).add("lastName", lastName).add("accountType", accountType).add("timezone", timezone).add("timezoneOffset", timezoneOffset).add("sessionKey", sessionKey).add("jSessionId", jSessionId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
