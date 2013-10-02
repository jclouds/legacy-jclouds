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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class User
 *
 * @author Adrian Cole
 */
public class User {

   /**
    */
   public static enum State {
      ENABLED,
      DISABLED,
      UNKNOWN;

      public static State fromValue(String value) {
         try {
            return valueOf(value.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return name().toLowerCase();
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUser(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String firstName;
      protected String lastName;
      protected String email;
      protected Date created;
      protected User.State state;
      protected String account;
      protected Account.Type accountType;
      protected String domain;
      protected String domainId;
      protected String timeZone;
      protected String apiKey;
      protected String secretKey;

      /**
       * @see User#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see User#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see User#getFirstName()
       */
      public T firstName(String firstName) {
         this.firstName = firstName;
         return self();
      }

      /**
       * @see User#getLastName()
       */
      public T lastName(String lastName) {
         this.lastName = lastName;
         return self();
      }

      /**
       * @see User#getEmail()
       */
      public T email(String email) {
         this.email = email;
         return self();
      }

      /**
       * @see User#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see User#getState()
       */
      public T state(User.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see User#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see User#getAccountType()
       */
      public T accountType(Account.Type accountType) {
         this.accountType = accountType;
         return self();
      }

      /**
       * @see User#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see User#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see User#getTimeZone()
       */
      public T timeZone(String timeZone) {
         this.timeZone = timeZone;
         return self();
      }

      /**
       * @see User#getApiKey()
       */
      public T apiKey(String apiKey) {
         this.apiKey = apiKey;
         return self();
      }

      /**
       * @see User#getSecretKey()
       */
      public T secretKey(String secretKey) {
         this.secretKey = secretKey;
         return self();
      }

      public User build() {
         return new User(id, name, firstName, lastName, email, created, state, account, accountType, domain, domainId, timeZone, apiKey, secretKey);
      }

      public T fromUser(User in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .firstName(in.getFirstName())
               .lastName(in.getLastName())
               .email(in.getEmail())
               .created(in.getCreated())
               .state(in.getState())
               .account(in.getAccount())
               .accountType(in.getAccountType())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .timeZone(in.getTimeZone())
               .apiKey(in.getApiKey())
               .secretKey(in.getSecretKey());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final String firstName;
   private final String lastName;
   private final String email;
   private final Date created;
   private final User.State state;
   private final String account;
   private final Account.Type accountType;
   private final String domain;
   private final String domainId;
   private final String timeZone;
   private final String apiKey;
   private final String secretKey;

   @ConstructorProperties({
         "id", "username", "firstname", "lastname", "email", "created", "state", "account", "accounttype", "domain",
         "domainid", "timezone", "apikey", "secretkey"
   })
   protected User(String id, @Nullable String name, @Nullable String firstName, @Nullable String lastName,
                  @Nullable String email, @Nullable Date created, @Nullable User.State state, @Nullable String account,
                  @Nullable Account.Type accountType, @Nullable String domain, @Nullable String domainId, @Nullable String timeZone,
                  @Nullable String apiKey, @Nullable String secretKey) {
      this.id = checkNotNull(id, "id");
      this.name = name;
      this.firstName = firstName;
      this.lastName = lastName;
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
    * @return the user ID
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the user name
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the user firstname
    */
   @Nullable
   public String getFirstName() {
      return this.firstName;
   }

   /**
    * @return the user lastname
    */
   @Nullable
   public String getLastName() {
      return this.lastName;
   }

   /**
    * @return the user email address
    */
   @Nullable
   public String getEmail() {
      return this.email;
   }

   /**
    * @return the date and time the user account was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the user state
    */
   @Nullable
   public User.State getState() {
      return this.state;
   }

   /**
    * @return the account name of the user
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the account type of the user
    */
   @Nullable
   public Account.Type getAccountType() {
      return this.accountType;
   }

   /**
    * @return the domain name of the user
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the domain ID of the user
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the timezone user was created in
    */
   @Nullable
   public String getTimeZone() {
      return this.timeZone;
   }

   /**
    * @return the api key of the user
    */
   @Nullable
   public String getApiKey() {
      return this.apiKey;
   }

   /**
    * @return the secret key of the user
    */
   @Nullable
   public String getSecretKey() {
      return this.secretKey;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, firstName, lastName, email, created, state, account, accountType, domain, domainId, timeZone, apiKey, secretKey);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      User that = User.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.firstName, that.firstName)
            && Objects.equal(this.lastName, that.lastName)
            && Objects.equal(this.email, that.email)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.accountType, that.accountType)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.timeZone, that.timeZone)
            && Objects.equal(this.apiKey, that.apiKey)
            && Objects.equal(this.secretKey, that.secretKey);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("firstName", firstName).add("lastName", lastName).add("email", email)
            .add("created", created).add("state", state).add("account", account).add("accountType", accountType).add("domain", domain)
            .add("domainId", domainId).add("timeZone", timeZone).add("apiKey", apiKey).add("secretKey", secretKey);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
