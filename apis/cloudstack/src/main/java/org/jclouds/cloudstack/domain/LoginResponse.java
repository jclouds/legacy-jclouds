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

package org.jclouds.cloudstack.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Representation of the login API call response
 * 
 * @author Andrei Savu
 */
public class LoginResponse implements Comparable<LoginResponse> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String username;
      private String userId;
      private String password;
      private String domainId;
      private long timeout;
      private boolean registered;
      private String accountName;
      private String firstName;
      private String lastName;
      private Account.Type accountType;
      private String timezone;
      private String timezoneOffset;
      private String sessionKey;
      private String jSessionId;

      public Builder copyOf(LoginResponse r) {
         this.username = r.username;
         this.userId = r.userId;
         this.password = r.password;
         this.domainId = r.domainId;
         this.timeout = r.timeout;
         this.registered = r.registered;
         this.accountName = r.accountName;
         this.firstName = r.firstName;
         this.lastName = r.lastName;
         this.accountType = r.accountType;
         this.timezone = r.timezone;
         this.timezoneOffset = r.timezoneOffset;
         this.sessionKey = r.sessionKey;
         this.jSessionId = r.jSessionId;
         return this;
      }

      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public Builder userId(String userId) {
         this.userId = userId;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder timeout(long timeout) {
         this.timeout = timeout;
         return this;
      }

      public Builder registered(boolean registered) {
         this.registered = registered;
         return this;
      }

      public Builder accountName(String accountName) {
         this.accountName = accountName;
         return this;
      }

      public Builder firstName(String firstName) {
         this.firstName = firstName;
         return this;
      }

      public Builder lastName(String lastName) {
         this.lastName = lastName;
         return this;
      }

      public Builder accountType(Account.Type accountType) {
         this.accountType = accountType;
         return this;
      }

      public Builder timezone(String timezone) {
         this.timezone = timezone;
         return this;
      }

      public Builder timezoneOffset(String timezoneOffset) {
         this.timezoneOffset = timezoneOffset;
         return this;
      }

      public Builder sessionKey(String sessionKey) {
         this.sessionKey = sessionKey;
         return this;
      }

      public Builder jSessionId(String jSessionId) {
         this.jSessionId = jSessionId;
         return this;
      }

      public LoginResponse build() {
         return new LoginResponse(username, userId, password, domainId, timeout, registered, accountName, firstName,
                  lastName, accountType, timezone, timezoneOffset, sessionKey, jSessionId);
      }
   }

   private final String username;
   @SerializedName("userid")
   private final String userId;
   private final String password;
   @SerializedName("domainid")
   private final String domainId;
   private final long timeout;
   private final boolean registered;
   @SerializedName("account")
   private final String accountName;
   @SerializedName("firstname")
   private final String firstName;
   @SerializedName("lastname")
   private final String lastName;
   @SerializedName("type")
   private final Account.Type accountType;
   private final String timezone;
   @SerializedName("timezoneoffset")
   private final String timezoneOffset;
   @SerializedName("sessionkey")
   private final String sessionKey;
   private final String jSessionId;

   public LoginResponse(String username, String userId, String password, String domainId, long timeout, boolean registered,
            String accountName, String firstName, String lastName, Account.Type accountType, String timezone,
            String timezoneOffset, String sessionKey, String jSessionId) {
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

   public String getUsername() {
      return username;
   }

   public String getUserId() {
      return userId;
   }

   public String getPassword() {
      return password;
   }

   public String getDomainId() {
      return domainId;
   }

   public long getTimeout() {
      return timeout;
   }

   public boolean isRegistered() {
      return registered;
   }

   public String getAccountName() {
      return accountName;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public Account.Type getAccountType() {
      return accountType;
   }

   public String getTimezone() {
      return timezone;
   }

   public String getTimezoneOffset() {
      return timezoneOffset;
   }

   public String getSessionKey() {
      return sessionKey;
   }

   public String getJSessionId() {
      return jSessionId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      LoginResponse that = (LoginResponse) o;

      if (!Objects.equal(accountName, that.accountName)) return false;
      if (!Objects.equal(accountType, that.accountType)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(firstName, that.firstName)) return false;
      if (!Objects.equal(jSessionId, that.jSessionId)) return false;
      if (!Objects.equal(lastName, that.lastName)) return false;
      if (!Objects.equal(password, that.password)) return false;
      if (!Objects.equal(registered, that.registered)) return false;
      if (!Objects.equal(sessionKey, that.sessionKey)) return false;
      if (!Objects.equal(timeout, that.timeout)) return false;
      if (!Objects.equal(timezone, that.timezone)) return false;
      if (!Objects.equal(timezoneOffset, that.timezoneOffset)) return false;
      if (!Objects.equal(userId, that.userId)) return false;
      if (!Objects.equal(username, that.username)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(accountName, accountType, domainId, firstName, jSessionId, lastName,
                               password, registered, sessionKey, timeout, timezone, timezoneOffset,
                               userId, username);
   }

   @Override
   public String toString() {
      return "LoginResponse{" + "username='" + username + '\'' + ", userId=" + userId + ", password='" + password
               + '\'' + ", domainId=" + domainId + ", timeout=" + timeout + ", registered=" + registered
               + ", accountName='" + accountName + '\'' + ", firstName='" + firstName + '\'' + ", lastName='"
               + lastName + '\'' + ", accountType=" + accountType + ", timezone='" + timezone + '\''
               + ", timezoneOffset='" + timezoneOffset + '\'' + ", sessionKey='" + sessionKey + '\'' + ", jSessionId='"
               + jSessionId + '\'' + '}';
   }

   @Override
   public int compareTo(LoginResponse arg0) {
      return sessionKey.compareTo(arg0.getSessionKey());
   }

}
