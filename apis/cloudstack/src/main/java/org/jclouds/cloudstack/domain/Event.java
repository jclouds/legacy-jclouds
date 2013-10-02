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
 * Class Event
 *
 * @author Vijay Kiran
 */
public class Event implements Comparable<Event> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromEvent(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected String description;
      protected Date created;
      protected String domain;
      protected String domainId;
      protected String level;
      protected String parentId;
      protected String state;
      protected String type;
      protected String username;

      /**
       * @see Event#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Event#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see Event#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see Event#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see Event#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Event#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Event#getLevel()
       */
      public T level(String level) {
         this.level = level;
         return self();
      }

      /**
       * @see Event#getParentId()
       */
      public T parentId(String parentId) {
         this.parentId = parentId;
         return self();
      }

      /**
       * @see Event#getState()
       */
      public T state(String state) {
         this.state = state;
         return self();
      }

      /**
       * @see Event#getType()
       */
      public T type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see Event#getUsername()
       */
      public T username(String username) {
         this.username = username;
         return self();
      }

      public Event build() {
         return new Event(id, account, description, created, domain, domainId, level, parentId, state, type, username);
      }

      public T fromEvent(Event in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .description(in.getDescription())
               .created(in.getCreated())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .level(in.getLevel())
               .parentId(in.getParentId())
               .state(in.getState())
               .type(in.getType())
               .username(in.getUsername());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final String description;
   private final Date created;
   private final String domain;
   private final String domainId;
   private final String level;
   private final String parentId;
   private final String state;
   private final String type;
   private final String username;

   @ConstructorProperties({
         "id", "account", "description", "created", "domain", "domainId", "level", "parentId", "state", "type", "username"
   })
   protected Event(String id, @Nullable String account, @Nullable String description, @Nullable Date created,
                   @Nullable String domain, @Nullable String domainId, @Nullable String level, @Nullable String parentId,
                   @Nullable String state, @Nullable String type, @Nullable String username) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.description = description;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.level = level;
      this.parentId = parentId;
      this.state = state;
      this.type = type;
      this.username = username;
   }

   /**
    * @return the ID of the event
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account name for the account that owns the object being acted on in the event
    *         (e.g. the owner of the virtual machine, ip address, or security group)
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the description of the event
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return the date the event was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the name of the account's domain
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the id of the account's domain
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the event level (INFO, WARN, ERROR)
    */
   @Nullable
   public String getLevel() {
      return this.level;
   }

   /**
    * @return whether the event is parented
    */
   @Nullable
   public String getParentId() {
      return this.parentId;
   }

   /**
    * @return the state of the event
    */
   @Nullable
   public String getState() {
      return this.state;
   }

   /**
    * @return the type of the event (see event types)
    */
   @Nullable
   public String getType() {
      return this.type;
   }

   /**
    * @return the name of the user who performed the action (can be different from the account if
    *         an admin is performing an action for a user, e.g. starting/stopping a user's virtual machine)
    */
   @Nullable
   public String getUsername() {
      return this.username;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, description, created, domain, domainId, level, parentId, state, type, username);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Event that = Event.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.level, that.level)
            && Objects.equal(this.parentId, that.parentId)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.username, that.username);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("description", description).add("created", created)
            .add("domain", domain).add("domainId", domainId).add("level", level).add("parentId", parentId)
            .add("state", state).add("type", type).add("username", username);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Event other) {
      return id.compareTo(other.getId());
   }

}
