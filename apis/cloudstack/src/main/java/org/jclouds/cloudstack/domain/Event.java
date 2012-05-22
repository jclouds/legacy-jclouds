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

import java.util.Date;

/**
 * @author Vijay Kiran
 */
public class Event implements Comparable<Event> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String account;
      private String description;
      private Date created;
      private String domain;
      private String domainId;
      //TODO Change to enum : the event level (INFO, WARN, ERROR)
      private String level;
      private String parentId;
      private String state;
      //Event Type
      private String type;
      private String username;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder level(String level) {
         this.level = level;
         return this;
      }

      public Builder parentId(String parentId) {
         this.parentId = parentId;
         return this;
      }

      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public Event build() {
         return new Event(id, account, description, created, domain, domainId, level, parentId, state, type, username);
      }

   }

   private String id;
   private String account;
   private String description;
   private Date created;
   private String domain;
   private String domainId;
   //TODO Change to enum : the event level (INFO, WARN, ERROR)
   private String level;
   private String parentId;
   private String state;
   //Event Type
   private String type;
   private String username;

   public Event(String id, String account, String description, Date created, String domain, String domainId, String level,
                String parentId, String state, String type, String username) {
      this.id = id;
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
    * present only for serializer
    */
   Event() {

   }

   /**
    * @return the ID of the event
    */
   public String getId() {
      return id;
   }

   /**
    * @return the account name for the account that owns the object being acted on in the event
    *         (e.g. the owner of the virtual machine, ip address, or security group)
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the date the event was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return the description of the event
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return the name of the account's domain
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the id of the account's domain
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return the event level (INFO, WARN, ERROR)
    */
   public String getLevel() {
      return level;
   }

   /**
    * @return whether the event is parented
    */
   public String getParentId() {
      return parentId;
   }

   /**
    * @return the state of the event
    */
   public String getState() {
      return state;
   }

   /**
    * @return the type of the event (see event types)
    */
   public String getType() {
      return type;
   }

   /**
    * @return the name of the user who performed the action (can be different from the account if
    *         an admin is performing an action for a user, e.g. starting/stopping a user's virtual machine)
    */
   public String getUsername() {
      return username;
   }

   @Override
   public int compareTo(Event arg0) {
      return id.compareTo(arg0.getId());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Event that = (Event) o;

      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(description, that.description)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(level, that.level)) return false;
      if (!Objects.equal(parentId, that.parentId)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(type, that.type)) return false;
      if (!Objects.equal(username, that.username)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, account, description, created, domain, domainId, level, parentId, state, type, username);
   }

   @Override
   public String toString() {
      return "Event{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", description='" + description + '\'' +
            ", created=" + created +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", level='" + level + '\'' +
            ", parentId='" + parentId + '\'' +
            ", state='" + state + '\'' +
            ", type='" + type + '\'' +
            ", username='" + username + '\'' +
            '}';
   }


}
