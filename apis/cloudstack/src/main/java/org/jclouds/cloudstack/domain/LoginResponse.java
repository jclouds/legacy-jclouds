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
      private long userId;
      private String password;
      private long domainId;
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

      public Builder userId(long userId) {
         this.userId = userId;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Builder domainId(long domainId) {
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
   private final long userId;
   private final String password;
   @SerializedName("domainid")
   private final long domainId;
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

   public LoginResponse(String username, long userId, String password, long domainId, long timeout, boolean registered,
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

   public long getUserId() {
      return userId;
   }

   public String getPassword() {
      return password;
   }

   public long getDomainId() {
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
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoginResponse other = (LoginResponse) obj;
      if (accountName == null) {
         if (other.accountName != null)
            return false;
      } else if (!accountName.equals(other.accountName))
         return false;
      if (accountType == null) {
         if (other.accountType != null)
            return false;
      } else if (!accountType.equals(other.accountType))
         return false;
      if (domainId != other.domainId)
         return false;
      if (firstName == null) {
         if (other.firstName != null)
            return false;
      } else if (!firstName.equals(other.firstName))
         return false;
      if (jSessionId == null) {
         if (other.jSessionId != null)
            return false;
      } else if (!jSessionId.equals(other.jSessionId))
         return false;
      if (lastName == null) {
         if (other.lastName != null)
            return false;
      } else if (!lastName.equals(other.lastName))
         return false;
      if (password == null) {
         if (other.password != null)
            return false;
      } else if (!password.equals(other.password))
         return false;
      if (registered != other.registered)
         return false;
      if (sessionKey == null) {
         if (other.sessionKey != null)
            return false;
      } else if (!sessionKey.equals(other.sessionKey))
         return false;
      if (timeout != other.timeout)
         return false;
      if (timezone == null) {
         if (other.timezone != null)
            return false;
      } else if (!timezone.equals(other.timezone))
         return false;
      if (timezoneOffset == null) {
         if (other.timezoneOffset != null)
            return false;
      } else if (!timezoneOffset.equals(other.timezoneOffset))
         return false;
      if (userId != other.userId)
         return false;
      if (username == null) {
         if (other.username != null)
            return false;
      } else if (!username.equals(other.username))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
      result = prime * result + ((accountType == null) ? 0 : accountType.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
      result = prime * result + ((jSessionId == null) ? 0 : jSessionId.hashCode());
      result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + (registered ? 1231 : 1237);
      result = prime * result + ((sessionKey == null) ? 0 : sessionKey.hashCode());
      result = prime * result + (int) (timeout ^ (timeout >>> 32));
      result = prime * result + ((timezone == null) ? 0 : timezone.hashCode());
      result = prime * result + ((timezoneOffset == null) ? 0 : timezoneOffset.hashCode());
      result = prime * result + (int) (userId ^ (userId >>> 32));
      result = prime * result + ((username == null) ? 0 : username.hashCode());
      return result;
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
