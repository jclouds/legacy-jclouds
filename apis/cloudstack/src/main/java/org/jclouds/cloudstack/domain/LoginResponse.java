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

      private String userName;
      private long userId;
      private String password;
      private long domainId;
      private long timeout;
      private String accountName;
      private String firstName;
      private String lastName;
      private Account.Type accountType;
      private String timezone;
      private String timezoneOffset;
      private String sessionKey;
      private String jSessionId;
      
      public Builder copyOf(LoginResponse r) {
         this.userName = r.userName;
         this.userId = r.userId;
         this.password = r.password;
         this.domainId = r.domainId;
         this.timeout = r.timeout;
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

      public Builder userName(String userName) {
         this.userName = userName;
         return this;
      }
      
      public Builder userId(long userId)  {
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
         return new LoginResponse(userName, userId, password, domainId, timeout, accountName,
            firstName, lastName, accountType, timezone, timezoneOffset, sessionKey, jSessionId);
      }
   }

   // for deserialization
   LoginResponse() { }

   @SerializedName("username")
   private String userName;
   @SerializedName("userid")
   private long userId;
   private String password;
   @SerializedName("domainid")
   private long domainId;
   private long timeout;
   @SerializedName("account")
   private String accountName;
   @SerializedName("firstname")
   private String firstName;
   @SerializedName("lastname")
   private String lastName;
   @SerializedName("type")
   private Account.Type accountType;
   private String timezone;
   @SerializedName("timezoneoffset")
   private String timezoneOffset;
   @SerializedName("sessionkey")
   private String sessionKey;
   private String jSessionId;

   public LoginResponse(String userName, long userId, String password, long domainId, long timeout,
                        String accountName, String firstName, String lastName, Account.Type accountType,
                        String timezone, String timezoneOffset, String sessionKey, String jSessionId) {
      this.userName = userName;
      this.userId = userId;
      this.password = password;
      this.domainId = domainId;
      this.timeout = timeout;
      this.accountName = accountName;
      this.firstName = firstName;
      this.lastName = lastName;
      this.accountType = accountType;
      this.timezone = timezone;
      this.timezoneOffset = timezoneOffset;
      this.sessionKey = sessionKey;
      this.jSessionId = jSessionId;
   }

   public String getUserName() {
      return userName;
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

      LoginResponse loginResponse = (LoginResponse) o;

      if (domainId != loginResponse.domainId) return false;
      if (timeout != loginResponse.timeout) return false;
      if (userId != loginResponse.userId) return false;
      if (accountName != null ? !accountName.equals(loginResponse.accountName) : loginResponse.accountName != null) return false;
      if (accountType != loginResponse.accountType) return false;
      if (firstName != null ? !firstName.equals(loginResponse.firstName) : loginResponse.firstName != null) return false;
      if (lastName != null ? !lastName.equals(loginResponse.lastName) : loginResponse.lastName != null) return false;
      if (password != null ? !password.equals(loginResponse.password) : loginResponse.password != null) return false;
      if (sessionKey != null ? !sessionKey.equals(loginResponse.sessionKey) : loginResponse.sessionKey != null) return false;
      if (timezone != null ? !timezone.equals(loginResponse.timezone) : loginResponse.timezone != null) return false;
      if (timezoneOffset != null ? !timezoneOffset.equals(loginResponse.timezoneOffset) : loginResponse.timezoneOffset != null)
         return false;
      if (userName != null ? !userName.equals(loginResponse.userName) : loginResponse.userName != null) return false;
      if (jSessionId != null ? !jSessionId.equals(loginResponse.jSessionId) : loginResponse.jSessionId != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = userName != null ? userName.hashCode() : 0;
      result = 31 * result + (int) (userId ^ (userId >>> 32));
      result = 31 * result + (password != null ? password.hashCode() : 0);
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (int) (timeout ^ (timeout >>> 32));
      result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
      result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
      result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
      result = 31 * result + (accountType != null ? accountType.hashCode() : 0);
      result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
      result = 31 * result + (timezoneOffset != null ? timezoneOffset.hashCode() : 0);
      result = 31 * result + (sessionKey != null ? sessionKey.hashCode() : 0);
      result = 31 * result + (jSessionId != null ? jSessionId.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "LoginResponse{" +
         "userName='" + userName + '\'' +
         ", userId=" + userId +
         ", password='" + password + '\'' +
         ", domainId=" + domainId +
         ", timeout=" + timeout +
         ", accountName='" + accountName + '\'' +
         ", firstName='" + firstName + '\'' +
         ", lastName='" + lastName + '\'' +
         ", accountType=" + accountType +
         ", timezone='" + timezone + '\'' +
         ", timezoneOffset='" + timezoneOffset + '\'' +
         ", sessionKey='" + sessionKey + '\'' +
         ", jSessionId='" + jSessionId + '\'' +
         '}';
   }

   @Override
   public int compareTo(LoginResponse arg0) {
      return sessionKey.compareTo(arg0.getSessionKey());
   }

}
