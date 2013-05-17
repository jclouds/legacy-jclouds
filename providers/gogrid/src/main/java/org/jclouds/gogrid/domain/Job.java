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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Longs;

/**
 * Represents any job in GoGrid system
 * (jobs include server creation, stopping, etc)
 *
 * @author Oleksiy Yarmula
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API:Job_(Object)" />
 */
public class Job implements Comparable<Job> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromJob(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected long id;
      protected Option command;
      protected ObjectType objectType;
      protected Date createdOn;
      protected Date lastUpdatedOn;
      protected JobState currentState;
      protected int attempts;
      protected String owner;
      protected Set<JobProperties> history = ImmutableSet.of();
      protected Map<String, String> details = ImmutableMap.of();

      /**
       * @see Job#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /**
       * @see Job#getCommand()
       */
      public T command(Option command) {
         this.command = command;
         return self();
      }

      /**
       * @see Job#getObjectType()
       */
      public T objectType(ObjectType objectType) {
         this.objectType = objectType;
         return self();
      }

      /**
       * @see Job#getCreatedOn()
       */
      public T createdOn(Date createdOn) {
         this.createdOn = createdOn;
         return self();
      }

      /**
       * @see Job#getLastUpdatedOn()
       */
      public T lastUpdatedOn(Date lastUpdatedOn) {
         this.lastUpdatedOn = lastUpdatedOn;
         return self();
      }

      /**
       * @see Job#getCurrentState()
       */
      public T currentState(JobState currentState) {
         this.currentState = currentState;
         return self();
      }

      /**
       * @see Job#getAttempts()
       */
      public T attempts(int attempts) {
         this.attempts = attempts;
         return self();
      }

      /**
       * @see Job#getOwner()
       */
      public T owner(String owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see Job#getHistory()
       */
      public T history(Set<JobProperties> history) {
         this.history = ImmutableSet.copyOf(checkNotNull(history, "history"));
         return self();
      }

      public T history(JobProperties... in) {
         return history(ImmutableSet.copyOf(in));
      }

      /**
       * @see Job#getDetails()
       */
      public T details(Map<String, String> details) {
         this.details = ImmutableMap.copyOf(checkNotNull(details, "details"));
         return self();
      }

      public Job build() {
         return new Job(id, command, objectType, createdOn, lastUpdatedOn, currentState, attempts, owner, history, details);
      }

      public T fromJob(Job in) {
         return this
               .id(in.getId())
               .command(in.getCommand())
               .objectType(in.getObjectType())
               .createdOn(in.getCreatedOn())
               .lastUpdatedOn(in.getLastUpdatedOn())
               .currentState(in.getCurrentState())
               .attempts(in.getAttempts())
               .owner(in.getOwner())
               .history(in.getHistory())
               .details(in.getDetails());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long id;
   private final Option command;
   private final ObjectType objectType;
   private final Date createdOn;
   private final Date lastUpdatedOn;
   private final JobState currentState;
   private final int attempts;
   private final String owner;
   private final Set<JobProperties> history;
   private final Map<String, String> details;

   /* NOTE: as of Feb 28, 10, there is a contradiction b/w the name in  documentation (details) and actual param name (detail)*/
   @ConstructorProperties({
         "id", "command", "objecttype", "createdon", "lastupdatedon", "currentstate", "attempts", "owner", "history", "detail"
   })
   protected Job(long id, Option command, ObjectType objectType, Date createdOn, @Nullable Date lastUpdatedOn,
                 JobState currentState, int attempts, String owner, Set<JobProperties> history, Map<String, String> details) {
      this.id = id;
      this.command = checkNotNull(command, "command");
      this.objectType = checkNotNull(objectType, "objectType");
      this.createdOn = checkNotNull(createdOn, "createdOn");
      this.lastUpdatedOn = lastUpdatedOn;
      this.currentState = checkNotNull(currentState, "currentState");
      this.attempts = attempts;
      this.owner = checkNotNull(owner, "owner");
      this.history = ImmutableSet.copyOf(checkNotNull(history, "history"));
      this.details = ImmutableMap.copyOf(checkNotNull(details, "details"));
   }

   public long getId() {
      return this.id;
   }

   public Option getCommand() {
      return this.command;
   }

   public ObjectType getObjectType() {
      return this.objectType;
   }

   public Date getCreatedOn() {
      return this.createdOn;
   }

   @Nullable
   public Date getLastUpdatedOn() {
      return this.lastUpdatedOn;
   }

   public JobState getCurrentState() {
      return this.currentState;
   }

   public int getAttempts() {
      return this.attempts;
   }

   public String getOwner() {
      return this.owner;
   }

   public Set<JobProperties> getHistory() {
      return this.history;
   }

   public Map<String, String> getDetails() {
      return this.details;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, command, objectType, createdOn, lastUpdatedOn, currentState, attempts, owner, history, details);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Job that = Job.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.command, that.command)
            && Objects.equal(this.objectType, that.objectType)
            && Objects.equal(this.createdOn, that.createdOn)
            && Objects.equal(this.lastUpdatedOn, that.lastUpdatedOn)
            && Objects.equal(this.currentState, that.currentState)
            && Objects.equal(this.attempts, that.attempts)
            && Objects.equal(this.owner, that.owner)
            && Objects.equal(this.history, that.history)
            && Objects.equal(this.details, that.details);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("command", command).add("objectType", objectType).add("createdOn", createdOn).add("lastUpdatedOn", lastUpdatedOn).add("currentState", currentState).add("attempts", attempts).add("owner", owner).add("history", history).add("details", details);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Job o) {
      if(createdOn != null && o.getCreatedOn() != null)
         return Longs.compare(createdOn.getTime(), o.getCreatedOn().getTime());
      return Longs.compare(id, o.getId());
   }

}
