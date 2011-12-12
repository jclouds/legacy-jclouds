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

import java.util.Date;

import org.jclouds.cloudstack.domain.Account.Type;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class User implements Comparable<User> {

   public static enum State {
      ENABLED,
      DISABLED,
      UNKNOWN;

      public static State fromValue(String value) {
         try {
            return valueOf(value.toUpperCase());
         } catch(IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return name().toLowerCase();
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String name;
      private String firstName;
      private String lastName;
      private String email;
      private Date created;
      private State state;
      private String account;
      private Account.Type accountType;
      private String domain;
      private long domainId;
      private String timeZone;
      private String apiKey;
      private String secretKey;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
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

      public Builder email(String email) {
         this.email = email;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder accountType(Account.Type accountType) {
         this.accountType = accountType;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder timeZone(String timeZone) {
         this.timeZone = timeZone;
         return this;
      }

      public Builder apiKey(String apiKey) {
         this.apiKey = apiKey;
         return this;
      }

      public Builder secretKey(String secretKey) {
         this.secretKey = secretKey;
         return this;
      }

      public User build() {
         return new User(id, name, firstName, lastName, email, created, state, account, accountType, domain, domainId,
               timeZone, apiKey, secretKey);
      }
   }

   /**
    * present only for serializer
    * 
    */
   User() {

   }

   private long id;
   @SerializedName("username")
   private String name;
   @SerializedName("firstname")
   private String firstName;
   @SerializedName("lastname")
   private String lastName;
   private String email;
   private Date created;
   private State state;
   private String account;
   @SerializedName("accounttype")
   private Account.Type accountType;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("timezone")
   private String timeZone;
   @SerializedName("apikey")
   private String apiKey;
   @SerializedName("secretkey")
   private String secretKey;

   public User(long id, String name, String firstname, String lastname, String email, Date created, State state,
         String account, Type accountType, String domain, long domainId, String timeZone, String apiKey,
         String secretKey) {
      this.id = id;
      this.name = name;
      this.firstName = firstname;
      this.lastName = lastname;
      this.email = email;
      this.created = created;
      this.state = state;
      this.account = account;
      this.accountType = accountType;
      this.domain = domain;
      this.domainId = domainId;
      this.timeZone = timeZone;
      this.apiKey = apiKey;
      this.secretKey = secretKey;
   }

   /**
    * 
    * @return the user ID
    */
   public long getId() {
      return id;
   }

   /**
    * 
    * @return the user name
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return the user firstname
    */
   public String getFirstName() {
      return firstName;
   }

   /**
    * 
    * @return the user lastname
    */
   public String getLastName() {
      return lastName;
   }

   /**
    * 
    * @return the user email address
    */
   public String getEmail() {
      return email;
   }

   /**
    * 
    * @return the date and time the user account was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * 
    * @return the user state
    */
   public State getState() {
      return state;
   }

   /**
    * 
    * @return the account name of the user
    */
   public String getAccount() {
      return account;
   }

   /**
    * 
    * @return the account type of the user
    */
   public Account.Type getAccountType() {
      return accountType;
   }

   /**
    * 
    * @return the domain name of the user
    */
   public String getDomain() {
      return domain;
   }

   /**
    * 
    * @return the domain ID of the user
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * 
    * @return the timezone user was created in
    */
   public String getTimeZone() {
      return timeZone;
   }

   /**
    * 
    * @return the api key of the user
    */
   public String getApiKey() {
      return apiKey;
   }

   /**
    * 
    * @return the secret key of the user
    */
   public String getSecretKey() {
      return secretKey;
   }

   @Override
   public int compareTo(User arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      User other = (User) obj;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", created=" + created +
            ", state='" + state + '\'' +
            ", account='" + account + '\'' +
            ", accountType=" + accountType +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", timeZone='" + timeZone + '\'' +
            ", apiKey='" + apiKey + '\'' +
            ", secretKey='" + secretKey + '\'' +
            '}';
   }

}
