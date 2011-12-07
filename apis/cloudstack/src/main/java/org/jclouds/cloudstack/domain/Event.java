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

/**
 * @author Vijay Kiran
 */
public class Event implements Comparable<Event> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String account;
      private String description;
      private Date created;
      private String domain;
      private long domainId;
      //TODO Change to enum : the event level (INFO, WARN, ERROR)
      private String level;
      private String parentId;
      private String state;
      //Event Type
      private String type;
      private String username;

      public Builder id(long id) {
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

      public Builder domainId(long domainId) {
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

   private long id;
   private String account;
   private String description;
   private Date created;
   private String domain;
   private long domainId;
   //TODO Change to enum : the event level (INFO, WARN, ERROR)
   private String level;
   private String parentId;
   private String state;
   //Event Type
   private String type;
   private String username;

   public Event(long id, String account, String description, Date created, String domain, long domainId, String level,
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
   public long getId() {
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
   public long getDomainId() {
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
      return new Long(id).compareTo(arg0.getId());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Event event = (Event) o;

      if (domainId != event.domainId) return false;
      if (id != event.id) return false;
      if (account != null ? !account.equals(event.account) : event.account != null) return false;
      if (created != null ? !created.equals(event.created) : event.created != null) return false;
      if (description != null ? !description.equals(event.description) : event.description != null) return false;
      if (domain != null ? !domain.equals(event.domain) : event.domain != null) return false;
      if (level != null ? !level.equals(event.level) : event.level != null) return false;
      if (parentId != null ? !parentId.equals(event.parentId) : event.parentId != null) return false;
      if (state != null ? !state.equals(event.state) : event.state != null) return false;
      if (type != null ? !type.equals(event.type) : event.type != null) return false;
      if (username != null ? !username.equals(event.username) : event.username != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (account != null ? account.hashCode() : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (created != null ? created.hashCode() : 0);
      result = 31 * result + (domain != null ? domain.hashCode() : 0);
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (level != null ? level.hashCode() : 0);
      result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
      result = 31 * result + (state != null ? state.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      result = 31 * result + (username != null ? username.hashCode() : 0);
      return result;
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
